import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class LoginHandler extends Thread {
    Socket socket = null;
    PrintWriter printWriter = null;
    BufferedReader bufferedReader;
    static LoginHandler loginHandler;
    boolean loginStatus;

    public LoginHandler(Socket clientSocket, PrintWriter printWriter, BufferedReader bufferedReader) {
        this.socket = clientSocket;
        this.printWriter = printWriter;
        this.bufferedReader = bufferedReader;
        String ip = clientSocket.getRemoteSocketAddress().toString();
    }

    public void run() {
        String name = null;

        while (true){

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                org.w3c.dom.Document document;
                String f = "res/users.xml";
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
        String message, recipient;

        try {
            broadcast(name + " joined the chat");
            messagePrompt(name);

            while ((message = bufferedReader.readLine()) != null) {


                if (message.startsWith("/pm")) {
                    printWriter.println("Send to: ");
                    recipient = bufferedReader.readLine();

                    messagePrompt(name);
                    message = bufferedReader.readLine();

                        for (Map.Entry<String, User> hash : Server.userHashMap.entrySet()) {
                             if (hash.getValue().name().equals(recipient)) {
                                 for (LoginHandler loginHandler : Server.loginHandlerArraylist) {
                                     if (loginHandler.socket.getRemoteSocketAddress().toString().equals(hash.getKey()))
                                         loginHandler.sendMessage(name + ": " + message);
                                     else
                                         sendMessage("User not existing");
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

    private void messagePrompt(String name) {
        printWriter.println("\n" + name + ": ");
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

        public void userValidation(String name, NodeList users) {

            String username, password;
            while (!loginStatus) {
                try {
                    printWriter.println("\nUsername: ");

                    username = bufferedReader.readLine();


                    for (int i = 0; i < users.getLength(); i++) {
                        Element u = (Element) users.item(i);
                        String uName = u.getElementsByTagName("Username").item(0).getTextContent();

                        if (uName.equals(username)) {



                            for (int j = 0; j < users.getLength(); j++) {
                                printWriter.println("\nPassword: ");
                                password = bufferedReader.readLine();
                                String pass = u.getElementsByTagName("Password").item(0).getTextContent();
                                String nameNode = u.getElementsByTagName("name").item(0).getTextContent();

                                if (pass.equals(password)) {
                                    name = nameNode;
                                    loginStatus = true;
                                    Server.loginHandlerArraylist.add(new LoginHandler(socket, printWriter, bufferedReader));
                                    User user = new User(u.getAttribute("User"), u.getElementsByTagName("name").item(0).getTextContent(), u.getElementsByTagName("Age").item(0).getTextContent(),
                                            u.getElementsByTagName("Username").item(0).getTextContent(),
                                            u.getElementsByTagName("Password").item(0).getTextContent());
                                    Server.userHashMap.put(socket.getRemoteSocketAddress().toString(), user);
                                    System.out.println("Login Successful!");

                                    joinServer(name, users);
                                    broadcast(name + ": ");
                                    break;
                                } else if (j == users.getLength() - 1)
                                    printWriter.println("Invalid password.");
                            }
                        } else if (i == users.getLength() - 1)
                            printWriter.println("User is not existing");



                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
}




