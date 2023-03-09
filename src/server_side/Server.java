package server_side;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import shared_classes.*;

import javax.print.Doc;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread{
    public static String f = "res/users.xml";
    private HashMap<String, List<ClientHandler>> groupsMap;
    static Socket clientSocket;
    public static ArrayList<ClientHandler> loginHandlerArraylist = new ArrayList<>();
    public static HashMap<ClientHandler, User> loggedInUserHashMap = new HashMap<>();
    public static int port;
    static Scanner scanner = new Scanner(System.in);
    public static ServerSocket serverSocket;
    private static ArrayList<String> userNames;
    public List<User> clients;
    private List<ClientHandler> clientsList;
    private List<Group> groups = new ArrayList<>();
    public Set<String> groupNames;
    static XMLParse xmlParse = new XMLParse("res/messages.xml");
    public static List<User> registeredUsersList = new ArrayList<>(XMLParse.getUserList());
    JFrame frame;


    public Server(int port, JFrame frame){
        this.port = port;
        this.frame = frame;

        //arraylist ng mgauser
        this.clients = new ArrayList<>();
        this.run();

        try {
            loadGroupsFromXml();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Server(){
    }

    public void addUser(User user){
        clients.add(user);
        //debug statment
        System.out.println(user+ " has been added to ze list of ze users");
    }
    public static List<String> getRegisteredUserNames() {
        userNames = new ArrayList<>();
        for (User user : registeredUsersList) {
            userNames.add(user.getName());
        }
        return userNames;
    }

    private void loadGroupsFromXml() throws Exception {
        File file = new File("res/users.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("Group");
        groupNames = new HashSet<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String groupName = element.getTextContent().trim();
                groupNames.add(groupName);
            }
        }

        for (String groupName : groupNames) {
            groups.add(new Group(groupName));
        }
    }


    public static List<User> getRegisteredUsersList() {
        return registeredUsersList;
    }

    /**
     This method creates a server socket and listens to incoming connections.
     It prompts the user to input a valid port number, and creates a new ServerSocket on that port.
     Then, it creates a new ClientHandler thread for each incoming client connection.
     */
    @Override
    public void run() {
        groupsMap = new HashMap<>();
        clientsList = new ArrayList<>();
        boolean validPort = false;
        auth:
        while(!validPort){
            try{
                port = Integer.parseInt(JOptionPane.showInputDialog(frame, "Input port", 0));
                serverSocket = new ServerSocket(port);
                validPort = true;
                UserManagement_GUI.portNumber.setText("Port: " + port);
            }
            catch(RuntimeException e){
                System.out.println(e.getMessage());
            }
            catch(BindException e){
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(frame, "PORT IS ALREADY IN USE, INPUT ANOTHER PORT");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        JOptionPane.showMessageDialog(frame, "Server created at port: "+port);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        populateGroupsMap();
        try {
            while (true) {
                try {
                    clientSocket = serverSocket.accept();
                    // Create an instance of ObjectOutputStream to write the message object to the client
                    getRegisteredUsers();
                    ObjectOutputStream outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
                    ClientHandler clientHandler = new ClientHandler(this, clientSocket, outToClient);
                    //outToClient.writeObject(new File("res/users.xml"));
                    executorService.execute(clientHandler);
                    clientsList.add(clientHandler);
                }catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }//end run method

    private void populateGroupsMap() {
        groupsMap = new HashMap<>();
        for (Group group : groups) {
            String groupName = group.getName();
            List<ClientHandler> members = new ArrayList<>();
            for (ClientHandler clientHandler : clientsList) {
                User user = loggedInUserHashMap.get(clientHandler);
                if (user != null && group.getMembers().contains(user.getName())) {
                    members.add(clientHandler);
                }
            }
            groupsMap.put(groupName, members);
        }
    }
    public void groupMessage(Message message, String groupName) {
        System.out.println("GROUP MESSAGE");
        ObjectOutputStream outToRecipient;
        for (ClientHandler client : loginHandlerArraylist) {
            /*System.out.println("A "+loggedInUserHashMap.get(client).isMember(groupName));
            System.out.println("B "+loggedInUserHashMap.get(client).getGroups());
            System.out.println("C "+getGroupByName(groupName));*/
            if (loggedInUserHashMap.get(client).isMember(groupName)) {
                outToRecipient = client.outToClient;
                try {
                    outToRecipient.writeObject(message);
                    outToRecipient.flush();
                } catch (IOException e) {
                    System.err.println("Error sending message to client: " + e);
                }
                return;
            }
        }
    }
    public void broadcastMessage(Message message) {
        for (ClientHandler client : clientsList) {
            try {
                client.outToClient.writeObject(message);
                client.outToClient.flush();
            } catch (IOException e) {
                System.err.println("Error sending message to client: " + e);
            }
        }
    }
    public void privateMessage(String recipient, Message message) {
        ObjectOutputStream outToRecipient;
        for (ClientHandler client : loginHandlerArraylist) {
            if (loggedInUserHashMap.get(client).getName().equalsIgnoreCase(recipient)) {
                outToRecipient = client.outToClient;
                try {
                    outToRecipient.writeObject(message);
                    outToRecipient.flush();
                } catch (IOException e) {
                    System.err.println("Error sending message to client: " + e);
                }
                return;
            }
        }

        //check muna if recipient is actually a user
        if(getRegisteredUserNames().contains(message.getRecipient())){
            //get yung date, para sa offline message
            LocalDateTime timeSent = LocalDateTime.now();
            xmlParse.addMessage(message.getSender(), message.getContent(), message.getRecipient(), timeSent);
            System.err.println("User: " + recipient +" is offline.... message will be written sa xml file :)");
        }
    }
    public void offlineMessage(String recipient, List<OfflineMessage> offlineMessages) {
        ObjectOutputStream outToRecipient;
        for (ClientHandler client : loginHandlerArraylist) {
            if (loggedInUserHashMap.get(client).getName().equals(recipient)) {
                outToRecipient = client.outToClient;

                for(int i = 0; i<offlineMessages.size();i++){
                    try {
                        outToRecipient.writeObject(offlineMessages);
                        outToRecipient.flush();
                    } catch (IOException e) {
                        System.err.println("Error sending message to client: " + e);
                    }
                    return;
                }
            }
        }
    }

    /**
     This method updates the XML file with new user information.
     @param users the NodeList containing the user information.
     @param document the Document object representing the XML file.
     */
    public static void updateXML(NodeList users, Document document) {
        users = document.getElementsByTagName("Users");
        Element element = (Element) users.item(0);
        XMLParse.trimWhiteSpace(element);

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(f));
            transformer.transform(domSource, streamResult);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     This method shuts down the server by closing all sockets and streams and exiting the program.
     */
    /*public static void shutdown() {
        try {
            bufferedReader.close();
            printWriter.close();
            clientSocket.close();
            serverSocket.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public static void updateUsersList() {
        registeredUsersList = new ArrayList<>(XMLParse.getUserList());
    }

    public static ArrayList<User> getUsersByGroupName(String groupName) {
        ArrayList<User> users = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse("res/users.xml");
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("User");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                NodeList groupList = element.getElementsByTagName("Groupname");
                for (int j = 0; j < groupList.getLength(); j++) {
                    Element group = (Element) groupList.item(j);
                    if (group.getTextContent().equals(groupName)) {
                        String id = element.getAttribute("id");
                        String name = element.getElementsByTagName("name").item(0).getTextContent();
                        String age = element.getElementsByTagName("Age").item(0).getTextContent();
                        String username = element.getElementsByTagName("Username").item(0).getTextContent();
                        String password = element.getElementsByTagName("Password").item(0).getTextContent();
                        String status = element.getElementsByTagName("status").item(0).getTextContent();
                        //User user = new User(id, name, age, username, password, status, "x");
                        //users.add(user);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public static Document getRegisteredUsers() {
        NodeList users;
        Document document;
        String id, name, age, username, password, status, banStatus;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(f);

            users = document.getElementsByTagName("User");

            for (int i = 0; i < users.getLength(); i++) {
                Element element = (Element) users.item(i);
                id = element.getAttribute("User");
                name = element.getElementsByTagName("name").item(0).getTextContent();
                age = element.getElementsByTagName("Age").item(0).getTextContent();
                username = element.getElementsByTagName("Username").item(0).getTextContent();
                password = element.getElementsByTagName("Password").item(0).getTextContent();
                status = element.getElementsByTagName("status").item(0).getTextContent();

                //bigla nag eerror dituy idk why, lagay ko muna ito
                try{
                    banStatus = element.getElementsByTagName("BanStatus").item(0).getTextContent();
                }
                catch(NullPointerException e){
                    banStatus = "x";
                }
                registeredUsersList.add(new User(id, name, age, username, password, status, banStatus));


            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
        return document;
    }


    public Group getGroupByName(String groupName) {
        for (Group group : groups) {
            if (group.getName().equals(groupName)) {
                return group;
            }
        }
        return null;
    }
}
