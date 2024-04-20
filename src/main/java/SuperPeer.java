import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SuperPeer {
    private Map<String, PeerHandler> peerHandlers = new HashMap<>();
    private Map<String, ArrayList<String>> allAvailableFiles = new HashMap<>();
    private Map<String, ArrayList<String>> allWantedFiles = new HashMap<>();

    public SuperPeer() {
        startServer(12347);
    }

    private void startServer(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("SuperPeer started on port " + port + " " + serverSocket.getInetAddress());

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

    public synchronized void findPairings() {
        for (String ipAddress : allAvailableFiles.keySet()) {
            ArrayList<String> has = allAvailableFiles.get(ipAddress);

            for (String otherIp : allWantedFiles.keySet()) {
                if (!otherIp.equals(ipAddress)) {
                    ArrayList<String> otherWants = allWantedFiles.get(otherIp);

                    for (String file : has) {
                        if (otherWants.contains(file)) {
                            System.out.println(ipAddress + " has " + file + " and " + otherIp + " wants it.");
                            establishP2PConnection(ipAddress, otherIp, file);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void establishP2PConnection(String ipAddress1, String ipAddress2, String file) {
        try {
            System.out.println("Trying connection");

            int p2pPort = 12345;

            Socket clientSocket = new Socket(ipAddress1.substring(1), p2pPort);
            System.out.println("Informing " + ipAddress1 + " of P2P connection with " + ipAddress2);

            Socket peerSocket = new Socket(ipAddress2.substring(1), p2pPort);
            System.out.println("Informing " + ipAddress2 + " of P2P connection with " + ipAddress1);

            String message1 = "IP " + ipAddress2 + " NEEDS " + file;
            String message2 = "IP " + ipAddress1 + " HAS " + file;

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


    private void receiveMessages(Socket socket) {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            while (true) {
                Object message = inputStream.readObject();
                System.out.println("Received message: " + message);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void printMap(Map<?, ?> map, String sep) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " " + sep + " " + entry.getValue());
        }
    }

    public static void main(String[] args) {
        SuperPeer superPeer = new SuperPeer();
    }
}
