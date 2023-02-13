import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String name, age, username, password;
        while (true) {
            try {
                do {
                    printWriter.println("Enter name: ");
                    name = bufferedReader.readLine();
                    do {
                        printWriter.println("Enter age: ");
                        age = bufferedReader.readLine();
                        if (!isNumeric(age))
                            printWriter.println("Invalid age");
                    } while (!isNumeric(age));

                    username = checkUsername();

                    do {
                        printWriter.println("Enter password: ");
                        password = bufferedReader.readLine();

                        if (password.length() > 16 || password.length() < 8) {
                            printWriter.println("Password must contain 8-16 characters");
                        }
                    } while (!passWordValidation(password));

                    userCreator.addUser(name, age, username, password); // may return object na user here
                } while (cont());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    private boolean passWordValidation(String password) {

        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        Pattern p = Pattern.compile(regex);

        if (password == null) {
            return false;
        }

        Matcher m = p.matcher(password);

        return m.matches();
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
