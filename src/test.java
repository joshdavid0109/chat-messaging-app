
import java.util.ArrayList;
import java.util.Scanner;

public class test {

    public static void main(String[] args) {
        final ArrayList<User> userArrayList = new ArrayList<>();

        //bruh idk why may spaces dun sa xml  huhuhuhu pag madami ka nilagay na user may spaces inbetween
        XMLParse userCreator = new XMLParse("users.xml");

        Scanner scn = new Scanner(System.in);
        String choice = "y";
        while (choice.equalsIgnoreCase("y")) {
            System.out.print("Enter name: ");
            String name = scn.nextLine();
            System.out.print("Enter age: ");
            String age = scn.nextLine();
            System.out.print("Enter username: ");
            String username = scn.nextLine();
            System.out.print("Enter pass: ");
            String password = scn.nextLine();

            userArrayList.add(userCreator.addUser(name, age, username, password));

            System.out.print("Do you want to continue adding users? (y/n): ");
            choice = scn.nextLine();
        }

        TestChat testChat = new TestChat();
        testChat.run();
    }
}
