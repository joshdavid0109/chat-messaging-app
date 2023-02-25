package server_side;

import client_side.GroupChatClientHandler;
import gui_classes.clientside.ClientMain;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import shared_classes.User;
import shared_classes.XMLParse;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.Objects;

import static server_side.Server.loggedInUserHashMap;
import static server_side.Server.loginHandlerArraylist;


public class ClientHandler implements Runnable {
    public Socket socket;
    public PrintWriter printWriter = null;
    public BufferedReader bufferedReader = null;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;
    boolean loginStatus;
    static File usersFile = new File("res/users.xml");
    XMLParse xmlParse = new XMLParse("res/messages.xml");
//    ObjectInputStream inputStream = new

    public ClientHandler(Socket clientSocket, PrintWriter printWriter, BufferedReader bufferedReader) throws IOException {
        this.socket= clientSocket;
        this.printWriter = printWriter;
        this.bufferedReader = bufferedReader;

    }

    public ClientHandler(Socket s) {
//        this.socket = s;
//        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        printWriter = new PrintWriter(socket.getOutputStream(), true);
    }

    public void run() {
        String name = null;

        while (true){

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
                throw new RuntimeException(e);
            }
        }
    }

    private void joinServer(User user, NodeList nodeList) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        String message, recipient;
//        objectInputStream = new ObjectInputStream(socket.getInputStream());
//        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

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
                    String sender = msg.getElementsByTagName("Sender").item(0).getTextContent();
                    String text = msg.getElementsByTagName("Text").item(0).getTextContent();
                    String time = msg.getElementsByTagName("Time").item(0).getTextContent();
                    printWriter.println(time+" [PM]" +sender+": "+text);

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
                            recipient = bufferedReader.readLine();
                            messagePrompt(user.name());
                            message = bufferedReader.readLine();
                            Server.getRegisteredUsers();
                            for (User u :Server.registeredUsersList) {
                                if (u.name().equals(recipient)) {
                                    if (u.status().equals("online")) {
                                        for (Map.Entry<ClientHandler, User> hash : loggedInUserHashMap.entrySet()) {
                                            if (hash.getValue().name().equals(recipient)) {
                                                for (ClientHandler loginHandler : loginHandlerArraylist) {
                                                    if (loginHandler.socket.equals(hash.getKey().socket)) {
                                                        loginHandler.sendMessage(user.name() + ": " + message);
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

                        case "ban":
                            //TODO
                            System.out.println();
                            break;
                        case "gm":
                            sendGM(user);
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
    }

    private void sendGM(User u) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(usersFile);
            Element root = document.getDocumentElement();
            NodeList users = root.getElementsByTagName("User");
            for (int i = 0; i < users.getLength(); i++) {
                Element element = (Element) users.item(i);
                NodeList groups = element.getElementsByTagName("Groupname");
                if (Objects.equals(u.id(), element.getAttribute("id"))) {
                    for (int j = 0; j < groups.getLength(); j++) {
                        Element nElement = (Element) groups.item(j);
                        String textContent = nElement.getTextContent();
                        printWriter.println(textContent + "\n");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLoginStatus(String name, String status) {
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



    public void broadcastMessages(String msg, User userSender) {
        for (ClientHandler client: loginHandlerArraylist) {
            client.printWriter.println(
                    userSender.toString() + "<span>: " + msg+"</span>");
        }
    }

    // send list of clients to all Users
    public void broadcastAllUsers(){
        for (ClientHandler client: loginHandlerArraylist) {
            client.printWriter.println(loginHandlerArraylist);
        }
    }

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

    public void broadcast(String message ){
        for (ClientHandler loginHandler : loginHandlerArraylist) {
            if (loginHandler != null && loginStatus) {
                loginHandler.sendMessage( message);
            }
        }
    }

    public void sendMessage (String message){
        printWriter.println(message);
    }


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
                                    if (u.getElementsByTagName("status").item(0).getTextContent().equals("online")) {
                                        printWriter.println("User is currently logged in on another device.");
                                        continue login;
                                    }

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


                                /*ClientMain clientMain = new ClientMain(user);
                                clientMain.run();*/

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
                throw new RuntimeException(e);
            }
        }
    }
}