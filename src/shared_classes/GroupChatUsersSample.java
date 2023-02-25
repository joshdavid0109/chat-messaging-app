package shared_classes;

import java.util.List;

public record GroupChatUsersSample(String id, String groupName, String admin, List<User> members) implements Runnable{

    static StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void run() {
        for (User user: members) {
            stringBuilder.append(user.name()).append("\n").append(user.username()).append("\n").append(user.status());
        }
    }

    @Override
    public String toString() {
        return "GroupConferenceName " + groupName + "\nAdmin: " + admin + "\nUsername: " + stringBuilder;
    }
}

