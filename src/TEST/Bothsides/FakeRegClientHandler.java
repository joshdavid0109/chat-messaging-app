package TEST.Bothsides;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.*;

public class FakeRegClientHandler {

    private static final FakeXMLParser FAKE_XML_PARSER = new FakeXMLParser();
    private static final Scanner scanner = new Scanner(System.in);

    public void register() {
        FakeUser newFakeUser;
        String name, age, userName, password;
        do {
            System.out.print("Enter name: ");
            name = scanner.nextLine();
            age = checkAge();
            userName = checkUsername();
            password = checkPassword();

            newFakeUser = new FakeUser(name, age, userName, password);
            FAKE_XML_PARSER.writeUser(newFakeUser);
            /* TODO: writeObj(newUser) to send to Server which will process data and write it to the XML file
             * server will invoke the writeUser method in XMLParser NOT the client
             */
        } while (cont());
    }

    private boolean cont() {
        String response = "";
        while (!("Y".equalsIgnoreCase(response) || "N".equalsIgnoreCase(response))) {
            System.out.print("Do you want to continue adding users? [y/n]: ");
            response = scanner.nextLine();
            if (!("Y".equalsIgnoreCase(response) || "N".equalsIgnoreCase(response))) {
                System.out.println("Invalid entry! Please use characters [y] or [n] and try again.");
            }
            System.out.println();
        }
        return "Y".equalsIgnoreCase(response);
    }

    private String checkUsername() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        while (isDuplicate(username)) {
            System.out.print("Username \"" + username + "\" is already taken! Please enter another username: ");
            username = scanner.nextLine();
        }
        return username;
    }

    private String checkAge() {
        System.out.print("Enter age: ");
        String age = scanner.nextLine();
        while (!isNumeric(age)) {
            System.out.print("Invalid value! Please enter a valid numerical age: ");
            age = scanner.nextLine();
        }
        return age;
    }

    private String checkPassword() {
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        while (isValid(password)) {
            System.out.println("Password must contain 8-16 valid characters! Please try again.");
            System.out.print("Password: ");
            password = scanner.nextLine();
        }
        return password;
    }

    private static boolean isDuplicate(String userName) {
        List<FakeUser> fakeUserList = FAKE_XML_PARSER.getUserList();

        for (FakeUser fakeUser : fakeUserList) {
            if (Objects.equals(fakeUser.getUserName(), userName)) {
                return true;
            }
        }

        return false;
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    private boolean isValid(String password) {

        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        Pattern p = Pattern.compile(regex);

        if (password == null) {
            return false;
        } else if (password.length() > 16 || password.length() < 8) {
            return false;
        }

        Matcher m = p.matcher(password);

        return m.matches();
    }
}
