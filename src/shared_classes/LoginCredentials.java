package shared_classes;

import java.io.Serializable;


public class LoginCredentials implements Serializable {
    private String username;
    private String password;

    public LoginCredentials(String username, String password) {
        this.username = username;
        this.password = password;
        setStatus(username, "online");
    }

    private void setStatus(String username, String status) {
        XMLParse.setStatusOfUser(username, status);
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }



}
