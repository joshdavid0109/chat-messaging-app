package server_side;

import gui_classes.clientside.GUIClientController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import shared_classes.*;

import javax.print.Doc;
import javax.swing.event.DocumentEvent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.Objects;

import static server_side.Server.*;

/**
 * The type Client handler.
 */
public class ClientHandler implements Runnable {

    public Socket clientSocket;
    public PrintWriter printWriter = null;
    public BufferedReader bufferedReader = null;
    public ObjectInputStream userInput = null;
    static File usersFile = new File("res/users.xml");
    XMLParse xmlParse = new XMLParse("res/messages.xml");

    private List<String> groups = new ArrayList<>();
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
        this.server = s;
        this.outToClient = outToClient;
        this.clientSocket = clientSocket;
        try {
            this.userInput = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //listen to messages from client
    public void run() {
        try {
            while (userInput != null) {
                Object obj = new Object();
                try{
                    obj = userInput.readObject();
                }
                catch (EOFException e){
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }


                if (obj.getClass().equals(Message.class)) {//ganito muna, kasi if (obj instanceof message) yung nakalagay, pati subclasses nun (like OfflineMessage) ay kasama
                    Message message = (Message) obj;
                    System.out.println("SENDER: "+message.getSender()+" MESSAGE: "+message.getContent()+" RECIPIENT: "+message.getRecipient());
                    if(message.getRecipient() == null){
                        server.broadcastMessage(message);
                    }
                    else if(message.getRecipient().equals("TOALL")){
                        server.broadcastMessage(message);
                    }
                    else if(message.getRecipient().startsWith("@")){
                        System.out.println("IM HERE GROUP");
                        String[] words = message.getRecipient().split("@");
                        String groupName = words[1];
                        System.out.println(groupName);
                        Group group = server.getGroupByName(groupName);
                        System.out.println("THISSSSS: "+group.getName()+" WITH MEMBERS "+group.getMembers());
                        if (group != null) {
                            server.groupMessage(message, group.getName());
                            System.out.println("IT WORKYYYYYY");
                        }
                        else {
                            server.privateMessage(message.getSender(), new Message("GROUP DOESN'T EXIST FOO"));
                        }
                    }
                    else{
                        server.privateMessage(message.getRecipient(), message);
                    }
                    //outToClient.writeObject(message);
                } else if (obj instanceof LoginCredentials loginCredentials) {

                    DocumentBuilderFactory documentBuilderFactory = null;
                    DocumentBuilder documentBuilder = null;
                    Document document = null;
                    NodeList nodelist = null;

                    try {
                        documentBuilderFactory = DocumentBuilderFactory.newInstance();
                        documentBuilder = documentBuilderFactory.newDocumentBuilder();
                        document = documentBuilder.parse("res/users.xml");
                        nodelist = document.getElementsByTagName("User");

                    Element element;
//                    getRegisteredUsers();

                    for (User user: registeredUsersList) {
                        if (user.getUsername().equals(loginCredentials.getUsername())) {
                            if (user.getPassword().equals(loginCredentials.getPassword())) {
                                for(int i =0; i < nodelist.getLength();i++) {
                                    element = (Element) nodelist.item(i);
                                    String uname = element.getElementsByTagName("Username").item(0).getTextContent();
                                    String pass = element.getElementsByTagName("Password").item(0).getTextContent();
                                    if (uname.equals(user.getUsername()) && pass.equals(user.getPassword())) {
                                        element.getElementsByTagName("status").item(0).setTextContent("online");
                                        Server.updateXML(nodelist, document);
                                        break;
                                    }
                                }
                                outToClient.writeObject(user);
                                outToClient.flush();
                                server.clients.add(user);
                                loginHandlerArraylist.add(this);
                                loggedInUserHashMap.put(this, user);
                                setUser(user);
                                //send offline messages to user
                                List<OfflineMessage> offlineMessages  = getOfflineMessages(user);
                                server.offlineMessage(user.getName(), offlineMessages);
                                System.out.println("GRUP "+user.getGroups());
                            }
                        }
                    }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                } // TODO: 04/03/2023 RECEIVE XML FILE FROM CLIENT THEN PARSE TO CURRENT XML FILE (PAG MAGKAIBANG MACHINE GAMIT)
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e);
        } catch (ClassNotFoundException e) {
            System.err.println("Invalid message received: " + e);
        } finally {
            try {
                userInput.close();
                clientSocket.close();
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse("res/users.xml");

                NodeList users = document.getElementsByTagName("User");

                for (int i = 0; i < users.getLength(); i++) {
                    Element element = (Element) users.item(i);

                    element.getElementsByTagName("status").item(0).setTextContent("offline");

                    Server.updateXML(users, document);
                }
            } catch (IOException | ParserConfigurationException | SAXException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets offline messages.
     *
     * @param user the user
     * @return the offline messages
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
                    if (messageRecipient.equals(user.getName())) {
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







        /*while (true){
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                org.w3c.dom.Document document;
                String f = "res/users.xml";
                File file= new File(f);
                document = documentBuilder.parse(file);
                NodeList users = document.getElementsByTagName("User");
                userValidation(name, users);
            } catch (IOException | ParserConfigurationException | SAXException e) {
                System.out.println(e.getMessage());
            }
        }*/
    }

    /*private void joinServer(User user, NodeList nodeList) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        String message, groupName = null;
        boolean hasUnread = false;
        StringBuilder unreadMessages = new StringBuilder();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File("res/messages.xml"));
            Element root = document.getDocumentElement();
            NodeList messageList = root.getElementsByTagName("Message");
            int messageCount = messageList.getLength();
            int i = 0;
            while (i < messageCount) {
                Element msg = (Element) messageList.item(i);
                if(user.name().equals(msg.getElementsByTagName("Recipient").item(0).getTextContent())){
                    hasUnread =true;
                    String sender = msg.getElementsByTagName("Sender").item(0).getTextContent();
                    String text = msg.getElementsByTagName("Text").item(0).getTextContent();
                    String time = msg.getElementsByTagName("Time").item(0).getTextContent();
                    unreadMessages.append(time).append(" [PM]").append(sender).append(": ").append(text).append("\n");

                    //alisin msg element sa messages.xml if nasent na
                    Node parent = msg.getParentNode();
                    parent.removeChild(msg);
                    messageCount--;
                    i--;
                }
                i++;
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File("res/messages.xml"));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(hasUnread){
            printWriter.println("UNREAD MESSAGES: ");
            printWriter.println(unreadMessages);
        }
        try {
            broadcast(user.name() + " joined the chat");
            messagePrompt(user.name());

            setLoginStatus(user.name(), "online");

            boolean exit = false;

            while (!exit && (message = bufferedReader.readLine()) != null) {
                if (message.startsWith("/")) {
                    String[] words = message.split("/");

                    String command = words[1];
                    System.out.println(command);
                    switch (command) {
                        case "edit":
                            showEditMenu();
                            printWriter.println("Input your choice: ");
                            byte choice = Byte.parseByte(bufferedReader.readLine());
                            switch (choice) {
                                //TODO
                                case 1 -> changeUName(user.name(), nodeList);
                                case 2 -> System.out.println();
                                case 3 -> System.out.println();
                                case 4 -> System.out.println();
                            }
                            break;
                        case "pm":
                            printWriter.println("Send to: ");
                            groupName = bufferedReader.readLine();
                            messagePrompt(user.name());
                            message = bufferedReader.readLine();
                            Server.getRegisteredUsers();
                            for (User u :Server.registeredUsersList) {
                                if (u.name().equals(groupName)) {
                                    if (u.status().equals("online")) {
                                        for (Map.Entry<ClientHandler, User> hash : loggedInUserHashMap.entrySet()) {
                                            if (hash.getValue().name().equals(groupName)) {
                                                for (ClientHandler loginHandler : loginHandlerArraylist) {
                                                    if (loginHandler.socket.equals(hash.getKey().socket)) {
                                                        loginHandler.sendMessage("[PRIVATE] "+user.name() + ": " + message);
                                                        break;
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                        break;
                                        //check if offline yung user, if offline yung user, store yung message sa messages.xml
                                    } else if (u.status().equals("offline")){
                                        LocalDateTime timeSent = LocalDateTime.now();
                                        printWriter.println("user "+u.name()+" is offline, "+u.name()+" will receive your message if "+u.name()+" goes online:)");
                                        xmlParse.addMessage(user.name(), message, u.name(),timeSent);
                                        break;
                                    } else
                                        sendMessage("User not existing");
                                }
                            }

                            break;
                        case "create":
                            GroupChatClientHandler gcClientHandler = new GroupChatClientHandler(socket, user);
                            gcClientHandler.start();
                            break;
                        case "help":
                            showCommands();
                            break;
                        case "quit":
                            broadcast(user.name() + " has left the chat.");
                            setLoginStatus(user.name(), "offline");
                            socket.close();
                            printWriter.close();
                            bufferedReader.close();
                            exit = true;
                            break;
                        case "gm":
                            printWriter.println("GROUP: ");
                            groupName = bufferedReader.readLine();
                            messagePrompt(user.name());
                            message = bufferedReader.readLine();
                            for (User u :Server.getUsersByGroupName(groupName)) {
                                for (Map.Entry<ClientHandler, User> hash : loggedInUserHashMap.entrySet()) {
                                    if (hash.getValue().name().equals(u.name())) {
                                        for (ClientHandler clientHandler : loginHandlerArraylist) {
                                            if (clientHandler.socket.equals(hash.getKey().socket)) {
                                                clientHandler.sendMessage("[GROUP @"+groupName+"] " + user.name() + ": " + message);
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                            break;
                        default:
                            printWriter.println("command not recognized. input '/help' for a list of commands");
                    }
                } else {
                    broadcast(user.name() + ": " + message);
                }
            }
        } catch (SocketException socketException) {
            System.out.println(socketException.getMessage());
            socketException.getCause();
            throw new SocketException("Socket closed");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("res/users.xml");

            NodeList users = document.getElementsByTagName("User");

            for (int i = 0; i < users.getLength(); i++) {
                Element element = (Element) users.item(i);
                element.getElementsByTagName("status").item(0).setTextContent("offline");
                Server.updateXML(users, document);
            }
            socket.close();
        }
    }*/

    /*private void sendGM(User u) {
        try {
            printWriter.println("You are a member of the groups: ");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(usersFile);
            Element root = document.getDocumentElement();
            NodeList users = root.getElementsByTagName("User");
            ArrayList<String> groupList = new ArrayList<>();
            for (int i = 0; i < users.getLength(); i++) {
                Element element = (Element) users.item(i);
                if (Objects.equals(u.name(), element.getElementsByTagName("name").item(0).getTextContent())) {
                    NodeList groups = element.getElementsByTagName("Groupname");
                    for (int j = 0; j < groups.getLength(); j++) {
                        Element element1 = (Element) groups.item(j);
                        String role = element1.getAttribute("id");
                        String groupName = element1.getTextContent();
                        printWriter.println("\"" + groupName + "\" with the role \"" + role + "\"");
                        groupList.add(groupName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /*private void setLoginStatus(String name, String status) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(usersFile);
            Element root = document.getDocumentElement();
            NodeList users = root.getElementsByTagName("User");
            int userListLength = users.getLength();
            for(int i = 0; i<userListLength;i++){
                Element u = (Element) users.item(i);
                String nameNode = u.getElementsByTagName("name").item(0).getTextContent();
                if(nameNode.equals(name)){
                    u.getElementsByTagName("status").item(0).setTextContent(status);
                    Server.updateXML(users, document);
                    System.out.println(usersFile.getCanonicalPath()+" has been updated!, status of "+name+" has been set to '"+status+"'.");
                    break;
                }
            }
        }
        catch(Exception e){
                e.printStackTrace();
            }
        }


    *//**
     * Broadcast messages.
     *
     * @param msg        the msg
     * @param userSender the user sender
     *//*
    public void broadcastMessages(String msg, User userSender) {
        for (ClientHandler client: loginHandlerArraylist) {
            client.printWriter.println(
                    userSender.toString() + "<span>: " + msg+"</span>");
        }
    }

    *//**
     * Broadcast all users.
     *//*
// send list of clients to all Users
    public void broadcastAllUsers(){
        for (ClientHandler client: loginHandlerArraylist) {
            client.printWriter.println(loginHandlerArraylist);
        }
    }

    *//**
     * Send message to user.
     *
     * @param msg        the msg
     * @param userSender the user sender
     * @param user       the user
     *//*
// send message to a User (String)
    public void sendMessageToUser(String msg, User userSender, String user){
        boolean find = false;
        for (Map.Entry<ClientHandler, User> client : loggedInUserHashMap.entrySet()) {
            if (client.getValue().username().equals(user) && client.getValue() != userSender) {
                find = true;
                client.getKey().printWriter.println(userSender.toString() + " -> " + client.toString() +": " + msg);
                client.getKey().printWriter.println(
                        "(<b>Private</b>)" + userSender.toString() + "<span>: " + msg+"</span>");
            }
        }
        if (!find) {
            for (Map.Entry<ClientHandler, User> clientHandlerUserEntry : loggedInUserHashMap.entrySet()) {
                if (clientHandlerUserEntry.getKey().equals(userSender))
                    clientHandlerUserEntry.getKey().printWriter.print(userSender.toString() + " -> (<b>no one!</b>): " + msg);
            }
        }
    }

    private void messagePrompt(String name) {
        printWriter.println("\n\n" + name + ": ");
    }

    private void showCommands() {
        printWriter.println("\n\n");
        printWriter.println("COMMANDS");
        printWriter.println("/help");
        printWriter.println("/edit");
        printWriter.println("/privatemessage");
        printWriter.println("\n\n");
    }

    private void showEditMenu() {
        printWriter.println("\n\n");
        printWriter.println("[1] Change username");
        printWriter.println("[2] Change name");
        printWriter.println("[3] Change age");
        printWriter.println("[4] Change password");
        printWriter.println("\n\n");
    }

    *//**
     * Broadcast.
     *
     * @param message the message
     *//*
    public void broadcast(String message ){
        for (ClientHandler loginHandler : loginHandlerArraylist) {
            if (loginHandler != null && loginStatus) {
                loginHandler.sendMessage("[BROADCAST] "+message);
            }
        }
    }

    *//**
     * Send message.
     *
     * @param message the message
     *//*
    public void sendMessage (String message){
        printWriter.println(message);
    }


    *//**
     * Change u name.
     *
     * @param name      the name
     * @param usersList the users list
     *//*
    public void changeUName(String name, NodeList usersList) {

        //should check list of usernames muna if available
        File xmlFile = new File("res/users.xml");
        Document document;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(xmlFile);
            NodeList users;

            users = document.getElementsByTagName("User");

            printWriter.println("Enter new username: ");
            String nameToChangeTo = bufferedReader.readLine();
            for (int i = 0; i < usersList.getLength(); i++) {
                Element u = (Element) users.item(i);
                String nameNode = u.getElementsByTagName("name").item(0).getTextContent();
                printWriter.println(nameNode);
                if (nameNode.equals(name)) {
                    u.getElementsByTagName("name").item(0).setTextContent(nameToChangeTo);
                    Server.updateXML(users, document);
                    printWriter.println("User " + nameNode + " has changed name to " + nameToChangeTo);
                    break;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    *//**
     * User validation.
     *
     * @param name  the name
     * @param users the users
     *//*
    public void userValidation(String name, NodeList users) {

        while (!loginStatus) {
            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(Server.f);

                String username, password;
                users = document.getElementsByTagName("User");
                Element u = null;

                login:
                while (true) {
                    printWriter.println("\nUsername: ");
                    username = bufferedReader.readLine();

                    auth:
                    for (int i = 0; i < users.getLength(); i++) {
                        u = (Element) users.item(i);
                        String uName = u.getElementsByTagName("Username").item(0).getTextContent();

                        if (uName.equals(username)) {
                            for (int j = 0; j < users.getLength(); j++) {
                                printWriter.println("\nPassword: ");
                                password = bufferedReader.readLine();
                                String pass = u.getElementsByTagName("Password").item(0).getTextContent();
                                String nameNode = u.getElementsByTagName("name").item(0).getTextContent();

                                if (pass.equals(password)) {
                                    name = nameNode;
                                    loginStatus = true;

                                    if (u.getElementsByTagName("BanStatus").item(0).getTextContent().equalsIgnoreCase("Banned")) {
                                        printWriter.println("Sorry. Your account is currently banned from the system.");
                                        continue login;
                                    }
                                    *//*if (u.getElementsByTagName("status").item(0).getTextContent().equals("online")) {
                                        printWriter.println("User is currently logged in on another device.");
                                        continue login;
                                    }*//*

                                    u.getElementsByTagName("status").item(0).setTextContent("online");
                                    Server.updateXML(users, document);


                                    // Add users to lists
                                    loginHandlerArraylist.add(new ClientHandler(socket, printWriter, bufferedReader));
                                    User user = new User(u.getAttribute("User"), u.getElementsByTagName("name").item(0).getTextContent(), u.getElementsByTagName("Age").item(0).getTextContent(),
                                            u.getElementsByTagName("Username").item(0).getTextContent(),
                                            u.getElementsByTagName("Password").item(0).getTextContent(), u.getElementsByTagName("status").item(0).getTextContent(), u.getElementsByTagName("BanStatus").item(0).getTextContent());

                                    // IP, USER HASHMAP

                                    loggedInUserHashMap.put(new ClientHandler(socket, printWriter, bufferedReader), user);


                                    System.out.println("Login Successful!");

                                    System.out.println(u.getElementsByTagName("name").item(0).getTextContent() + " " + u.getElementsByTagName("status").item(0).getTextContent());

                                    joinServer(user, users);
                                    broadcast(name + ": ");
                                    break;
                                }
                                printWriter.println("Invalid password.");
                            }
                            break;
                        } else if (i == users.getLength() - 1)
                            printWriter.println("User is not existing");
                    }
                }
            } catch (IOException | ParserConfigurationException | SAXException | TransformerException e) {
                System.out.println(e.getMessage());
            }
        }
    }*/
