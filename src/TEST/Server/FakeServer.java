package TEST.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class FakeServer {
    private final int portNum;
    private final List<ClientHandler> handlers = new ArrayList<>();

    public FakeServer(int portNum) {
        this.portNum = portNum;
    }

    public static void main(String[] args) throws IOException {
        FakeServer server = new FakeServer(7777);
        server.start();
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(portNum)) {
            System.out.println("Server started on port " + portNum);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                handlers.add(clientHandler);

                clientHandler.start();
            }
        }
    }

    private void broadcast(String msg, ClientHandler sender) {
        // send message to all clients except sender
        for (ClientHandler clientHandler : handlers) {
            if (clientHandler != sender) {
                clientHandler.sendMessage(msg);
            }
        }
    }

    private class ClientHandler extends Thread {
        private final Socket clientSocket;
        private final BufferedReader reader;
        private final PrintWriter writer;

        public ClientHandler(Socket s) throws IOException {
            this.clientSocket = s;
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);

            writer.println("Welcome to the server!");
        }

        public void sendMessage(String msg) {
            writer.println(msg);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String message = reader.readLine();
                    if (message == null || message.equals("/quit")) {
                        // client has disconnected
                        break;
                    }

                    System.out.println("Message received from client: " + message);

                    broadcast(message, this);
                }
            } catch (IOException e) {
                System.err.println("Error in ClientHandler: " + e.getMessage());
            } finally {
                // remove client from list of clients
                handlers.remove(this);
                System.out.println("Client has disconnected: " + clientSocket);
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
            }
        }
    }
}
