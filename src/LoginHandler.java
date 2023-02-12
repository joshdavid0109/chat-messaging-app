import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class LoginHandler extends Thread {
    final Socket socket;
    final PrintWriter printWriter;
    final BufferedReader bufferedReader;
    HashMap<String, User> userHashMap = new HashMap<>();
    boolean loginStatus;
    private final String f = "users.xml";
    private final String ip;


    public LoginHandler(Socket clientSocket, PrintWriter printWriter, BufferedReader bufferedReader) {
        this.socket = clientSocket;
        this.printWriter = printWriter;
        this.bufferedReader = bufferedReader;
        ip = clientSocket.getRemoteSocketAddress().toString();
    }

    public void run() {
        String name = null;

        while (true){

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                org.w3c.dom.Document document;
                File file = new File(f);

                document = documentBuilder.parse(file);


                NodeList users = document.getElementsByTagName("User");

                userValidation(name, users);



            } catch (IOException | ParserConfigurationException | SAXException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void joinServer(String name, NodeList usersList) {
        String message, recipient, sender;

        try {
            broadcast(name + " joined the chat");
            printWriter.println("\n" + name + ": ");

            // Test private message, di pa 100% working
            while ((message = bufferedReader.readLine()) != null) {


                if (message.startsWith("/privatemessage")) {
                    printWriter.println("Send to: ");
                    recipient = bufferedReader.readLine();

                    /*for (int i = 0; i < usersList.getLength(); i++) {
                        Element users = (Element) usersList.item(i);
                        String r = users.getElementsByTagName("name").item(0).getTextContent();
                    }*/
                        for (Map.Entry<String, User> hash : userHashMap.entrySet()) {
                            if (hash.getValue().name().equals(recipient)) {
                                sendMessage(name + ": " + message);
                                printWriter.println("hello " + name);
                            }
                        }

                        /*for (ClientHandler clientHandler: TestChat.clientHandlerArraylist) {
                            System.out.println(clientHandler.);
                        }
                            for (LoginHandler loginHandlerhander : Server.loginHandlerArraylist) {
                                if (loginHandlerhander.socket.getRemoteSocketAddress().toString().equals(ip) && loginHandlerhander.loginStatus) {
                                    loginHandlerhander.sendMessage(name + ": " + message);
                                }
                            }*/


                } else
                    broadcast(name + ": " + message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

        /*private void joinServer (String name) throws IOException {
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

        }*/
        public void broadcast (String message){

            for (LoginHandler loginHandler : Server.loginHandlerArraylist) {
                if (loginHandler != null && loginStatus) {
                        loginHandler.sendMessage(message);
                }
            }
        }

        public void sendMessage (String message){
            printWriter.println(message);
        }

        public HashMap<String, User> userValidation(String name, NodeList users) {

            String username, password;
            while (!loginStatus) {
                try {
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
                            Server.loginHandlerArraylist.add(new LoginHandler(socket, printWriter, bufferedReader));
                            User user = new User(u.getElementsByTagName("name").item(0).getTextContent(), u.getElementsByTagName("Age").item(0).getTextContent(),
                                    u.getElementsByTagName("Username").item(0).getTextContent(),
                                    u.getElementsByTagName("Password").item(0).getTextContent());
                            userHashMap = new HashMap<>();
                            userHashMap.put(socket.getRemoteSocketAddress().toString(), user);
                            System.out.println("Login Successful!");

                            joinServer(name, users);
                            broadcast(name + ": ");
                            break;
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return userHashMap;
        }
    }




