package server_side;

import gui_classes.clientside.AddKickMemberFromGroup;
import gui_classes.clientside.GUIClientController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import shared_classes.*;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import static server_side.Server.*;

/**
 * It handles the client's messages and sends it to the server
 */
public class ClientHandler implements Runnable {

    public Socket clientSocket;
    public ObjectInputStream userInput = null;
    private Server server;
    private User user;
    public ObjectOutputStream outToClient = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ObjectOutputStream getOutToClient() {
        return outToClient;
    }

    public ClientHandler(Server s, Socket clientSocket, ObjectOutputStream outToClient) {
        try {
            this.server = s;
            this.outToClient = outToClient;
            this.clientSocket = clientSocket;
            this.userInput = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * It handles the messages sent by the client
     */
    public void run() {
        try {
            while (userInput != null) {
                Object obj = new Object();
                try {
                    obj = userInput.readObject();
                } catch (EOFException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }


                if (obj.getClass().equals(Message.class)) {
                    Message message = (Message) obj;

                    if(message.getRecipient() == null){
                        return;
                    }

                    //broadcast msg
                    else if (message.getRecipient().equals("TOALL")) {
                        server.broadcastMessage(message);
                    }

                    //group msg
                    else if(message.getRecipient().startsWith("@")){
                        server.groupMessage(message.getRecipient().replace("@",""), message);
                    }

                    //private msg
                    else {
                        server.privateMessage(message.getRecipient(), message);
                    }
                    //outToClient.writeObject(message);
                }

                // Update status of users
                else if (obj instanceof ArrayList<?> arrayList) {
                    ArrayList<String> strings= (ArrayList<String>) arrayList;
                    server.updateUserFrameList(strings);

                }

                else if(obj instanceof Group group){
                    XMLParse.addGroup(group);
                    server.updateGroupsFrame(group);
                    //debug
                    server.privateMessage(group.getAdmin().getName(), new Message("GRP CREATED!"));
                }

                else if (obj instanceof String s) {
                    if (s.contains("/leavegroup")) {
                        String [] x = s.split(" ");
                        XMLParse.removeUserFromGroup(x[2], x[1]);
                        outToClient.writeObject(new JOptionPane(x[2] + " have successfully removed from the group " + x[1]));
                    }
                }

                else if (obj instanceof LoginCredentials loginCredentials) {
                    try {
                        boolean loginStatus = false;
                        Server.updateUsersList();
                        for (User user : registeredUsersList) {
                            if (user.getUsername().equals(loginCredentials.getUsername())) {
                                if (user.getPassword().equals(loginCredentials.getPassword())) {
                                    if (user.getStatus().equals("online")) {
                                        loginStatus = true;
                                        outToClient.writeObject(new JOptionPane("User is currently logged in on another device."));
                                        break;
                                    } else if (user.getBanStatus().equals("BANNED")) {
                                        loginStatus = true;
                                        outToClient.writeObject(new JOptionPane("User is currently banned from the system."));
                                        break;
                                    } else {
                                        if (XMLParse.loginAuth(loginCredentials.getUsername(), loginCredentials.getPassword())  ) {
                                            loginStatus = true;
                                            outToClient.writeObject(user);
                                            outToClient.flush();
                                            server.clients.add(user);
                                            loginHandlerArraylist.add(this);
                                            loggedInUserHashMap.put(this, user);
                                            setUser(user);
                                            //send offline messages to user
                                            List<OfflineMessage> offlineMessages = getOfflineMessages(user);
                                            server.offlineMessage(user.getName(), offlineMessages);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if (!loginStatus){
                            outToClient.writeObject(new JOptionPane("Invalid Username or password.", JOptionPane.ERROR_MESSAGE));
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e);
        } catch (ClassNotFoundException e) {
            System.err.println("Invalid message received: " + e);
        } finally {
            try {
                userInput.close();
                clientSocket.close();
//                XMLParse.setEveryoneOffline();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void replaceContent(File f, File file) throws IOException {
        FileInputStream in = new FileInputStream(f);
        FileOutputStream out = new FileOutputStream(file);
        try {


            int c;

            while ((c = in.read()) !=-1) {
                out.write(c);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (in!=null) {
                in.close();
            } if (out!=null) {
                out.close();
            }
        }
        System.out.println("file copied");
    }

    /**
     * It reads the XML file, finds all the messages that are addressed to the user, creates a list of OfflineMessage
     * objects from them, and then deletes the messages from the XML file
     *
     * @param user The user object of the user who is logging in.
     * @return A list of offline messages
     */
    public List<OfflineMessage> getOfflineMessages(User user) {
        List<OfflineMessage> offlineMessages = new ArrayList<>();
        try {
            File inputFile = new File("res/messages.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("Message");

            // Create a list to hold the messages to be deleted
            List<Node> messagesToDelete = new ArrayList<>();

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String messageRecipient = eElement.getElementsByTagName("Recipient").item(0).getTextContent();
                    if (messageRecipient.equalsIgnoreCase(user.getName())) {
                        String sender = eElement.getElementsByTagName("Sender").item(0).getTextContent();
                        String messageText = eElement.getElementsByTagName("Text").item(0).getTextContent();
                        String timestamp = eElement.getElementsByTagName("Time").item(0).getTextContent();
                        OfflineMessage message = new OfflineMessage(sender, user.getName(), messageText, timestamp);
                        offlineMessages.add(message);
                        // Add the message element to the list of messages to be deleted
                        messagesToDelete.add(eElement);
                    }
                }
            }

            // Delete the message elements from the XML file
            for (Node messageNode : messagesToDelete) {
                messageNode.getParentNode().removeChild(messageNode);
            }

            // Save the changes to the XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(inputFile);
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return offlineMessages;
    }
}