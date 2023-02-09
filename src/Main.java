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
import java.util.Scanner;
import java.util.UUID;

public class Main {
    private final static String file = "res/users.xml";
    public static void main(String[] args) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document;
            File f = new File(file);

            NodeList users = null;

            
            if (f.exists()){
                document = documentBuilder.parse(file);
                users = document.getElementsByTagName("Users");
            } else {
                document = documentBuilder.newDocument();
                users = document.getElementsByTagName("Users");
            }

            Node element = users.item(users.getLength()-1);
            UUID randomID = UUID.randomUUID();

            // USER 1
            byte choice = 0;
            Scanner scn = new Scanner(System.in);

                Element elementUser = document.createElement("User" + (users.getLength() + 1));
                element.appendChild(elementUser);

                elementUser.setAttribute("id", String.valueOf(randomID));

                Element name = document.createElement("name");
                System.out.print("Enter name: ");
                name.appendChild(document.createTextNode(scn.nextLine()));

                elementUser.appendChild(name);

                Element userAge = document.createElement("Age");
                System.out.print("Enter age: ");
                userAge.appendChild(document.createTextNode(scn.nextLine()));
                elementUser.appendChild(userAge);

                Element userName = document.createElement("Username");
                System.out.print("Enter username: ");
                userName.appendChild(document.createTextNode(scn.nextLine()));
                elementUser.appendChild(userName);

                Element password = document.createElement("Password");
                System.out.print("Enter password: ");
                password.appendChild(document.createTextNode(scn.nextLine()));
                elementUser.appendChild(password);

                System.out.print("Do you want to continue adding users?");
                choice = scn.nextByte();




            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no" );
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(file));
            transformer.transform(domSource, streamResult);




        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }

    }
}