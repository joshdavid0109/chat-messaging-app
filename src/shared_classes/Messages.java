package shared_classes;

public record Messages<T> (String sender, String messageType, T message){

    @Override
    public String toString() {
        //return this.userName+": "+ message;
        return sender + ": " + message;
    }
}