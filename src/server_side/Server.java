package server_side;

import gui_classes.LoginGUI;
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
                System.out.println("/ban [NAME] to ban a user\n/unban [NAME] to unban a user");
                getRegisteredUsers();
                ExecutorService executorService = Executors.newFixedThreadPool(10);
                while (true) {

                        try {
                            clientSocket = serverSocket.accept();
                            System.out.println("A client has connected.");
                            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                            executorService.execute(new ClientHandler(clientSocket, printWriter, bufferedReader));
                        }catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    executorService.execute(new Thread(() -> {
                        String input ="";
                    if ((input = scanner.nextLine())!= null) {
                        String finalInput = input;

                            if ((finalInput.startsWith("/ban") || finalInput.startsWith("/unban")))
                                banUser(finalInput.split(" ")[0], finalInput.split(" ")[1]);
                    }
                    }));

                }

            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }



            /* finally {
                // set status of all users to offline working yung code pero di ko sure san dapat nakalagay

                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(f);

                NodeList users = document.getElementsByTagName("User");

                for (int i = 0; i < users.getLength(); i++) {
                    Element element = (Element) users.item(i);

                    element.getElementsByTagName("status").item(0).setTextContent("offline");

                    Server.updateXML(users, document);
                }
            }*/



    private void banUser(String command, String name) {
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


    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.run();
        } catch (IOException | SAXException | ParserConfigurationException e) {
            System.out.println(e.getMessage());
        }


    }

    private void getRegisteredUsers() {
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
