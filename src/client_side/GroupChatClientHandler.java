package client_side;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server_side.Server;
import shared_classes.User;
import shared_classes.XMLParse;
import shared_classes.XMLParserGC;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class GroupChatClientHandler extends Thread {

    final Socket socket;
    final BufferedReader bufferedReader;
    final PrintWriter printWriter;
    User user;

    public GroupChatClientHandler(Socket socket, PrintWriter printWriter, BufferedReader bufferedReader, User user) {
        this.socket = socket;
        this.bufferedReader = bufferedReader;
        this.printWriter = printWriter;
        this.user = user;
    }

    @Override
    public void run() {
        XMLParserGC createGC = new XMLParserGC("res/Users.xml");
        String gcName, admin;
        ArrayList<User> groupMembers;

            try {
                printWriter.println("INPUT GROUP NAME: ");
                gcName = bufferedReader.readLine();
                printWriter.println(gcName);
                //gcName = checkGroupname();

                groupMembers = populateMembers(this.user, gcName);

                createGC.GroupChat(gcName, groupMembers);

            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    private ArrayList<User> populateMembers(User admin, String groupName) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        ArrayList<User> users = new ArrayList<>();
        File file = new File("res/users.xml");
        users.add(admin);
        Document document;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        document = documentBuilder.parse(file);
        NodeList usersNodeList = document.getElementsByTagName("User");
        String m = "";
        while (!m.equals("finished")) {
            boolean userExists = false;
            for (int i = 0; i < Server.registeredUsersList.size(); i++) {
                User user = Server.registeredUsersList.get(i);
                if (admin.username().equals(user.username())){
                    for (int j = 0; j < usersNodeList.getLength(); i ++) {
                        Element u = (Element) usersNodeList.item(i);
                        printWriter.println(u.getTextContent());
                        String username = u.getElementsByTagName("Username").item(0).getTextContent();
                        printWriter.println(username);

                        if (username.equals(admin.username())) {
                            printWriter.println(username);
                            Element gcName = document.createElement("Groupname");
                            gcName.setTextContent(groupName);
                            gcName.setAttribute("id", "Admin");
                            u.appendChild(gcName);
                            users.add(user);
                            printWriter.println("User" + username + " added to " + groupName);
                            Server.updateXML(usersNodeList, document);
                            break;
                        }
                    }
                    break;
                }
            }

            for (int j = 0; j<10; j++) {
                printWriter.println("Enter username of member: ");
                m = bufferedReader.readLine();
                User user = Server.registeredUsersList.get(j);
                if (user.username().equals(m)) {
                    for (int x = 0; x < usersNodeList.getLength(); x++) {
                        Element u1 = (Element) usersNodeList.item(j);
                        String username = u1.getElementsByTagName("username").item(0).getTextContent();
                        if (username.equals(m)) {

                            Element gcName = document.createElement("Groupname");
                            gcName.setTextContent(groupName);
                            gcName.setAttribute("id", "Member");
                            u1.appendChild(gcName);
                            users.add(user);
                            printWriter.println("USER " + u1.getElementsByTagName("Username").item(0).getTextContent() + " HAS BEEN ADDED TO ARRAYLIST");
                            userExists = true;
                            break;
                        }
                    }
                }

            }


            Element element = (Element) usersNodeList.item(usersNodeList.getLength() - 1);
            XMLParse.trimWhiteSpace(element);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = null;
            try {
                transformer = transformerFactory.newTransformer();
            } catch (TransformerConfigurationException e) {
                throw new RuntimeException(e);
            }

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(file.toURI()));
            transformer.transform(domSource, streamResult);

           /* for (User user: Server.registeredUsersList) {
                if (m.equals(user.username())) {



                    users.add(user);
                    printWriter.println(user);
                    userExists = true;
                    break;
                }
            }*/

            if (!userExists && !m.equals("finished")) {
                printWriter.println("USER NOT FOUND");
            }
        }
        return users;
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

    private String checkGroupname() throws Exception {
        System.out.println("Enter Group Name : ");
        String groupName = bufferedReader.readLine();
        while (isDuplicate(groupName)) {
            System.out.print(groupName + "\" is already taken! Please enter another Group Name: ");
            groupName = bufferedReader.readLine();
        }
        return groupName;
    }
}
