package groupConference;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class sampleServer {

    // establish a socket
    private ServerSocket serverSocket;

    // constructor
    public sampleServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * It waits for a client to connect to the server, and when a client connects, it creates a new thread for that client
     */
    public void startServer() {

        while (true) {
            try {
                // Checking if the server socket is closed. If it is closed, it will not accept any new client.
                while (serverSocket.isClosed()) {

                    // Waiting for a client to connect to the server.
                    Socket socket = serverSocket.accept();
                    System.out.println("A new member is added");

                    ClientHandler clientHandler = new ClientHandler(socket);

                    Thread thread = new Thread(clientHandler);
                    thread.start();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * It closes the server socket
     */
    public void closeServerSocket() {
        try {
            if(serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The main function creates a new server socket on port 8888, creates a new groupConference.sampleServer object, and then starts the
     * server
     */
    public static void main(String[] args) throws IOException {
        // It creates a new server socket on port 8888.
        ServerSocket serverSocket = new ServerSocket(1234);
        sampleServer server = new sampleServer(serverSocket);
        server.startServer();
    }
}
