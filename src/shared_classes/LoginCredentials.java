package shared_classes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import server_side.Server;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.Serializable;


public class LoginCredentials implements Serializable {
    private String username;
    private String password;

    public LoginCredentials(String username, String password) {
        this.username = username;
        this.password = password;
        setStatus(username, password);
    }

    private void setStatus(String username, String password) {
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
            boolean foundUser = false;;
                for (int i = 0; i < nodelist.getLength(); i++) {
                    element = (Element) nodelist.item(i);
                    String uname = element.getElementsByTagName("Username").item(0).getTextContent();
                    String pass = element.getElementsByTagName("Password").item(0).getTextContent();
                    System.out.println(uname);
                    if (uname.equals(username) && pass.equals(password)) {
                        element.getElementsByTagName("status").item(0).setTextContent("online");
                        Server.updateXML(nodelist, document);
                        break;
                    }
                }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }



}
