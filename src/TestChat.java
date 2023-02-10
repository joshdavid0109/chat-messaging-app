import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TestChat {
    static String f = "users.xml";
    static Socket clientSocket;
    static PrintWriter printWriter;
    static BufferedReader bufferedReader;

    private boolean done;


    public static void main(String[] args) {
        int port = 8888;


        while (true) {


            try (ServerSocket serverSocket = new ServerSocket(port)) {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document;
                File file = new File(f);

                document = documentBuilder.parse(file);

                clientSocket = serverSocket.accept();

                bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                printWriter = new PrintWriter(clientSocket.getOutputStream(), true);


                System.out.println("A client has connected.");

                Thread handler = new ClientHandler(clientSocket, printWriter, bufferedReader);
                handler.start();


            } catch (ParserConfigurationException | IOException | SAXException e) {
                throw new RuntimeException(e);
            }
        }
    }


}

class ClientHandler extends Thread {

    final ArrayList<ClientHandler> clientHandlerArraylist = new ArrayList<>();
    final Socket socket;
    final PrintWriter printWriter;
    final BufferedReader bufferedReader;
    int counter;
    boolean loginStatus;
    private String f = "users.xml";

    public ClientHandler(Socket clientSocket, PrintWriter printWriter, BufferedReader bufferedReader) {
        this.counter = counter;
        this.socket = clientSocket;
        this.printWriter = printWriter;
        this.bufferedReader = bufferedReader;
    }

    public void run() {
        String username, password, name = null;
        int index = 0;
        loginStatus = false;

        while (true){

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                org.w3c.dom.Document document;
                File file = new File(f);

                document = documentBuilder.parse(file);

                try {

                    NodeList users = null;
                    org.w3c.dom.Element user = null;

                    users = document.getElementsByTagName("Users");
                    NodeList userTest = document.getElementsByTagName("User");


                    while(!loginStatus){


                        printWriter.println("\nUsername: ");
                        username = bufferedReader.readLine();

                        for (int i = 0; i < userTest.getLength(); i++) {
                            Element u = (Element) userTest.item(i);
                            Node uName = u.getElementsByTagName("Username").item(0).getFirstChild();

                            if (!uName.getTextContent().equals(username)) {
                                continue;
                            }

                            printWriter.println("\nPassword: ");
                            password = bufferedReader.readLine();
                            Node pass = u.getElementsByTagName("Password").item(0).getFirstChild();
                            Node nameNode = u.getElementsByTagName("name").item(0).getFirstChild();

                            if (pass.getTextContent().equals(password)) {
                                name = nameNode.getTextContent();
                                loginStatus = true;
                                System.out.println(name + " has successfully logged onto idjay server");
                                joinServer(name);
                                break;
                            }
                        }
                    }

                } catch (IOException ignored) {

                }

            } catch (IOException | ParserConfigurationException | SAXException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void joinServer(String name) throws IOException {
        sendMessage(name + " joined the chat");
        String message;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        while(true){

            printWriter.println("INPUT YOUR MESSAGE: ");
            printWriter.flush();
            message = bufferedReader.readLine();
            //get sa system yung date, dapat server side siguro ito para consistent
            LocalDateTime now = LocalDateTime.now();
            Messages msg = new Messages(name, message);
            printWriter.println(dtf.format(now));
            printWriter.println(msg);
            printWriter.flush();
        }

    }


    public void broadcast(String message) {

        for (ClientHandler clientHandler: clientHandlerArraylist) {

            if (clientHandler != null) {
                clientHandler.sendMessage(message);

            } else
                printWriter.println("helloi=");
        }
    }

    public void sendMessage(String message) {
        printWriter.println(message);
    }

}


