package client_side;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import shared_classes.XMLParserGC;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class GroupChatClientHandler extends Thread {

    final Socket socket;
    final BufferedReader bufferedReader;
    final PrintWriter printWriter;

    public GroupChatClientHandler(Socket socket, PrintWriter printWriter, BufferedReader bufferedReader) {
        this.socket = socket;
        this.bufferedReader = bufferedReader;
        this.printWriter = printWriter;
    }

    @Override
    public void run() {
        XMLParserGC createGC = new XMLParserGC("gcUsers.xml");
        String gcName, admin, members;

        while (true) {
            try {

                gcName = checkGroupname();

                System.out.println("Enter the Admin Name : ");
                admin = bufferedReader.readLine();

                do {
                    System.out.println("Enter members : ");
                    members = bufferedReader.readLine();

                    for (int x = 0; x < members.length(); x++) {
                        System.out.println("members : " + members);
                    }
                } while (members.equals("/finished"));


                createGC.GroupChat(gcName, admin, members); // may return object na user here

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

/*
    // just in case
    private boolean cont() throws IOException {
        String response = "";
        while (!("Y".equalsIgnoreCase(response) || "N".equalsIgnoreCase(response))) {
            System.out.print("Do you want to continue adding users? [y/n]: ");
            response = bufferedReader.readLine();
            if (!("Y".equalsIgnoreCase(response) || "N".equalsIgnoreCase(response))) {
                System.out.println("Invalid entry! Please use characters [y] or [n] and try again.");
            }
            System.out.println();
        }
        return "Y".equalsIgnoreCase(response);
    }
*/

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
