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

    /**
     *
     * @param socket inherited socket from the superpeer that has the connection to the peer
     * @param superPeer reference to the superpeer
     * @param clientIP IP address of the client that is associated with this peer handler
     */
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

    /**
     * main running body of the peer handler class that process the file names that are sent from
     * the peers, when they receive a file name they add the name and IP address of the peer to a
     * map and send it up to the superpeer
     */
    @Override
    public void run() {
        try {
            String message;

            while ((message = in.readLine()) != null) {
                try {
                    String[] parts = message.split(" ");
                    if (parts.length == 3) {
                        String action = parts[0];
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

    /**
     * Sends all the available and wanted files supplied by the connected peer back
     * up to the super peer
     */
    public void sendFilesToSuperPeer() {
        superPeer.receiveFiles(availableFiles, wantedFiles, clientIP);
    }

    /**
     *
     * @param ipAddress IP of the client that owns this file
     * @param filename name of the file that is owned
     *
     *  this method also checks to make sure that this entry is not already entered
     */
    public synchronized void addAvailableFile(String ipAddress, String filename) {
        if (!availableFiles.containsKey(ipAddress)) {
            availableFiles.put(ipAddress, new ArrayList<>());
        }
        ArrayList<String> files = availableFiles.get(ipAddress);
        if (!files.contains(filename)) {
            files.add(filename);
        }
    }

    /**
     *
     * @param ipAddress IP of the client that needs the file
     * @param filename the name of the file needed
     *
     *  this method also checks to make sure this entry is not already entered
     */
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
