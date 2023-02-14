import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Server {
    static String f = "res/users.xml";
    static Socket clientSocket;
    static PrintWriter printWriter;
    static BufferedReader bufferedReader;
    static ArrayList<LoginHandler> loginHandlerArraylist = new ArrayList<>();
    static List<User> registeredUsersList = new ArrayList<>();
    static HashMap<String, User> loggedInUserHashMap = new HashMap<>();

    static Scanner scanner = new Scanner(System.in);

    public void run() {
        int testChoice = 0;


        while (true) {


            try (ServerSocket serverSocket = new ServerSocket(1234)) {
                clientSocket = serverSocket.accept();

                bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                printWriter = new PrintWriter(clientSocket.getOutputStream(), true);

                String input;
                System.out.println("A client has connected.");
                System.out.println("Ban a user - /ban + [name]");

                input = scanner.nextLine();
                if (input.startsWith("/ban")) {
                    banUser(input.split(" ")[1]);
                } else if (input.startsWith("add")) {
                    RegClientHandler regClientHandler = new RegClientHandler(clientSocket, printWriter, bufferedReader);
                    regClientHandler.start();
                }

                getRegisteredUsers();

                printWriter.println("Login");
                testChoice = Integer.parseInt(bufferedReader.readLine());
                if (testChoice == 2){

                    // if registration is successful --
                    Thread login = new LoginHandler(clientSocket, printWriter, bufferedReader);

                    login.start();
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void banUser(String name) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(f);

            NodeList users = document.getElementsByTagName("User");
            Element element = null;

            for (int i = 0; i < users.getLength(); i++) {
                element = (Element) users.item(i);
                if (element.getElementsByTagName("name").item(0).getTextContent().equals(name)) {
                    element.getElementsByTagName("BanStatus").item(0).setTextContent("Banned");
                    printWriter.println(name + " banned\n");
                    break;
                }


            }

            users = document.getElementsByTagName("Users");
            element = (Element) users.item(0);
            XMLParse.trimWhiteSpace(element);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(f));
            transformer.transform(domSource, streamResult);



        } catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();


    }

    private void getRegisteredUsers() {
        String id, name, age, username, password;
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

                registeredUsersList.add(new User(id, name, age, username, password));

                printWriter.println(name + " added\n");
            }



        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }


    }
}
