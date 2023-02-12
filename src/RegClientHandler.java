import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class RegClientHandler extends Thread {
    final Socket socket;
    final BufferedReader bufferedReader;
    final PrintWriter printWriter;

    public RegClientHandler(Socket socket, PrintWriter printWriter, BufferedReader bufferedReader) {
        this.socket = socket;
        this.bufferedReader = bufferedReader;
        this.printWriter = printWriter;
    }

    @Override
    public void run() {
        XMLParse userCreator = new XMLParse("users.xml");
        while (true) {
            try {
                do {
                    printWriter.println("Enter name: ");
                    String name = bufferedReader.readLine();
                    printWriter.println("Enter age: ");
                    String age = bufferedReader.readLine();
                    String username = checkUsername();
                    printWriter.println("Enter pass: ");
                    String password = bufferedReader.readLine();
                    userCreator.addUser(name, age, username, password); // may return object na user here
                } while (cont());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // may masmaayos siguro na method kesa dito but it worky
    private String checkUsername() throws Exception {
        printWriter.println("Enter username: ");
        String username = bufferedReader.readLine();
        while (isDuplicate(username)) {
            printWriter.print("Username \"" + username + "\" is already taken! Please enter another username: ");
            username = bufferedReader.readLine();
        }
        return username;
    }

    private boolean cont() throws IOException {
        String response = "";
        while (!("Y".equalsIgnoreCase(response) || "N".equalsIgnoreCase(response))) {
            printWriter.print("Do you want to continue adding users? [y/n]: ");
            response = bufferedReader.readLine();
            if (!("Y".equalsIgnoreCase(response) || "N".equalsIgnoreCase(response))) {
                printWriter.println("Invalid entry! Please use characters [y] or [n] and try again.");
            }
            printWriter.println();
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
