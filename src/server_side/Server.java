package server_side;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import shared_classes.GroupChatUsersSample;
import shared_classes.User;
import shared_classes.XMLParse;

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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {
    public static String f = "res/users.xml";
    static Socket clientSocket;
    static PrintWriter printWriter;
    static BufferedReader bufferedReader;
    public static ArrayList<ClientHandler> loginHandlerArraylist = new ArrayList<>();
    public static List<User> registeredUsersList = new ArrayList<>();
    public static List<GroupChatUsersSample> groupChatUsers = new ArrayList<>();
    public static HashMap<ClientHandler, User> loggedInUserHashMap = new HashMap<>();
    private int port;
    static Scanner scanner = new Scanner(System.in);
    static ServerSocket serverSocket;

    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    public Server(int port){
        this.port = port;
    }
    public Server(){
    }

    public void run() throws IOException, SAXException, ParserConfigurationException {
        System.out.println("Welcome to the Admin Panel of Budget Discord!");
        System.out.println("What do you want to do?");
        adminPanel();
        port = 0;
        boolean validPort = false;
        while(!validPort){
            try{
                System.out.print("INPUT PORT: ");
                port = Integer.parseInt(scanner.nextLine());
                serverSocket = new ServerSocket(port);
                validPort = true;
            }
            catch(NumberFormatException e){
                System.out.println("Input a valid port");
                System.out.println(e.getMessage());
            }
            catch(RuntimeException e){
                System.out.println(e.getMessage());
            }
            catch(BindException e){
                System.out.println(e.getMessage());
                System.out.println("PORT IS ALREADY IN USE, INPUT ANOTHER PORT");
            }
        }
        System.out.println("Server created at port: "+port);

            try {

                while (true) {
                    ExecutorService executorService = Executors.newFixedThreadPool(10);
                        try {
                            clientSocket = serverSocket.accept();
                            System.out.println("A client has connected.");
                            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                            executorService.execute(new ClientHandler(clientSocket, printWriter, bufferedReader));
                        }catch (IOException e) {
                            throw new RuntimeException(e);
                        }



                }

            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }


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
            System.out.println(f+" has been updated!");
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    private static void banUser(String command, String name) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(f);

            NodeList users = document.getElementsByTagName("User");
            Element element = null;


            if (command.equals("/ban")) {

                for (int i = 0; i < users.getLength(); i++) {
                    element = (Element) users.item(i);
                    if (element.getElementsByTagName("name").item(0).getTextContent().equals(name)) {
                        element.getElementsByTagName("BanStatus").item(0).setTextContent("Banned");
                        element.getElementsByTagName("status").item(0).setTextContent("online");
                        System.out.println(name + " is banned.\n");
                        break;
                    }
                }
            } else if (command.equals("/unban")) {
                for (int i = 0; i < users.getLength(); i++) {
                    element = (Element) users.item(i);
                    if (element.getElementsByTagName("name").item(0).getTextContent().equals(name)) {
                        element.getElementsByTagName("BanStatus").item(0).setTextContent(" ");
                        System.out.println(name + " is unbanned\n");
                        break;
                    }
                }
            }
            updateXML(users, document);

        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public void adminPanel() {
        do {
            getRegisteredUsers();
            System.out.println("[1] View list of users");
            System.out.println("[2] Ban / unban a user");
            System.out.println("[3] Start server");
            System.out.println("[4] Exit\n");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 1) {
                printUserList();
            } else if (choice == 2) {
                do {
                    System.out.println("Type /ban [NAME] to ban a user or /unban [NAME] to unban a user");
                    String whatToDo = scanner.nextLine();
                    String first = whatToDo.split(" ")[0];
                    if (!(first.equals("/ban")) && !(first.equals("/unban"))) {
                        System.out.println("Enter the commands \"/ban\" or \"/unban\" only!\n");
                        continue;
                    }
                    banUser(whatToDo.split(" ")[0], whatToDo.split(" ")[1]);
                    registeredUsersList.clear();
                    break;
                } while (true);
            } else if (choice == 3) {
                System.out.println("Starting server...\n");
                break;
            } else if (choice == 4) {
                System.out.println("Closing Budget Discord, have a nice day!");
                System.exit(0);
            }
        } while (true);
    }

    public void printUserList() {
        System.out.printf("%n-----------------------------------------------------------------%n");
        System.out.printf("                  LIST OF REGISTERED USERS                       %n");
        System.out.printf("-----------------------------------------------------------------%n");
        System.out.printf("| %-10s | %-4s | %-15s | %-10s | %-8s |%n", "NAME", "AGE", "USERNAME",
                "STATUS", "BAN STATUS");
        System.out.printf("-----------------------------------------------------------------%n");
        for (User user : registeredUsersList) {
            String banStats;
            if (Objects.equals(user.banStatus(), "")) {
                banStats = "NOT BANNED";
            } else {
                banStats = "BANNED";
            }
            System.out.printf("| %-10s | %-4s | %-15s | %-10s | %-8s |%n",
                    user.name(), user.age(), user.username(), user.status(), banStats);
        }
        System.out.printf("-----------------------------------------------------------------%n%n");
    }


    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.run();
        } catch (IOException | SAXException | ParserConfigurationException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void getRegisteredUsers() {
        String id, name, age, username, password, status, banStatus;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(f);

            NodeList users = document.getElementsByTagName("User");

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
    }


}
