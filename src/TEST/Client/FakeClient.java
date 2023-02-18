package TEST.Client;

import TEST.Bothsides.FakeRegClientHandler;

public class FakeClient {
    public static void main(String[] args) {
//        XMLParser xmlParser = new XMLParser();
//
//        User newUser = xmlParser.getUser("franz1");
//
//        List<User> userList = xmlParser.getUserList();
//
//        for (User user : userList) {
//            System.out.println(user + "\n");
//        }
        FakeRegClientHandler fakeRegClientHandler = new FakeRegClientHandler();
        fakeRegClientHandler.register();
    }
}
