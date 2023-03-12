package shared_classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Group implements Serializable {
    private String name;
    private List<User> members;
    private User admin;

    public Group(String name) {
        this.name = name;
        members = new ArrayList<>();
    }

    public Group(String groupname, List<User> members) {
        this.name = groupname;
        this.members = members;
    }

    public Group(String name, User creator) {
        this.name = name;
        this.admin = creator;
        members = new ArrayList<>();
    }

    public Group(String name, User creator, List<User> members) {
        this.name = name;
        this.admin = creator;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public List<User> getMembers() {
        return members;
    }

    public void addMember(User member) {
        members.add(member);
    }

    public void removeMember(User member) {
        members.remove(member);
    }

    public boolean containsMember(User member) {
        return members.contains(member);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
