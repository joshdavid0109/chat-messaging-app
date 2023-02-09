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

public class TestChat {
    static String f = "users.xml";

    public static void main(String[] args) {
        int port = 8888;


        while (true) {
            Socket clientSocket;
            PrintWriter printWriter;
            BufferedReader bufferedReader;

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

                    users = document.getElementsByTagName("User");

                    printWriter.println("\nUsername: ");
                    username = bufferedReader.readLine();
                    for (int i = 0; i <= users.getLength(); i++) {
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
                            break;
                        }
                    }
                    
                    printWriter.printf("[%s]: ", name);

                } catch (IOException ignored) {
                }

            } catch (IOException | ParserConfigurationException | SAXException e) {
                throw new RuntimeException(e);
            }
        }




    }
}


