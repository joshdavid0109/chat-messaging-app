import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

public class test {

    protected static Scanner scn = new Scanner(System.in);
    public static void main(String[] args) throws Exception {
        XMLParse userCreator = new XMLParse("users.xml");

        do {
            System.out.print("Enter name: ");
            String name = scn.nextLine();
            System.out.print("Enter age: ");
            String age = scn.nextLine();
            String username = checkUsername();
            System.out.print("Enter pass: ");
            String password = scn.nextLine();
            userCreator.addUser(name, age, username, password);
        } while (cont());
    }

    // may masmaayos siguro na method kesa dito but it worky
    private static String checkUsername() throws Exception {
        System.out.print("Enter username: ");
        String username = scn.nextLine();
        while (isDuplicate(username)) {
            System.out.print("Username \"" + username + "\" is already taken! Please enter another username: ");
            username = scn.nextLine();
        }
        return username;
    }

    private static boolean cont() {
        String response = "";
        while (!("Y".equalsIgnoreCase(response) || "N".equalsIgnoreCase(response))) {
            System.out.print("Do you want to continue adding users? [y/n]: ");
            response = scn.next();
            if (!("Y".equalsIgnoreCase(response) || "N".equalsIgnoreCase(response))) {
                System.out.println("Invalid entry! Please use characters [y] or [n] and try again.");
            }
            System.out.println();
        }
        return "Y".equalsIgnoreCase(response);
    }

    // iterate through elements to get usernames then check if entered username is the same
    private static boolean isDuplicate(String userName) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document users = documentBuilder.parse(new File("users.xml"));

        NodeList nodeList = users.getElementsByTagName("Username");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Objects.equals(node.getTextContent(), userName)) {
                return true;
            }
        }

        return false;
    }
}
