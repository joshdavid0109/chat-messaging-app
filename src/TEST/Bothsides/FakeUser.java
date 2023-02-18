package TEST.Bothsides;

public class FakeUser {
    private String name;
    private String age;
    private String userName;
    private String password;

    public FakeUser() {}

    public FakeUser(String n, String a, String un, String p) {
        this.name = n;
        this.age = a;
        this.userName = un;
        this.password = p;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toString() {
        return "User: " + name + "\nAge: " + age + "\nUsername: " + userName + "\nPassword: " + password;
    }
}


