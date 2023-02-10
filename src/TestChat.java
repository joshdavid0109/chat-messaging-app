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
import java.util.ArrayList;

public class TestChat {
    static String f = "users.xml";
    static Socket clientSocket;
    static PrintWriter printWriter;
    static BufferedReader bufferedReader;



    private boolean done;


    public static void main(String[] args) {
        final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
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

                clientHandlers.add(new ClientHandler(clientSocket, printWriter, bufferedReader));


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
    boolean loginStatus = false;
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


                    for (int i = 0; i < users.getLength(); i++) {
                        printWriter.println("\nUsername: ");
                        username = bufferedReader.readLine();
                        user = (Element) users.item(i);
                        Node uName = user.getElementsByTagName("Username").item(0).getFirstChild();
                        if (!uName.getTextContent().equals(username)) {
                            continue;
                        }

                        printWriter.println("\nPassword: ");
                        password = bufferedReader.readLine();
                        for (int j = 0; j < users.getLength(); j++) {
                            Node pass = user.getElementsByTagName("Password").item(0).getFirstChild();
                            Node nameNode = user.getElementsByTagName("name").item(0).getFirstChild();
                            if (!pass.getTextContent().equals(password))
                                continue;

                            name = nameNode.getTextContent();
                            System.out.println("Login Successful!");
                            joinServer(name);
                            break;
                        }
                        break;
                    }




                } catch (IOException ignored) {
                }

            } catch (IOException | ParserConfigurationException | SAXException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void joinServer(String name) {
        sendMessage(name + " joined the chat");
        String message;

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


