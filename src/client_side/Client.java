package client_side;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket client;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private boolean done;
    ObjectInputStream objectInputStream;
    Scanner scanner = new Scanner(System.in);
    private int port;

    public Client() {
        boolean validPort = false;
        while (!validPort) {
            try {
                System.out.print("enter port: ");
                port = Integer.parseInt(scanner.nextLine());
                client = new Socket("localhost", port);
                printWriter = new PrintWriter(client.getOutputStream(), true);
                bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                InputHandler inHandler = new InputHandler();
                Thread t = new Thread(inHandler);
                t.start();
                String inMessage;

                while ((inMessage = bufferedReader.readLine()) != null) {
                    System.out.println(inMessage);
                }
                validPort = true;
            } catch (NumberFormatException e) {
                System.out.println("valid number pls");
                System.out.println(e.getMessage());
            } catch (NullPointerException e) {
                System.out.println("INPUT A VALID PORT");
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println("ioexcepadwawdad");
                System.out.println(e.getMessage());
            }
        }
    }


    public void shutdown() {
        done = true;
        try {
            bufferedReader.close();
            printWriter.close();
            if (!client.isClosed()) {
                client.close();
            }
        } catch (IOException ignored) {

        }
    }

    class InputHandler implements Runnable {
        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {
                    String message = inReader.readLine();
                    if (message.equals("/quit")) {
                        inReader.close();
                        shutdown();
                    } else {
                        printWriter.println(message);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
