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
        user.setGroups(XMLParse.getGroupsOfUser(user));
    }

    public void setGroups(List<Group> groups) {
        this.listOfGroups = groups;
    }
    public List<Group> getGroups() {
        return listOfGroups;
    }
    public void printGroups(){
        System.out.println(this.getName()+"'s groups: ");;
        for (Group listOfGroup : listOfGroups) {
            System.out.println(listOfGroup.getName());
        }
    }

    public Boolean isMember(String groupName){
        for (Group listOfGroup : listOfGroups) {
            if (listOfGroup.getName().equalsIgnoreCase(groupName)) {
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

    @Override
    public String toString() {
        return "USER " + id + "\n" +
                "USERNAME: " + username + "\n" +
                "NAME: " + name + "\n" +
                "AGE: " + age + "\n\n";
    }
}
