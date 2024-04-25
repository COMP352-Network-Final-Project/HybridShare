import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    int port;

    /**
     *
     * @param port the peer server port, 12345 by default
     */
    public Server(int port) {
        this.port = port;
    }


    /**
     * the main running body of the server that accepts incoming connections
     * and spawns instances of the ServerThread class when they connect
     */
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server running...");
            while (true) {
                Socket socket = serverSocket.accept();

                Thread t = new Thread(new ServerThread(socket));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}