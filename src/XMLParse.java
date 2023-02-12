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
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;


public class XMLParse {

    private String file;

    User newUser;
    public XMLParse(String file) {
        this.file = file;
    }


    public User addUser(String name, String age, String username, String password) {
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
                users = document.getElementsByTagName("Users");
                element = users.item(users.getLength() - 1);
            }
            //if awan ti file f, create new document
            else {
                document = documentBuilder.newDocument();
                element = document.createElement("Users");
                document.appendChild(element);
            }

            //random id exampol <User id="3f99fe2e-a7cb-452f-9bd0-d74ace5eeb7d">
            UUID randomID = UUID.randomUUID();

            //create element user
            Element elementUser = document.createElement("User");
            element.appendChild(elementUser);

            //set id for user
            elementUser.setAttribute("id", String.valueOf(randomID));

            Element nameElement = document.createElement("name");
            nameElement.appendChild(document.createTextNode(name));
            elementUser.appendChild(nameElement);

            Element ageElement = document.createElement("Age");
            ageElement.appendChild(document.createTextNode(age));
            elementUser.appendChild(ageElement);

            Element usernameElement = document.createElement("Username");
            usernameElement.appendChild(document.createTextNode(username));
            elementUser.appendChild(usernameElement);

            Element passwordElement = document.createElement("Password");
            passwordElement.appendChild(document.createTextNode(password));
            elementUser.appendChild(passwordElement);

            newUser = new User(nameElement.getTextContent(), ageElement.getTextContent(),
                    usernameElement.getTextContent(), passwordElement.getTextContent());

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
        return newUser;
    }

    // ikaten tayu dagijay white spaces idjay xml file
    private static void trimWhiteSpace(Node node) {
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