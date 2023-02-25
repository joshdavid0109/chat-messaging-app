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
                System.out.print("\nInput port: ");
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
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public static void shutdown() {
        try {
            bufferedReader.close();
            printWriter.close();
            clientSocket.close();
            serverSocket.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                        User user = new User(id, name, age, username, password, status, "x");
                        users.add(user);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
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
