package TEST.Bothsides;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

public class FakeXMLParser {
    private static Document usersDoc;
    private static Document msgDoc;

    private void getUsersDoc() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File f = new File("src/TEST/fakeres/FakeUsers.xml");
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

    private void getMsgDoc() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File f = new File("src/TEST/fakeres/FakeUsers.xml");
            Node element;
            if (f.exists()) {
                msgDoc = db.parse(f);
            } else {
                msgDoc = db.newDocument();
                element = msgDoc.createElement("Messages");
                msgDoc.appendChild(element);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public FakeUser getUser(String userName) {
        getUsersDoc();
        usersDoc.getDocumentElement().normalize();
        NodeList nodeList = usersDoc.getElementsByTagName("User");
        FakeUser fakeUser = new FakeUser();
        Element element;

        for (int i = 0; i < nodeList.getLength(); i++) {
            element = (Element) nodeList.item(i);
            if (element.getElementsByTagName("Username").item(0).getTextContent().equals(userName)) {
                fakeUser.setName(element.getElementsByTagName("name").item(0).getTextContent());
                fakeUser.setAge(element.getElementsByTagName("Age").item(0).getTextContent());
                fakeUser.setUserName(element.getElementsByTagName("Username").item(0).getTextContent());
                fakeUser.setPassword(element.getElementsByTagName("Password").item(0).getTextContent());
                System.out.println(fakeUser);
                break;
            }
        }

        return fakeUser;
    }

    public List<FakeUser> getUserList() {
        getUsersDoc();
        usersDoc.getDocumentElement().normalize();
        NodeList nodeList = usersDoc.getElementsByTagName("User");
        List<FakeUser> fakeUserList = new ArrayList<>();
        Element element;

        for (int i = 0; i < nodeList.getLength(); i++) {
            element = (Element) nodeList.item(i);
            FakeUser temp = new FakeUser();
            temp.setName(element.getElementsByTagName("name").item(0).getTextContent());
            temp.setAge(element.getElementsByTagName("Age").item(0).getTextContent());
            temp.setUserName(element.getElementsByTagName("Username").item(0).getTextContent());
            temp.setPassword(element.getElementsByTagName("Password").item(0).getTextContent());

            fakeUserList.add(temp);
        }

        return fakeUserList;
    }

    // THIS IS A TEST METHOD
    // will be used by server
    // is this correct
    public void writeUser(FakeUser newFakeUser) {
        try {
            List<FakeUser> fakeUserList = getUserList();
            UUID userID = UUID.randomUUID();
            clearXML(usersDoc);
            fakeUserList.add(newFakeUser);

            for (FakeUser fakeUser : fakeUserList) {
                Element usersTag = usersDoc.getDocumentElement();
                Element nUser = usersDoc.createElement("User");
                nUser.setAttribute("id", String.valueOf(userID));
                Element nameElement = usersDoc.createElement("name");
                nameElement.setTextContent(fakeUser.getName());
                Element ageElement = usersDoc.createElement("Age");
                ageElement.setTextContent(fakeUser.getAge());
                Element usrnmElement = usersDoc.createElement("Username");
                usrnmElement.setTextContent(fakeUser.getUserName());
                Element pswdElement = usersDoc.createElement("Password");
                pswdElement.setTextContent(fakeUser.getPassword());

                nUser.appendChild(nameElement);
                nUser.appendChild(ageElement);
                nUser.appendChild(usrnmElement);
                nUser.appendChild(pswdElement);

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
            StreamResult streamResult = new StreamResult(new File("src/TEST/fakeres/FakeUsers.xml"));
            transformer.transform(source, streamResult);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Document clearXML(Document xml) {
        Element root = xml.getDocumentElement();
        Node child = root.getFirstChild();
        while (child != null) {
            Node nextChild = child.getNextSibling();
            root.removeChild(child);
            child = nextChild;
        }

        return xml;
    }
}
