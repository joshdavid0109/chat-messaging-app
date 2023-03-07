package shared_classes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class User implements Serializable {
    private String id;
    private transient ObjectOutputStream streamOut;
    private transient ObjectInputStream streamIn;
    private String name;
    private String username;
    private String password;
    private String age;
    private String status;
    private String banStatus;
    private List<Group> listOfGroups;

    public User(){

    }

    public User(String id, String name,String age, String username, String password, String status, String banStatus) throws IOException, ParserConfigurationException, SAXException {
        this.id = id;
        this.name = name;
        this.age = age;
        this.username = username;
        this.password = password;
        this.status = status;
        this.banStatus = banStatus;
        populateGroups(this);
    }

    public static void populateGroups(User user) throws ParserConfigurationException, SAXException, IOException, ParserConfigurationException, SAXException {
        String fileName = "res/users.xml";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        doc.getDocumentElement().normalize();

        NodeList userList = doc.getElementsByTagName("User");

        for (int i = 0; i < userList.getLength(); i++) {
            Node userNode = userList.item(i);
            if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                Element userElement = (Element) userNode;
                String name = userElement.getElementsByTagName("name").item(0).getTextContent();
                if (name.equals(user.getName())) {
                    NodeList groupNodes = userElement.getElementsByTagName("Group");
                    List<Group> groups = new ArrayList<>();
                    for (int j = 0; j < groupNodes.getLength(); j++) {
                        Node groupNode = groupNodes.item(j);
                        if (groupNode.getNodeType() == Node.ELEMENT_NODE) {
                            String group = groupNode.getTextContent();
                            groups.add(new Group(group));
                        }
                    }
                    user.setGroups(groups);
                    break;
                }
            }
        }
    }

    public void setGroups(List<Group> groups) {
        this.listOfGroups = groups;
    }
    public List<Group> getGroups() {
        return listOfGroups;
    }

    public Boolean isMember(String groupName){
        for(int i = 0; i<listOfGroups.size();i++){
            if(listOfGroups.get(i).getName().equals(groupName)){
                return true;
            }
        }
        return false;
    }

    public ObjectOutputStream getOutStream() {
        return this.streamOut;
    }

    public ObjectInputStream getInputStream() {
        return this.streamIn;
    }

    public String getName() {
        return this.name;
    }

    public String getUsername() {
        return username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBanStatus() {
        return banStatus;
    }

    public void setBanStatus(String banStatus) {
        this.banStatus = banStatus;
    }


}
