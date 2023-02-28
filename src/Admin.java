/*
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server_side.RegClientHandler;
import server_side.Server;
import shared_classes.User;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static server_side.Server.updateXML;

public class Admin {
    static File f = new File("res/users.xml");
    static ArrayList<User> userArrayList;
    static Scanner scanner;
    static PrintWriter printWriter = new PrintWriter(System.out);
    private int port;

    public static void main(String[] args) throws Exception {
        Admin admin = new Admin();
        admin.run();
    }

    public void run() throws Exception {
        scanner = new Scanner(System.in);
        int choice = 0;
        userArrayList = getUsers();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        System.out.println("Welcome to the Admin Panel of Budget Discord!");
        System.out.println("What do you want to do?");

        do {
            panelMenu();
            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    //RegClientHandler regClientHandler = new RegClientHandler();
                    //regClientHandler.run();
                    //break;
                case 2:
                    System.out.println("/ban [NAME] to ban a user\n/unban [NAME] to unban a user");
                    executorService.execute(new Thread(() -> {
                        String input = "";
                        if ((input = scanner.nextLine()) != null) {
                            String finalInput = input;
                            if ((finalInput.startsWith("/ban") || finalInput.startsWith("/unban")))
                                banUser(finalInput.split(" ")[0], finalInput.split(" ")[1]);
                        }
                    }));
                    break;
                case 3:
                    System.out.print("Enter username of user you want to delete: ");
                    scanner.nextLine();
                    String name = scanner.nextLine();
                    deleteUser(name);
                    break;
                case 4:
                    //printUserList();
                    break;
                case 5:
                    Server server = new Server(port);
                    server.run();
                    break;
                case 6:
                    //Server.shutdown();
                    break;
                default:
                    break;
            }
        }while (choice != 6);
}

public void panelMenu() {
    System.out.println("--------ADMIN PANEL---------");
    System.out.println("[1] Add user");
    System.out.println("[2] Ban/Unban User");
    System.out.println("[3] Delete User ");
    System.out.println("[4] View List of Registered Users Server ");
    System.out.println("[5] Start Server ");
    System.out.println("[6] Terminate System");
}

public void deleteUser(String username) {

    try {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(f);

        NodeList users = document.getElementsByTagName("Username");

        for (int i = 0; i < users.getLength(); i++) {
            Node node = users.item(i);
            String uname = node.getTextContent();
            if (uname.equalsIgnoreCase(username)) {
                Node user = node.getParentNode();
                Node userRoot = user.getParentNode();
                userRoot.removeChild(user);
                i--;
                ;
            }
            Server.updateXML(users, document);

        }
    } catch (ParserConfigurationException | IOException | SAXException e) {
        throw new RuntimeException(e);
    }

}

    public void printUserList() {
        userArrayList = getUsers();
        System.out.printf("%n-----------------------------------------------------------------%n");
        System.out.printf("                  LIST OF REGISTERED USERS                       %n");
        System.out.printf("-----------------------------------------------------------------%n");
        System.out.printf("| %-10s | %-4s | %-15s | %-10s | %-8s |%n", "NAME", "AGE", "USERNAME",
                "STATUS", "BAN STATUS");
        System.out.printf("-----------------------------------------------------------------%n");
        for (User user : userArrayList) {
            String banStats;
            if (Objects.equals(user.getBanStatus(), "")) {
                banStats = "NOT BANNED";
            } else {
                banStats = "BANNED";
            }
            System.out.printf("| %-10s | %-4s | %-15s | %-10s | %-8s |%n",
                    user.getName(), user.getAge(), user.getUsername(), user.getStatus(), banStats);
        }
        System.out.printf("-----------------------------------------------------------------%n%n");
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

    public static ArrayList<User> getUsers() {
        ArrayList<User> userArrayList = new ArrayList<>();
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
                //userArrayList.add(new User(id, name, age, username, password, status, banStatus));


            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
        return userArrayList;
    }
}
*/
