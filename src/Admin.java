import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server_side.RegClientHandler;
import shared_classes.User;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static server_side.Server.updateXML;

public class Admin {
    static File f = new File("res/users.xml");
    static ArrayList<User> userArrayList;
    static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    static PrintWriter printWriter = new PrintWriter(System.out);
    public static void main(String[] args) throws IOException {
        int choice = 0;
        userArrayList = getUsers();
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        System.out.println("--------ADMIN PANEL---------");
        System.out.println("[1] Add user");
        System.out.println("[2] Ban/Unban User");
        System.out.println("[3] Delete User ");
        System.out.println("[4] Start Server ");
        System.out.println("[5] Terminate System");

        while (choice <=5) {
            choice = bufferedReader.read();
            switch (choice) {
                case 1:
                    RegClientHandler regClientHandler = new RegClientHandler();
                    regClientHandler.run();
                    break;
                case 2:
                    System.out.println("/ban [NAME] to ban a user\n/unban [NAME] to unban a user");
                            executorService.execute(new Thread(() -> {
                                String input = "";
                                try {
                                    if ((input = bufferedReader.readLine()) != null) {
                                        String finalInput = input;
                                        if ((finalInput.startsWith("/ban") || finalInput.startsWith("/unban")))
                                            banUser(finalInput.split(" ")[0], finalInput.split(" ")[1]);
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }));
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
            }
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
                userArrayList.add(new User(id, name, age, username, password, status, banStatus));


            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
        return userArrayList;
    }
}
