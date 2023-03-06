package shared_classes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class XMLParse {

    private static Document usersDoc;
    private String file;
    public XMLParse(String file) {
        this.file = file;
    }
    public XMLParse(){}


    private static void getUsersDoc() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File f = new File("res/users.xml");
            Node element;
            if (f.exists()) {
                usersDoc = db.parse(f);
            } else {
                usersDoc = db.newDocument();
                element = usersDoc.createElement("Users");
                usersDoc.appendChild(element);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public User getUser(String userName) {
        User user = new User();
        try {
            getUsersDoc();
            usersDoc.getDocumentElement().normalize();
            NodeList nodeList = usersDoc.getElementsByTagName("User");
            Element element;

            for (int i = 0; i < nodeList.getLength(); i++) {
                element = (Element) nodeList.item(i);
                if (element.getElementsByTagName("Username").item(0).getTextContent().equals(userName)) {
                    user.setName(element.getElementsByTagName("name").item(0).getTextContent());
                    user.setAge(element.getElementsByTagName("Age").item(0).getTextContent());
                    user.setUsername(element.getElementsByTagName("Username").item(0).getTextContent());
                    user.setPassword(element.getElementsByTagName("Password").item(0).getTextContent());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public static void setOnline(User user) {
        try {
            getUsersDoc();
            usersDoc.getDocumentElement().normalize();
            NodeList nodeList = usersDoc.getElementsByTagName("User");
            Element element;

            for (int i = 0; i < nodeList.getLength(); i++) {
                element = (Element) nodeList.item(i);
                if (element.getElementsByTagName("Username").item(0).getTextContent().equals(user.getUsername())) {
                    element.getElementsByTagName("status").item(0).setTextContent("online");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<User> getUserList() {
        getUsersDoc();
        usersDoc.getDocumentElement().normalize();
        NodeList nodeList = usersDoc.getElementsByTagName("User");
        List<User> userList = new ArrayList<>();
        Element element;

        for (int i = 0; i < nodeList.getLength(); i++) {
            element = (Element) nodeList.item(i);
            User temp = new User();
            temp.setName(element.getElementsByTagName("name").item(0).getTextContent());
            temp.setAge(element.getElementsByTagName("Age").item(0).getTextContent());
            temp.setUsername(element.getElementsByTagName("Username").item(0).getTextContent());
            temp.setPassword(element.getElementsByTagName("Password").item(0).getTextContent());
            temp.setStatus(element.getElementsByTagName("status").item(0).getTextContent());
            temp.setBanStatus(element.getElementsByTagName("BanStatus").item(0).getTextContent());
            userList.add(temp);
        }

        System.out.println(userList);

        return userList;
    }

    public void addUser(User newUser) {
        try {
            List<User> userList = getUserList();
            UUID userID = UUID.randomUUID();
            clearXML(usersDoc);
            userList.add(newUser);

            for (User user : userList) {
                Element usersTag = usersDoc.getDocumentElement();
                Element nUser = usersDoc.createElement("User");
                nUser.setAttribute("id", String.valueOf(userID));
                Element nameElement = usersDoc.createElement("name");
                nameElement.setTextContent(user.getName());
                Element ageElement = usersDoc.createElement("Age");
                ageElement.setTextContent(user.getAge());
                Element usrnmElement = usersDoc.createElement("Username");
                usrnmElement.setTextContent(user.getUsername());
                Element pswdElement = usersDoc.createElement("Password");
                pswdElement.setTextContent(user.getPassword());
                Element statusElement = usersDoc.createElement("status");
                statusElement.setTextContent(user.getStatus());
                Element banSttsElement = usersDoc.createElement("BanStatus");
                banSttsElement.setTextContent(user.getBanStatus());

                nUser.appendChild(nameElement);
                nUser.appendChild(ageElement);
                nUser.appendChild(usrnmElement);
                nUser.appendChild(pswdElement);
                nUser.appendChild(statusElement);
                nUser.appendChild(banSttsElement);

                usersTag.appendChild(nUser);
            }

            DOMSource source = new DOMSource(usersDoc);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            StreamResult streamResult = new StreamResult(new File("res/users.xml"));
            transformer.transform(source, streamResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearXML(Document xml) {
        Element root = xml.getDocumentElement();
        Node child = root.getFirstChild();
        while (child != null) {
            Node nextChild = child.getNextSibling();
            root.removeChild(child);
            child = nextChild;
        }
    }

    public void addMessage(String sender, String message, String recipient, LocalDateTime timeSent) {

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document;
            File f = new File(file);
            NodeList users = null;
            Node element = null;

            //check nu adda ti file f idjay compooter
            if (f.exists()) {
                document = documentBuilder.parse(file);
                users = document.getElementsByTagName("Messages");
                element = users.item(users.getLength() - 1);
            }
            //if awan ti file f, create new document
            else {
                document = documentBuilder.newDocument();
                element = document.createElement("Messages");
                document.appendChild(element);
            }

            //create element MESSAGE
            Element msgElement = document.createElement("Message");
            element.appendChild(msgElement);

            //set id for user
            msgElement.setAttribute("id", "xxx");

            //dtf example 2023-02-20 7:30
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            Element timeElement = document.createElement("Time");
            timeElement.appendChild(document.createTextNode(timeSent.format(dtf)));
            msgElement.appendChild(timeElement);

            Element senderElement = document.createElement("Sender");
            senderElement.appendChild(document.createTextNode(sender));
            msgElement.appendChild(senderElement);

            Element txtElement = document.createElement("Text");
            txtElement.appendChild(document.createTextNode(message));
            msgElement.appendChild(txtElement);

            Element recipientElement = document.createElement("Recipient");
            recipientElement.appendChild(document.createTextNode(recipient));
            msgElement.appendChild(recipientElement);


            trimWhiteSpace(element);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(file));
            transformer.transform(domSource, streamResult);
        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    // ikaten tayu dagijay white spaces idjay xml file
    public static void trimWhiteSpace(Node node) {
        NodeList nodeList = node.getChildNodes();
        try {
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node child = nodeList.item(i);
                if (child.getNodeType() == Node.TEXT_NODE) {
                    child.setTextContent(child.getTextContent().trim());
                }
                trimWhiteSpace(child);
            }
        } catch (NullPointerException nullPointerException) {
            throw new NullPointerException("Socket closed");
        }
    }
}