package shared_classes;

import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class User implements Serializable {
    private String id;
    private String name;
    private String username;
    private String password;
    private String age;
    private String status;
    private String banStatus;
    private List<Group> listOfGroups;

    public User(){
    }

    public User(String id, String name,String age, String username, String password, String status, String banStatus) throws IOException, ParserConfigurationException {
        this.id = id;
        this.name = name;
        this.age = age;
        this.username = username;
        this.password = password;
        this.status = status;
        this.banStatus = banStatus;
        populateGroups(this);
        //System.out.println(Arrays.toString(this.getGroups().toArray()));
    }

    public static void populateGroups(User user){
        try {
            user.setGroups(XMLParse.getGroupsOfUser(user));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    public void setGroups(List<Group> groups) {
        this.listOfGroups = groups;
    }
    public List<Group> getGroups() {
        return listOfGroups;
    }
    public void printGroups(){
        for (Group listOfGroup : listOfGroups) {
            System.out.println(listOfGroup.getName());
        }
    }

    public Boolean isMember(String groupName){
        try {
            for (Group listOfGroup : XMLParse.getGroupsOfUser(this)) {
                if (listOfGroup.getName().equalsIgnoreCase(groupName)) {
                    return true;
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return false;
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
