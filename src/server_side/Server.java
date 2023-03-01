package server_side;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread{
    public static String f = "res/users.xml";
    static Socket clientSocket;
    public static ArrayList<ClientHandler> loginHandlerArraylist = new ArrayList<>();
    public static HashMap<ClientHandler, User> loggedInUserHashMap = new HashMap<>();
    public static int port;
    static Scanner scanner = new Scanner(System.in);
    static ServerSocket serverSocket;
    public List<User> clients;
    private List<ClientHandler> clientsList;
    static XMLParse xmlParse = new XMLParse("res/messages.xml");
    public static List<User> registeredUsersList = new ArrayList<>(xmlParse.getUserList());


    public Server(int port){
        this.port = port;

        //arraylist ng mgauser
        this.clients = new ArrayList<>();
        this.run();
    }
    public Server(){
    }

    public void addUser(User user){
        clients.add(user);
        //debug statment
        System.out.println(user+ " has been added to ze list of ze users");
    }
    public static List<String> getRegisteredUserNames() {
        List<String> userNames = new ArrayList<>();
        for (User user : registeredUsersList) {
            userNames.add(user.getName());
        }
        return userNames;
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
        clientsList = new ArrayList<>();

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JOptionPane.showMessageDialog(new JPanel(), "Server created at port: " + port);

        ExecutorService executorService = Executors.newFixedThreadPool(10);

            try {
                while (true) {
                    try {
                        clientSocket = serverSocket.accept();
                        // Create an instance of ObjectOutputStream to write the message object to the client
                        ObjectOutputStream outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
                        ClientHandler clientHandler = new ClientHandler(this, clientSocket, outToClient);
                        executorService.execute(clientHandler);
                        clientsList.add(clientHandler);
                    }catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            } catch (RuntimeException e) {
                e.printStackTrace();
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
            if (loggedInUserHashMap.get(client).getName().equals(recipient)) {
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
        //get yung date, para sa offline message
        LocalDateTime timeSent = LocalDateTime.now();
        xmlParse.addMessage(message.getSender(), message.getContent(), message.getRecipient(), timeSent);
        System.err.println("User: " + recipient +" is offline.... message will be written sa xml file :)");
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

//    public static Document getRegisteredUsers() {
//        NodeList users;
//        Document document;
//        String id, name, age, username, password, status, banStatus;
//        try {
//            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//            document = documentBuilder.parse(f);
//
//            users = document.getElementsByTagName("User");
//
//            for (int i = 0; i < users.getLength(); i++) {
//                Element element = (Element) users.item(i);
//                id = element.getAttribute("User");
//                name = element.getElementsByTagName("name").item(0).getTextContent();
//                age = element.getElementsByTagName("Age").item(0).getTextContent();
//                username = element.getElementsByTagName("Username").item(0).getTextContent();
//                password = element.getElementsByTagName("Password").item(0).getTextContent();
//                status = element.getElementsByTagName("status").item(0).getTextContent();
//
//                //bigla nag eerror dituy idk why, lagay ko muna ito
//                try{
//                    banStatus = element.getElementsByTagName("BanStatus").item(0).getTextContent();
//                }
//                catch(NullPointerException e){
//                    banStatus = "x";
//                }
//                registeredUsersList.add(new User(id, name, age, username, password, status, banStatus));
//
//
//            }
//        } catch (ParserConfigurationException | IOException | SAXException e) {
//            throw new RuntimeException(e);
//        }
//        return document;
//    }


}
