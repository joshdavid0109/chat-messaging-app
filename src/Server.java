import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    static String f = "res/users.xml";
    static Socket clientSocket;
    static PrintWriter printWriter;
    static BufferedReader bufferedReader;
    static ArrayList<LoginHandler> loginHandlerArraylist = new ArrayList<>();
    static HashMap<String, User> userHashMap = new HashMap<>();

    public void run() {
        int testChoice = 0;


        while (true) {


            try (ServerSocket serverSocket = new ServerSocket(1234)) {
                clientSocket = serverSocket.accept();

                bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                printWriter = new PrintWriter(clientSocket.getOutputStream(), true);


                System.out.println("A client has connected.");

                printWriter.println("Register [1]\nLogin [2]");
                testChoice = Integer.parseInt(bufferedReader.readLine());
                if (testChoice == 1) {
                    RegClientHandler registration = new RegClientHandler(clientSocket, printWriter, bufferedReader);
                    registration.start();
                }else  if (testChoice == 2){

                    // if registration is successful --
                    Thread login = new LoginHandler(clientSocket, printWriter, bufferedReader);

                    login.start();
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();


    }
}
