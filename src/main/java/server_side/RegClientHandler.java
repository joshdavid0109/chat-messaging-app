package server_side;

import org.w3c.dom.*;
import shared_classes.User;
import shared_classes.XMLParse;

import javax.xml.parsers.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegClientHandler implements Runnable {
    final BufferedReader bufferedReader;
    final PrintWriter printWriter;

    public RegClientHandler() {
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        this.printWriter = new PrintWriter(System.out);
    }

    @Override
    public void run() {
        XMLParse userCreator = new XMLParse("users.xml");
        String name, age, username, password;
        while (true) {
            try {
                do {
                    System.out.println("Enter name: ");
                    name = bufferedReader.readLine();
                    do {
                        System.out.println("Enter age: ");
                        age = bufferedReader.readLine();
                        if (!isNumeric(age))
                            System.out.println("Invalid age");
                    } while (!isNumeric(age));

                    username = checkUsername();

                    do {
                        System.out.println("Enter password: ");
                        password = bufferedReader.readLine();

                        if (password.length() > 16 || password.length() < 8) {
                            System.out.println("Password must contain 8-16 characters");
                        }
                    } while (!passWordValidation(password));

                    UUID randomID = UUID.randomUUID();

                    userCreator.addUser(randomID.toString(), name, age, username, password); // may return object na user here
                } while (cont());
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
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
        System.out.println("Enter username: ");
        String username = bufferedReader.readLine();
        while (isDuplicate(username)) {
            System.out.print("Username \"" + username + "\" is already taken! Please enter another username: ");
            username = bufferedReader.readLine();
        }
        return username;
    }

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

}
