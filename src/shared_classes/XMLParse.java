package shared_classes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server_side.Server;

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
import java.util.*;


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
    /**
     * Get the groups of a user, return as a List
     *
     * @param user The user whose groups you want to get.
     * @return A list of groups that the user is a member of.
     */
    public static List<Group> getGroupsOfUser(User user) throws ParserConfigurationException, IOException, SAXException {
        getUsersDoc();
        usersDoc.getDocumentElement().normalize();
        NodeList nodeList = usersDoc.getElementsByTagName("User");
        List<Group> groups = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node userNode = nodeList.item(i);
            if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                Element userElement = (Element) userNode;
                String name = userElement.getElementsByTagName("name").item(0).getTextContent();
                if (name.equals(user.getName())) {
                    NodeList groupNodes = userElement.getElementsByTagName("Group");
                    groups = new ArrayList<>();
                    for (int j = 0; j < groupNodes.getLength(); j++) {
                        Node groupNode = groupNodes.item(j);
                        if (groupNode.getNodeType() == Node.ELEMENT_NODE) {
                            String group = groupNode.getTextContent();
                            groups.add(new Group(group));
                        }
                    }
                    break;
                }
            }
        }
        return groups;
    }
    public static List<String> getGroupsOfUserString(User user) throws ParserConfigurationException, IOException, SAXException {
        getUsersDoc();
        usersDoc.getDocumentElement().normalize();
        NodeList nodeList = usersDoc.getElementsByTagName("User");
        List<String> groups = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node userNode = nodeList.item(i);
            if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                Element userElement = (Element) userNode;
                String name = userElement.getElementsByTagName("name").item(0).getTextContent();
                if (name.equals(user.getName())) {
                    NodeList groupNodes = userElement.getElementsByTagName("Group");
                    groups = new ArrayList<>();
                    for (int j = 0; j < groupNodes.getLength(); j++) {
                        Node groupNode = groupNodes.item(j);
                        if (groupNode.getNodeType() == Node.ELEMENT_NODE) {
                            String group = groupNode.getTextContent();
                            groups.add(new Group(group).getName());
                        }
                    }
                    break;
                }
            }
        }
        return groups;
    }

    /**
     * "Get all the groups from the users.xml file and return them in an ArrayList."
     *
     * The first thing we do is create an ArrayList to hold the groups. We then call the getUsersDoc() function to get the
     * users.xml file. We then get the root element of the document and normalize it. We then get all the User nodes in the
     * document. We then loop through all the User nodes. For each User node, we get all the Group nodes. We then loop
     * through all the Group nodes. For each Group node, we get the text content of the node and convert it to lower case.
     * We then check to see if the group is already in the ArrayList. If it isn't, we add it to the ArrayList
     *
     * @return A list of all the groups in the users.xml file.
     */
    public static ArrayList<String> getAllGroups() throws ParserConfigurationException, SAXException, IOException {
        ArrayList<String> groups = new ArrayList<>();

        getUsersDoc();
        usersDoc.getDocumentElement().normalize();
        NodeList nodeList = usersDoc.getElementsByTagName("User");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node userNode = nodeList.item(i);
            if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                Element userElement = (Element) userNode;
                NodeList groupNodes = userElement.getElementsByTagName("Group");
                for (int j = 0; j < groupNodes.getLength(); j++) {
                    Node groupNode = groupNodes.item(j);
                    if (groupNode.getNodeType() == Node.ELEMENT_NODE) {
                        String group = groupNode.getTextContent().toLowerCase(Locale.ROOT);
                        if (!groups.contains(group)) {
                            groups.add(group);
                        }
                    }
                }
            }
        }

        return groups;
    }

    public static boolean loginAuth(String username, String password) {
        DocumentBuilderFactory documentBuilderFactory = null;
        DocumentBuilder documentBuilder = null;
        Document document = null;
        NodeList nodelist = null;
        Element element;
        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse("res/users.xml");
            nodelist = document.getElementsByTagName("User");

            for (int i = 0; i < nodelist.getLength(); i++) {
                element = (Element) nodelist.item(i);
                String uname = element.getElementsByTagName("Username").item(0).getTextContent();
                String pass = element.getElementsByTagName("Password").item(0).getTextContent();
                if (uname.equals(username) && pass.equals(password)) {
                    System.out.println("paol");
                    element.getElementsByTagName("status").item(0).setTextContent("online");
                    Server.updateXML(nodelist, document);
                    return true;
                }

            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static void setStatusOfUser(String username, String status) {
        DocumentBuilderFactory documentBuilderFactory = null;
        DocumentBuilder documentBuilder = null;
        Document document = null;
        NodeList nodelist = null;

        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse("res/users.xml");
            nodelist = document.getElementsByTagName("User");

            Element element;
            for (int i = 0; i < nodelist.getLength(); i++) {
                element = (Element) nodelist.item(i);
                String uname = element.getElementsByTagName("Username").item(0).getTextContent();
                if (uname.equals(username)) {
                    element.getElementsByTagName("status").item(0).setTextContent(status);
                    Server.updateXML(nodelist, document);
                    break;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void setEveryoneOffline() {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("res/users.xml");
            NodeList users = document.getElementsByTagName("User");
            for (int i = 0; i < users.getLength(); i++) {
                Element element = (Element) users.item(i);

                element.getElementsByTagName("status").item(0).setTextContent("offline");

                Server.updateXML(users, document);
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
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
    public void setBanStatus(String username, String status){
        try {
            getUsersDoc();
            usersDoc.getDocumentElement().normalize();
            NodeList nodeList = usersDoc.getElementsByTagName("User");
            Element element = null;

            if(status.equals("BANNED")){
                for (int i = 0; i < nodeList.getLength(); i++) {
                    element = (Element) nodeList.item(i);
                    if (element.getElementsByTagName("Username").item(0).getTextContent().equals(username)) {
                        if(element.getElementsByTagName("BanStatus").item(0).getTextContent().equals("BANNED")){
                            System.out.println(username+ " is already banned");
                        }
                        else{
                            element.getElementsByTagName("BanStatus").item(0).setTextContent("BANNED");
                            System.out.println(username+"'s status has been set to "+status);
                        }
                    }
                }

            }
            else if (status.equals("")) {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    element = (Element) nodeList.item(i);
                    if (element.getElementsByTagName("Username").item(0).getTextContent().equals(username)) {
                        if (element.getElementsByTagName("BanStatus").item(0).getTextContent().equals("")) {
                            System.out.println(username + " is not banned");
                        } else {
                            element.getElementsByTagName("BanStatus").item(0).setTextContent("");
                            System.out.println(username+"'s status has been set to "+status);
                        }
                    }
                }
            }
            nodeList = usersDoc.getElementsByTagName("Users");
            trimWhiteSpace((Element) nodeList.item(0));

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
    public static List<String> getAllContactNames() {
        getUsersDoc();
        usersDoc.getDocumentElement().normalize();
        NodeList nodeList = usersDoc.getElementsByTagName("User");
        List<String> contactNames = new ArrayList<>();
        Element element;

        for (int i = 0; i < nodeList.getLength(); i++) {
            element = (Element) nodeList.item(i);
            contactNames.add(element.getElementsByTagName("name").item(0).getTextContent());
        }
        return contactNames;
    }

    /**
     * It reads the users.xml file and returns an array of strings containing the names and usernames of all registered
     * users
     *
     * @return A list of all the users in the XML file.
     */
    public static String[] usersList() {
        String[] contacts = new String[Server.registeredUsersList.size()];
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("res/users.xml");
            NodeList userNodes = document.getElementsByTagName("User");

            contacts = new String[userNodes.getLength()];

            for (int i = 0; i < userNodes.getLength(); i++) {
                Node userNode = userNodes.item(i);
                if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element userElement = (Element) userNode;
                    String name = userElement.getElementsByTagName("name").item(0).getTextContent();
                    String username = userElement.getElementsByTagName("Username").item(0).getTextContent();
                    contacts[i] = name + " - " + username;
                }
            }
            // Sort contacts alphabetically
            Arrays.sort(contacts);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            System.out.println(e.getMessage());
        }
        return contacts;
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
    public static String searchXML(String name){
        try{
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("res/users.xml");
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("User");

            for(int i = 0; i < nodeList.getLength(); i++){
                Node nNode = nodeList.item(i);

                if(nNode.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) nNode;

                    String userName = element
                            .getElementsByTagName("name")
                            .item(0)
                            .getTextContent();

                    if(userName.equalsIgnoreCase(name)){
                        String info = "Name: " + userName + "\n";
                        info += "User name: " +element.getElementsByTagName("Username")
                                .item(0)
                                .getTextContent() +"\n";

                        info += "Status: " +element.getElementsByTagName("status")
                                .item(0)
                                .getTextContent() +"\n";

                        return info;
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
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

    public void deleteUser(String nameToDelete) {
        try {
            getUsersDoc();
            usersDoc.getDocumentElement().normalize();
            NodeList nodeList = usersDoc.getElementsByTagName("User");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element user = (Element)nodeList.item(i);
                String name = user.getElementsByTagName("Username").item(0).getTextContent();
                if (name.equals(nameToDelete)) {
                    Element parent = (Element) user.getParentNode();
                    parent.removeChild(user);
                }
            }

            nodeList = usersDoc.getElementsByTagName("Users");
            trimWhiteSpace((Element) nodeList.item(0));

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
    public static ArrayList<User> getUsersWithGroup(String groupName) {
        ArrayList<User> users = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse("res/users.xml");
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("User");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                NodeList groupList = element.getElementsByTagName("Groupname");
                for (int j = 0; j < groupList.getLength(); j++) {
                    Element group = (Element) groupList.item(j);
                    if (group.getTextContent().equals(groupName)) {
                        String id = element.getAttribute("id");
                        String name = element.getElementsByTagName("name").item(0).getTextContent();
                        String age = element.getElementsByTagName("Age").item(0).getTextContent();
                        String username = element.getElementsByTagName("Username").item(0).getTextContent();
                        String password = element.getElementsByTagName("Password").item(0).getTextContent();
                        String status = element.getElementsByTagName("status").item(0).getTextContent();
                        String banStatus = element.getElementsByTagName("BanStatus").item(0).getTextContent();
                        users.add(new User(id, name, age, username, password, status, banStatus));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }
}