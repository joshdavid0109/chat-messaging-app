import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.net.IDN;

public class Main {
    private final static String file = "D:\\2222551\\javachattest\\res\\users.xml";
    public static void main(String[] args) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            Element element = document.createElement("Users");
            document.appendChild(element);

            // USER 1
            Element elementUser1 = document.createElement("User1");
            element.appendChild(elementUser1);

            Element user1 = document.createElement("name");
            user1.appendChild(document.createTextNode("Joshua"));
            elementUser1.appendChild(user1);

            Element user1Age = document.createElement("Age");
            user1Age.appendChild(document.createTextNode("20"));
            elementUser1.appendChild(user1Age);


            I


            // USER 2
            Element user2 = document.createElement("user2");
            user2.appendChild(document.createTextNode("Maervin"));
            document.appendChild(user2);







            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(file));

            transformer.transform(domSource, streamResult);




        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }

    }
}