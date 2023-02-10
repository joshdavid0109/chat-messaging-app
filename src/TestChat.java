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
    static ArrayList<ClientHandler> clientHandlerArraylist = new ArrayList<>();

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
                clientHandlerArraylist.add(new ClientHandler(clientSocket, printWriter, bufferedReader));
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
    String ip;


    public ClientHandler(Socket clientSocket, PrintWriter printWriter, BufferedReader bufferedReader) {
        this.socket = clientSocket;
        this.printWriter = printWriter;
        this.bufferedReader = bufferedReader;
        ip = clientSocket.getRemoteSocketAddress().toString();
    }

    public void run() {
        String username, password, name = null;
        loginStatus = false;

        while (true){

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                org.w3c.dom.Document document;
                File file = new File(f);

                document = documentBuilder.parse(file);


                try {
                    NodeList users = document.getElementsByTagName("User");

                    while(!loginStatus){

                        printWriter.println("\nUsername: ");
                        username = bufferedReader.readLine();

                        for (int i = 0; i < users.getLength(); i++) {
                            Element u = (Element) users.item(i);
                            String uName = u.getElementsByTagName("Username").item(0).getTextContent();

                            if (!uName.equals(username)) {
                                continue;
                            }

                            printWriter.println("\nPassword: ");
                            password = bufferedReader.readLine();
                            String pass = u.getElementsByTagName("Password").item(0).getTextContent();
                            String nameNode = u.getElementsByTagName("name").item(0).getTextContent();

                            if (pass.equals(password)) {
                                name = nameNode;
                                loginStatus = true;
                                System.out.println("Login Successful!");

                                joinServer(name, users);
                                broadcast(name + ": ");
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

    private void joinServer(String name, NodeList usersList) {
        broadcast(name + " joined the chat");
        String message, recipient, sender;

        try {
            printWriter.println(name + ": ");

            // Test private message, di pa 100% working
            while ((message = bufferedReader.readLine()) != null) {
                if (message.startsWith("/privatemessage")) {
                    printWriter.print("Send to: ");
                    printWriter.println();
                    recipient = bufferedReader.readLine();

                    for (int i = 0; i < usersList.getLength(); i++) {
                        Element users = (Element) usersList.item(i);
                        String r = users.getElementsByTagName("name").item(0).getTextContent();

                        if (r.equals(recipient)) {
                            for (ClientHandler clientHandlerhander : TestChat.clientHandlerArraylist) {
                                if (clientHandlerhander.socket.getRemoteSocketAddress().toString().equals(ip)) {
                                    clientHandlerhander.sendMessage(name + ": " + message);
                                }
                            }
                        }

                    }

                } else
                    broadcast(name + ": " + message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

       /* private void joinServer (String name) throws IOException {
            sendMessage(name + " joined the chat");
            String message;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

            while (true) {

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
*/
        public void broadcast (String message){
            for (ClientHandler clientHandler : TestChat.clientHandlerArraylist) {

                if (clientHandler != null) {
                    clientHandler.sendMessage(message);

                } else
                    printWriter.println("helloi=");
            }
        }

        public void sendMessage (String message){
            printWriter.println(message);
        }
    }



