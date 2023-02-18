package TEST.Client;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Address {
    private static final String HOSTNAME = "localhost";
    private static final int PORTNUMBER = 7777;

    private InetAddress serverAddress;
    private int serverPort;

    public Address() throws UnknownHostException {
        this.serverAddress = InetAddress.getByName(HOSTNAME);
        this.serverPort = PORTNUMBER;
    }

    public InetAddress getServerAddress() {return serverAddress;}
    public int getServerPort() {return serverPort;}

    @Override
    public String toString() {
        return serverAddress.getHostAddress() + ":" + serverPort;
    }
}
