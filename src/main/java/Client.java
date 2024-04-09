import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {
    private int port;
    private String peerAddress;

    private String [] Files_has;
    private String [] Files_wants;
    public Client(String peerAddress, int port, String [] files_has, String [] files_wants){
        this.port = port;
        this.peerAddress = peerAddress;
    }
    public void run() {
        try {
            Socket socket = new Socket(peerAddress, port);

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String message;
            while ((message = userInput.readLine()) != null) {
                out.println(message);
            }
            userInput.close();
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}