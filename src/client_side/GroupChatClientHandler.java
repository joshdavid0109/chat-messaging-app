package client_side;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server_side.Server;
import shared_classes.User;
import shared_classes.XMLParserGC;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class GroupChatClientHandler extends Thread {

    final Socket socket;
    final BufferedReader bufferedReader;
    final PrintWriter printWriter;
    User user;

    public GroupChatClientHandler(Socket socket, PrintWriter printWriter, BufferedReader bufferedReader, User user) {
        this.socket = socket;
        this.bufferedReader = bufferedReader;
        this.printWriter = printWriter;
        this.user = user;
    }

    @Override
    public void run() {
        XMLParserGC createGC = new XMLParserGC("res/gcUsers.xml");
        String gcName, admin;
        ArrayList<User> groupMembers;

            try {
                printWriter.println("INPUT GROUP NAME: ");
                gcName = bufferedReader.readLine();
                printWriter.println(gcName);
                //gcName = checkGroupname();

                admin = user.name();

                groupMembers = populateMembers();
                for(int i = 0; i<groupMembers.size(); i++){
                    printWriter.println(groupMembers.get(i));
                }
                createGC.GroupChat(gcName, admin, groupMembers);

            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    private ArrayList<User> populateMembers() throws IOException, SAXException, ParserConfigurationException {
        ArrayList<User> users = new ArrayList<>();
        Document document;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        document = documentBuilder.parse("res/users.xml");
        NodeList usersNodeList;
        usersNodeList = document.getElementsByTagName("User");
        String m = "";
        while (!m.equals("finished")) {

            printWriter.println("Enter username of member: ");
            m = bufferedReader.readLine();
            boolean userExists = false;
            /*for (int i = 0; i < usersNodeList.getLength(); i++) {
                Element u = (Element) usersNodeList.item(i);
                String nameNode = u.getElementsByTagName("Username").item(0).getTextContent();
                if (nameNode.equals(m)) {
                    users.add(u.getElementsByTagName("Username").item(0).getTextContent());
                    printWriter.println("USER " + u.getElementsByTagName("Username").item(0).getTextContent() + " HAS BEEN ADDED TO ARRAYLIST");
                    userExists = true;
                    break;
                }
            }*/

            for (User user: Server.registeredUsersList) {
                if (m.equals(user.username())) {
                    users.add(user);
                    printWriter.println(user);
                    userExists = true;
                    break;
                }
            }

            if (!userExists && !m.equals("finished")) {
                printWriter.println("USER NOT FOUND");
            }
        }
        return users;
    }


    // iterate through elements to get usernames then check if entered username is the same
    private static boolean isDuplicate(String userName) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document users = documentBuilder.parse(new File("res/users.xml"));

        NodeList nodeList = users.getElementsByTagName("Username");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Objects.equals(node.getTextContent(), userName)) {
                return true;
            }
        }

        return false;
    }

    private String checkGroupname() throws Exception {
        System.out.println("Enter Group Name : ");
        String groupName = bufferedReader.readLine();
        while (isDuplicate(groupName)) {
            System.out.print(groupName + "\" is already taken! Please enter another Group Name: ");
            groupName = bufferedReader.readLine();
        }
        return groupName;
    }
}
