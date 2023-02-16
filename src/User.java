record User(String id, String name, String age, String username, String password) {

    @Override
    public String toString() {
        return "User: " + name + "\nAge: " + age + "\nUsername: " + username;
    }
}

