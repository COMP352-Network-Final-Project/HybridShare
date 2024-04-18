import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PeerHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private SuperPeer superPeer;

    public PeerHandler(Socket socket, SuperPeer superPeer) {
        this.socket = socket;
        this.superPeer = superPeer;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String message;

            while ((message = in.readLine()) != null) {


                try {
                    String clientIP = socket.getInetAddress().getHostAddress();
                    String messageString = message.toString();
                    String[] parsed = messageString.split(";");
                    if(parsed.length == 2) {
                        int port = Integer.parseInt(parsed[0]);
                        String forwardedMessage = parsed[1];
                        Socket outSocket = new Socket("0.0.0.0", port);
                        PrintWriter outWriter = new PrintWriter(outSocket.getOutputStream(), true);
                        outWriter.println(forwardedMessage);
                        System.out.println("Message forwarded to peer: " + port + " " + parsed[1]);
                    }else{
                        System.out.println("Message from peer: " + message);
                    }
                }catch (Exception e){

                }

                sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
