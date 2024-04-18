import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SuperPeer {
    private Map<String, PeerHandler> peerHandlers = new HashMap<>();

    public SuperPeer() {
        startServer(12347);
    }

    private void startServer(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("SuperPeer started on port " + port + " " + serverSocket.getInetAddress());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New peer connected: " + clientSocket.getInetAddress() + " " + clientSocket.getPort());
                PeerHandler peerHandler = new PeerHandler(clientSocket, this);
                peerHandlers.put(clientSocket.getInetAddress().getHostAddress(), peerHandler);
                Thread thread = new Thread(peerHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        SuperPeer superPeer = new SuperPeer();
    }
}
