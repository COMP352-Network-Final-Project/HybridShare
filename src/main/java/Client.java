import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.*;
public class Client implements Runnable {
    private int port;
    private String peerAddress;


    public String [] files;
    public Client(String peerAddress, int port, String [] files) {
        this.port = port;
        this.peerAddress = peerAddress;
        this.files = files;

    }
    public void run() {
        try {
            Socket socket = new Socket(peerAddress, port);

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();


            Runnable sendMessageTask = () -> {
                try {
                    String messageH = "HAS " + socket.getInetAddress().toString() +  " " + files[0];
                    String messageW = "WANTS " + socket.getInetAddress().toString() +  " " + files[1];
                    out.println(messageH);
                    out.println(messageW);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            executor.scheduleAtFixedRate(sendMessageTask, 0, 5, TimeUnit.SECONDS);

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