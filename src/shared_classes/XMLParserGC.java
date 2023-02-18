package shared_classes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import shared_classes.GroupChatUsersSample;

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
import java.util.UUID;

public class XMLParserGC {
    private final String file;

    GroupChatUsersSample gcUsers;

    public XMLParserGC(String file) {
        this.file = file;
    }

    public GroupChatUsersSample GroupChat(String groupName, String admin, String members) {
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

            Element groupAdminElement = document.createElement("Admin");
            groupAdminElement.appendChild(document.createTextNode(admin));
            groupElement.appendChild(groupAdminElement);

            Element groupMemberElement = document.createElement("Members");
            groupMemberElement.appendChild(document.createTextNode(members));
            groupElement.appendChild(groupMemberElement);

            gcUsers = new GroupChatUsersSample(groupElement.getAttribute("id"), groupNameElement.getTextContent(),
                    groupAdminElement.getTextContent(), groupMemberElement.getTextContent());

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
        return gcUsers;
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
}