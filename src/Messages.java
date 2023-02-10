public class Messages {

    private String userName;
    private String message;
    private String date;

    public Messages(String userName, String message) {
        this.userName = userName;
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        //return this.userName+": "+ message;
        return userName + ": " + message;
    }
}