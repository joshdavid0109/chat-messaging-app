/*
package shared_classes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class XMLParserGC {
    private final String file;

    GroupChatUsersSample gcUsers;

    public XMLParserGC(String file) {
        this.file = file;
    }

    public void GroupChat(String groupName, ArrayList<User> members) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document;
            File f = new File(file);
            NodeList users;
            Node element;

            if (f.exists()) {
                document = documentBuilder.parse(file);
                users = document.getElementsByTagName("GroupChat");
                element = users.item(users.getLength() - 1);
            }

            else {
                document = documentBuilder.newDocument();
                element = document.createElement("GroupChat");
                document.appendChild(element);
            }

            UUID randomID = UUID.randomUUID();

            //create element user
            Element groupElement = document.createElement("GroupChat");
            element.appendChild(groupElement);

            //set id for user
            groupElement.setAttribute("id", String.valueOf(randomID));

            Element groupNameElement = document.createElement("GroupName");
            groupNameElement.appendChild(document.createTextNode(groupName));
            groupElement.appendChild(groupNameElement);

            Element memberElement = document.createElement("Members");
            groupElement.appendChild(memberElement);

            for (int i = 0; i < members.size(); i++) {
                User member = members.get(i);
                //create element user
                Element elementUser = document.createElement("User");
                element.appendChild(elementUser);

                    //set id for user
                elementUser.setAttribute("Group", "GroupName");
                elementUser.setAttribute("id", String.valueOf(member.id()));

                Element nameElement = document.createElement("name");
                nameElement.appendChild(document.createTextNode(member.name()));
                elementUser.appendChild(nameElement);

                Element ageElement = document.createElement("Age");
                ageElement.appendChild(document.createTextNode(member.age()));
                elementUser.appendChild(ageElement);

                Element usernameElement = document.createElement("Username");
                usernameElement.appendChild(document.createTextNode(member.username()));
                elementUser.appendChild(usernameElement);

                Element passwordElement = document.createElement("Password");
                passwordElement.appendChild(document.createTextNode(member.password()));
                elementUser.appendChild(passwordElement);

            }



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
}*/
