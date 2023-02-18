package shared_classes;

public record GroupChatUsersSample(String id, String groupName, String admin, String member) {
    @Override
    public String toString() {
        return "GroupConferenceName " + groupName + "\nAdmin: " + admin + "\nUsername: " + member;
    }
}

