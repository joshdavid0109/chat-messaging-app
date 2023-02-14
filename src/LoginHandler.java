import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class LoginHandler extends Thread {
    Socket socket;
    final PrintWriter printWriter;
    final BufferedReader bufferedReader;
    boolean loginStatus;


    public LoginHandler(Socket clientSocket, PrintWriter printWriter, BufferedReader bufferedReader) {
        this.socket = clientSocket;
        this.printWriter = printWriter;
        this.bufferedReader = bufferedReader;
        String ip = clientSocket.getRemoteSocketAddress().toString();
    }

    public void run() {
        String name = null;

        while (true){

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                org.w3c.dom.Document document;
                String f = "res/users.xml";
                File file = new File(f);

                document = documentBuilder.parse(file);

                NodeList users = document.getElementsByTagName("User");

                userValidation(name, users);

            } catch (IOException | ParserConfigurationException | SAXException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void joinServer(String name, NodeList usersList) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        String message, recipient;

        try {
            broadcast(name + " joined the chat");
            messagePrompt(name);

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
                                case 1 -> changeUName(name, usersList);
                                case 2 -> System.out.println();
                                case 3 -> System.out.println();
                                case 4 -> System.out.println();
                            }
                            break;
                        case "pm":
                            printWriter.println("Send to: ");
                            recipient = bufferedReader.readLine();
                            messagePrompt(name);
                            message = bufferedReader.readLine();
                            for (Map.Entry<String, User> hash : Server.loggedInUserHashMap.entrySet()) {
                                if (hash.getValue().name().equals(recipient)) {
                                    for (LoginHandler loginHandler : Server.loginHandlerArraylist) {
                                        if (loginHandler.socket.getRemoteSocketAddress().toString().equals(hash.getKey()))
                                            loginHandler.sendMessage(name + ": " + message);
                                        else
                                            sendMessage("User not existing");
                                    }
                                }
                            }

                            break;
                        case "help":
                            showCommands();
                            break;
                        case "quit":
                            broadcast(name + " has left the chat.");
                            exit = true;
                            break;
                        case "ban":
                            //TODO
                            System.out.println();
                            break;
                        default:
                            printWriter.println("command not recognized. input '/help' for a list of commands");
                    }
                } else {
                    broadcast(name + ": " + message);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            socket.close();
        }
    }
    private void messagePrompt(String name) {
        printWriter.println("\n" + name + ": ");
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

    public void editName(String name, String newName) {

    }
    public void broadcast (String message){
        for (LoginHandler loginHandler : Server.loginHandlerArraylist) {
            if (loginHandler != null && loginStatus) {
                loginHandler.sendMessage(message);
            }
        }
    }

    public void sendMessage (String message){
        printWriter.println(message);
    }

    public void changeUName(String name, NodeList usersList){
        File xmlFile = new File("users.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document users = null;
        try {
            users = dBuilder.parse(xmlFile);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
        String nameToChangeTo = "hardcodelol";
        for (int i = 0; i < usersList.getLength(); i++) {
            Element u = (Element) usersList.item(i);
            String nameNode = u.getElementsByTagName("name").item(0).getTextContent();
            if (nameNode.equals(name)) {
                u.getElementsByTagName("name").item(0).setTextContent(nameToChangeTo);
                System.out.println("User "+nameNode+" has changed name to "+nameToChangeTo);
                break;
            }
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        DOMSource source = new DOMSource(users);
        StreamResult result = new StreamResult(xmlFile);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        System.out.println(xmlFile);
        System.out.println(source);
        System.out.println("XML file updated successfully!");
    }

    public void userValidation(String name, NodeList users) {

        String username, password;
        while (!loginStatus) {
            try {
                printWriter.println("\nUsername: ");

                username = bufferedReader.readLine();


                for (int i = 0; i < users.getLength(); i++) {
                    Element u = (Element) users.item(i);
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
                                Server.loginHandlerArraylist.add(new LoginHandler(socket, printWriter, bufferedReader));
                                User user = new User(u.getAttribute("User"), u.getElementsByTagName("name").item(0).getTextContent(), u.getElementsByTagName("Age").item(0).getTextContent(),
                                        u.getElementsByTagName("Username").item(0).getTextContent(),
                                        u.getElementsByTagName("Password").item(0).getTextContent());

                                if (u.getElementsByTagName("BanStatus").item(0).getTextContent().equalsIgnoreCase("Banned")) {
                                    printWriter.println("Sorry. Your account is currently banned from the system.");
                                    break;
                                }

                                Server.loggedInUserHashMap.put(socket.getRemoteSocketAddress().toString(), user);
                                System.out.println("Login Successful!");

                                joinServer(name, users);
                                broadcast(name + ": ");
                                break;
                            }
                            printWriter.println("Invalid password.");
                        }
                    } else if (i == users.getLength() - 1)
                        printWriter.println("User is not existing");
                }
            } catch (IOException | ParserConfigurationException | SAXException | TransformerException e) {
                throw new RuntimeException(e);
            }
        }
    }
}