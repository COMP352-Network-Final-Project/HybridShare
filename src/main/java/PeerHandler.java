import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PeerHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private SuperPeer superPeer;
    private Map<String, ArrayList<String>> availableFiles = new HashMap<>();
    private Map<String, ArrayList<String>> wantedFiles = new HashMap<>();
    private String clientIP;

    public PeerHandler(Socket socket, SuperPeer superPeer, String clientIP) {
        this.socket = socket;
        this.superPeer = superPeer;
        this.clientIP = clientIP;

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
                    String[] parts = message.split(" ");
                    if (parts.length == 3) {
                        String ipAddress = parts[0];
                        String action = parts[1];
                        String filename = parts[2];

                        if (action.equals("HAS")) {
                            addAvailableFile(clientIP, filename);

                        } else if (action.equals("WANTS")) {
                            addWantedFile(clientIP, filename);

                        }
                        System.out.println();

                        sendFilesToSuperPeer();
                        superPeer.findPairings();


                    } else {
                        System.out.println("Invalid message format: " + message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
    public void sendFilesToSuperPeer() {
        superPeer.receiveFiles(availableFiles, wantedFiles, clientIP);
    }
    public synchronized void addAvailableFile(String ipAddress, String filename) {
        if (!availableFiles.containsKey(ipAddress)) {
            availableFiles.put(ipAddress, new ArrayList<>());
        }
        ArrayList<String> files = availableFiles.get(ipAddress);
        if (!files.contains(filename)) {
            files.add(filename);
        }
    }

    public synchronized void addWantedFile(String ipAddress, String filename) {
        if (!wantedFiles.containsKey(ipAddress)) {
            wantedFiles.put(ipAddress, new ArrayList<>());
        }
        ArrayList<String> files = wantedFiles.get(ipAddress);
        if (!files.contains(filename)) {
            files.add(filename);
        }
    }

}
