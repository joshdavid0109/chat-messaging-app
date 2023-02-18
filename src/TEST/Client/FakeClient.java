package TEST.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class FakeClient {

    private final Address address;

    public FakeClient() throws UnknownHostException {
        address = new Address();
    }

    public void start() {
        try {
            Socket clientSocket = new Socket(address.getServerAddress(), address.getServerPort());
            BufferedReader streamRdr = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String welcomeMessage = streamRdr.readLine();
            System.out.println(welcomeMessage);
            System.out.println("Connected to server at " + clientSocket.getInetAddress());

            Thread thread = new Thread(() -> {
                try {
                    while (true) {
                        String message = streamRdr.readLine();
                        if (message == null) {
                            // the server has terminated the connection
                            break;
                        }
                        System.out.println(message);
                    }
                } catch (IOException i) {
                    System.err.println("Error reading from server: " + i.getMessage());
                }
            });
            thread.start();

            inputHandler(clientSocket);

            thread.join();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void inputHandler(Socket clientSocket) throws IOException {
        BufferedReader streamRdr = null;
        try {
            streamRdr = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String input;
        while ((input = streamRdr.readLine()) != null) {
            if (input.equals("/quit")) {
                /// send message to server indicating the client is quitting
                clientSocket.getOutputStream().write(("/quit\n").getBytes());
                break;
            }

            clientSocket.getOutputStream().write((input + "\n").getBytes());
        }
    }

    public static void main(String[] args) throws IOException {
        FakeClient fakeClient = new FakeClient();
        fakeClient.start();
    }
}
