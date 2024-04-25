import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class SuperPeer {
    private Map<String, PeerHandler> peerHandlers = new HashMap<>();
    private Map<String, ArrayList<String>> allAvailableFiles = new HashMap<>();
    private Map<String, ArrayList<String>> allWantedFiles = new HashMap<>();
    private Set<String> processedPairs = new HashSet<>();

    /**
     * initiates the server on port 12347
     */
    public SuperPeer() {
        startServer(12347);
    }

    /**
     * Starts the SuperPeer server waits for incoming connections.
     * When a connection is established a new instance of Peer Handler is invoked
     * and passed the socket of the client, an instance of the SuperPeer, and the
     * client IP
     * @param port port to start the superpeer server on
     */
    private void startServer(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("SuperPeer started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New peer connected: " + clientSocket.getInetAddress() + clientSocket.getPort());
                PeerHandler peerHandler = new PeerHandler(clientSocket, this, clientSocket.getInetAddress().toString());
                peerHandlers.put(clientSocket.getInetAddress().toString(), peerHandler);
                Thread thread = new Thread(peerHandler);
                thread.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is invoked by the PeerHandlers to inform the SuperPeer of the files
     * that are owned and needed by the connected peers. This method is synchronized as
     * multiple PeerHandlers may try to invoke it at the same time.
     *
     * @param availableFiles Map of available files from the client
     * @param wantedFiles Map of wanted files from the client
     * @param clientIP IP of the client
     */
    public synchronized void receiveFiles(Map<String, ArrayList<String>> availableFiles,
                                          Map<String, ArrayList<String>> wantedFiles,
                                          String clientIP) {
        System.out.println("Receiving files");
        allAvailableFiles.put(clientIP, new ArrayList<>(availableFiles.getOrDefault(clientIP, new ArrayList<>())));
        allWantedFiles.put(clientIP, new ArrayList<>(wantedFiles.getOrDefault(clientIP, new ArrayList<>())));
        printMap(allAvailableFiles, " : ");
        printMap(allWantedFiles, " : ");
        findPairings();
    }

    /**
     * This method is used to find matches between pairs who have files
     * and those that need them. It first iterates through the IP addresses
     * associated with owning a file and then adds all of those files to
     * a list. Then it iterates of all the IP's associated with wanting a file
     * and adds these wanted files to another list. Finally it iterates through
     * the available files and if there is a match it will call establishP2PConnection and
     * add that pair of IPs and file to a list of already proccessed pairs,
     * if a pair has been processed it will not be processed again
     * and a connection will not be established
     */
    public synchronized void findPairings() {
        for (String ipAddress : allAvailableFiles.keySet()) {
            ArrayList<String> has = allAvailableFiles.get(ipAddress);

            for (String otherIp : allWantedFiles.keySet()) {
                if (!otherIp.equals(ipAddress)) {
                    ArrayList<String> otherWants = allWantedFiles.get(otherIp);

                    for (String file : has) {
                        if (otherWants.contains(file)) {
                            String pair = ipAddress + ":" + otherIp + ":" + file;
                            if (!processedPairs.contains(pair)) {
                                System.out.println(ipAddress + " has " + file + " and " + otherIp + " wants it.");
                                establishP2PConnection(ipAddress, otherIp, file);
                                processedPairs.add(pair);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * This method connects to both of the clients' servers and
     * sends them a message informing them that there is a
     * peer that wants/needs a file
     * @param ipAddress1 IP of the client that ows the file
     * @param ipAddress2 IP of the client that wants the file
     * @param file the name of the file to be shared
     */
    private void establishP2PConnection(String ipAddress1, String ipAddress2, String file) {
        try {
            System.out.println("Trying connection");

            int p2pPort = 12345;

            Socket clientSocket = new Socket(ipAddress1.substring(1), p2pPort);
            System.out.println("Informing " + ipAddress1 + " of P2P connection with " + ipAddress2);

            Socket peerSocket = new Socket(ipAddress2.substring(1), p2pPort);
            System.out.println("Informing " + ipAddress2 + " of P2P connection with " + ipAddress1);

            String message1 = "WANTS " + ipAddress2 + " " + file;
            String message2 = "HAS " + ipAddress1 + " " + file;

            PrintWriter out1 = new PrintWriter(clientSocket.getOutputStream(), true);
            PrintWriter out2 = new PrintWriter(peerSocket.getOutputStream(), true);

            out1.println(message1);
            out2.println(message2);

            clientSocket.close();
            peerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Helper function to print out a map, where the key and value are separated
     * by a string
     * @param map map to print
     * @param sep separator to divide the key and value with
     */
    public static void printMap(Map<?, ?> map, String sep) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " " + sep + " " + entry.getValue());
        }
    }

    public static void main(String[] args) {
        SuperPeer superPeer = new SuperPeer();
    }
}
