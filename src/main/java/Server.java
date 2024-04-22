import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    int port;
    public Server(int port) {
        this.port = port;
    }

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