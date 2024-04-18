import java.io.*;
import java.net.*;

public class P2PClient {
    public static void main(String[] args) {
        try {
            // Server's address and port
            InetAddress serverAddress = InetAddress.getByName(args[0]);
            int serverPort = Integer.parseInt(args[1]);

            // Create server sockets with port 0 to let the OS choose an available port
            ServerSocket serverSocketA = new ServerSocket(0);
            ServerSocket serverSocketB = new ServerSocket(0);

            // Get the dynamically assigned port numbers
            int clientAPort = serverSocketA.getLocalPort();
            int clientBPort = serverSocketB.getLocalPort();

            // Exchange connection information with the server
            String clientAInfo = "A," + clientAPort; // Format: "client_id,port"
            String clientBInfo = "B," + clientBPort; // Format: "client_id,port"

            // Send Client A's info to the server
            try (Socket clientSocket = new Socket(serverAddress, serverPort)) {
                OutputStream outputStream = clientSocket.getOutputStream();
                outputStream.write(clientAInfo.getBytes());
            }

            // Send Client B's info to the server
            try (Socket clientSocket = new Socket(serverAddress, serverPort)) {
                OutputStream outputStream = clientSocket.getOutputStream();
                outputStream.write(clientBInfo.getBytes());
            }

            // Close the server sockets
            serverSocketA.close();
            serverSocketB.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
