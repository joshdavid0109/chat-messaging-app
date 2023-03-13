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
import javax.xml.stream.events.EndElement;
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
                        String group = groupNode.getTextContent();
                        if (!groups.contains(group)) {
                            groups.add(group);
                        }
                    }
                }
            }
        }

        return groups;
    }

    /**
     * It takes a username and password as parameters, and checks if the username and password are in the XML file. If they
     * are, it changes the status of the user to online and returns true
     *
     * @param username The username of the user who is trying to login
     * @param password The password of the user
     * @return A boolean value.
     */
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

    /**
     * It takes a username and a status as parameters, and then it updates the status of the user in the XML file
     *
     * @param username The username of the user whose status is to be changed.
     * @param status The status of the user.
     */
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

    /**
     * It sets the status of all users to offline
     */
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

    public static void parseGroup(Group group) {
        User admin = group.getAdmin();
        String groupname = group.getName();
        List<String> members = group.getMembers(); //todo, instead of string dapat user


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
    /**
     * It takes a username and a status, and sets the user's ban status to the status
     *
     * @param username The username of the user you want to ban/unban
     * @param status The status of the user. Either "BANNED" or ""
     */
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
    /**
     * "Get all the contact names from the XML file and return them as a list."
     *
     * The first line of the function calls the `getUsersDoc()` function, which is defined in the `XMLHelper` class. This
     * function returns a `Document` object that represents the XML file
     *
     * @return A list of all the contact names in the XML file.
     */
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
    public static String[] usersList(User user) {
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

                        contacts[i] = name + " @" + username;
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

    /**
     * It takes a User object, adds it to the list of users, and then writes the list to the XML file
     *
     * @param newUser The new user to be added to the XML file.
     */
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
                Element groupElement = usersDoc.createElement("Groups");


                nUser.appendChild(nameElement);
                nUser.appendChild(ageElement);
                nUser.appendChild(usrnmElement);
                nUser.appendChild(pswdElement);
                nUser.appendChild(statusElement);
                nUser.appendChild(banSttsElement);
                nUser.appendChild(groupElement);

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

    public static String[] getAllContacts() {
        String[] contacts;
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
                    String status = userElement.getElementsByTagName("status").item(0).getTextContent();
                    contacts[i] = name + " : " + status ;
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        return contacts;
    }

    public static ArrayList<User> getAllUsers() {
        ArrayList<User> users = null;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("res/users.xml");
            NodeList userNodes = document.getElementsByTagName("User");

            users = new ArrayList<>();

            for (int i = 0; i < userNodes.getLength(); i++) {
                Node userNode = userNodes.item(i);
                if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element userElement = (Element) userNode;
                    String id = userElement.getAttribute("id");
                    String username = userElement.getElementsByTagName("Username").item(0).getTextContent();
                    String name = userElement.getElementsByTagName("name").item(0).getTextContent();
                    String age = userElement.getElementsByTagName("Age").item(0).getTextContent();
                    String password = userElement.getElementsByTagName("Password").item(0).getTextContent();
                    String  status= userElement.getElementsByTagName("status").item(0).getTextContent();
                    String banStatus= userElement.getElementsByTagName("BanStatus").item(0).getTextContent();

                    users.add(new User(id, name, age,username, password, status, banStatus));
                }
            }
            // Sort contacts alphabetically
            Arrays.sort(new ArrayList[]{users});
        } catch (SAXException | IOException | ParserConfigurationException e) {
            System.out.println(e.getMessage());
        }
        return users;
    }

    public static void addGroup(Group group) {
        List<String> usersToAdd = group.getMembers();
        String groupName = group.getName();
        User admin = group.getAdmin();

        try {
            getUsersDoc();
            usersDoc.getDocumentElement().normalize();
            NodeList nodeList = usersDoc.getElementsByTagName("User");
            Element element = null;
            Element base;
            Element root;

            for (int j = 0; j < usersToAdd.size(); j++) {
                String temp = usersToAdd.get(j).split("\\s+", 3)[2];
                System.out.println(temp);
                for (int i = 0; i < nodeList.getLength(); i++) {
                    element = (Element) nodeList.item(i);
                    if (element.getElementsByTagName("Username").item(0).getTextContent().equals(temp)) {
                        System.out.println(element.toString());
                        NodeList groupNode = usersDoc.getElementsByTagName("Groups");
                        if (groupNode.getLength() > 0) {
                            root = (Element)groupNode.item(i) ;
                            base = usersDoc.createElement("Group");
                            if (element.getElementsByTagName("name").item(0).getTextContent().equals(admin.getName())) {
                                base.setAttribute("id", "Admin");
                                base.setTextContent(groupName);
                            }else {
                                base.setAttribute("id", "Member");
                                base.setTextContent(groupName);
                            }
                            root.appendChild(base);
                            break;
                        } else {
                                root = (Element) groupNode.item(i);
                                Element groupRoot = usersDoc.createElement("Groups");
                                base = usersDoc.createElement("Group");
                                if (element.getElementsByTagName("name").item(0).getTextContent().equals(admin.getName())) {
                                    base.setAttribute("id", "Admin");
                                    base.setTextContent(groupName);
                                }else {
                                    base.setAttribute("id", "Member");
                                    base.setTextContent(groupName);
                                }
                                groupRoot.appendChild(base);
                                root.appendChild(groupRoot);
                                break;
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

    /**
     * Remove all children from the root element of the given XML document.
     *
     * @param xml The XML document to clear.
     */
    private void clearXML(Document xml) {
        Element root = xml.getDocumentElement();
        Node child = root.getFirstChild();
        while (child != null) {
            Node nextChild = child.getNextSibling();
            root.removeChild(child);
            child = nextChild;
        }
    }
    /**
     * It searches the XML file for the search term and returns a list of objects that match the search term
     *
     * @param toSearch The search term that the user has entered
     * @return A list of objects - can be Users or Groups :)
     */
    public static List<Object> searchXML(String toSearch) {
        List<Object> searchResults = new ArrayList<>();
        List<String> addedGroups = new ArrayList<>();
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("res/users.xml");
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("User");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nNode = nodeList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) nNode;

                    String userName = element.getElementsByTagName("Username").item(0).getTextContent();
                    if (userName.toLowerCase().contains(toSearch.toLowerCase())) {
                        // If the search term matches a username, create a User object and add it to the search results
                        String id = element.getAttribute("id");
                        String name = element.getElementsByTagName("name").item(0).getTextContent();
                        String age = element.getElementsByTagName("Age").item(0).getTextContent();
                        String password = element.getElementsByTagName("Password").item(0).getTextContent();
                        String status = element.getElementsByTagName("status").item(0).getTextContent();
                        String banStatus = element.getElementsByTagName("BanStatus").item(0).getTextContent();
                        User user = new User(id, name, age, userName, password, status, banStatus);
                        searchResults.add(user);

                    } else {
                        // If the search term doesn't match a username, check if it matches a group name
                        Node groupNode = element.getElementsByTagName("Group").item(0);
                        if (groupNode != null && groupNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element groupElement = (Element) groupNode;
                            String groupName = groupElement.getTextContent();
                            if (groupName.toLowerCase().contains(toSearch.toLowerCase())) {
                                String userId = element.getAttribute("id");
                                userName = element.getElementsByTagName("Username").item(0).getTextContent();
                                User user = new User(userId, null, null, userName, null, null, null);
                                Group group = new Group(groupName, null, user);
                                if (!addedGroups.contains(groupName)) {
                                    searchResults.add(group);
                                    addedGroups.add(groupName);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchResults;
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

    //TODO
    public static void removeUserFromGroup(String nameToDelete, String groupName) {
        try {
            getUsersDoc();
            usersDoc.getDocumentElement().normalize();
            NodeList nodeList = usersDoc.getElementsByTagName("User");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element user = (Element)nodeList.item(i);
                String name = user.getElementsByTagName("Username").item(0).getTextContent();
                if (name.equals(nameToDelete)) {
                    NodeList nodeL = usersDoc.getElementsByTagName("Groups");
                    if (nodeL != null) {
                        for (int j = 0; j < nodeL.getLength(); j++) {
                            Element node = (Element) nodeL.item(j);
                            if (node.getElementsByTagName("Group").getLength() != 0) {
                                String group = node.getElementsByTagName("Group").item(0).getTextContent();
                                System.out.println(group + "\n" + groupName);
                                if (group.equals(groupName)) {
                                    System.out.println(group + "11");
                                    Element parent = (Element) node.getParentNode();
                                    parent.removeChild(node);
                                }
                            }
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