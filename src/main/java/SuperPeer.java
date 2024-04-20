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
        printMap(allAvailableFiles, " : ");
        printMap(allWantedFiles, " : ");
        // Iterate over each peer's available files
        for (String ipAddress : allAvailableFiles.keySet()) {
            ArrayList<String> has = allAvailableFiles.get(ipAddress);

            // Iterate over other peers' wanted files
            for (String otherIp : allWantedFiles.keySet()) {
                // Skip checking against the same peer
                if (!otherIp.equals(ipAddress)) {
                    ArrayList<String> otherWants = allWantedFiles.get(otherIp);

                    // Check if any file this peer has is wanted by the other peer
                    for (String file : has) {
                        if (otherWants.contains(file)) {
                            System.out.println(ipAddress + " has " + file + " and " + otherIp + " wants it.");
                            // Establish P2P connection between the matching clients
                            establishP2PConnection(ipAddress, otherIp, file);
                            return;
                        }
                    }
                }
            }
        }
    }
/*

    private void establishP2PConnection(String ipAddress1, String ipAddress2, String file) {
        try {
            System.out.println("Trying connection");

            // Assuming fixed ports for P2P communication
            int p2pPort1 = 12345;
            int p2pPort2 = 12346;
            // Create a socket to connect to ipAddress2
            Socket clientSocket = new Socket("172.17.0.3", 12345);
            System.out.println("Connected to " + ipAddress2 + " for P2P communication.");

            // Create a socket to connect to ipAddress1
            Socket peerSocket = new Socket("172.17.0.4", 12346);
            System.out.println("Connected to " + ipAddress1 + " for P2P communication.");

            Thread peerReceiveThread = new Thread(() -> receiveMessages(peerSocket, file));
            peerReceiveThread.start();

            Thread peerSendThread = new Thread(() -> sendMessages(peerSocket, clientSocket, file));
            peerSendThread.start();

            Thread.sleep(1000);

            peerReceiveThread.join();
            peerSendThread.join();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

*/

    private void establishP2PConnection(String ipAddress1, String ipAddress2, String file) {
        try {
            System.out.println("Trying connection");

            // Assuming fixed ports for P2P communication
            int p2pPort1 = 12345;
            int p2pPort2 = 12346;

            // Create a socket to connect to ipAddress2
            Socket clientSocket = new Socket(ipAddress1.substring(1), 12345);
            System.out.println("Informing " + ipAddress1 + " of P2P connection with " + ipAddress2);

            // Create a socket to connect to ipAddress1
            Socket peerSocket = new Socket(ipAddress2.substring(1), 12346);
            System.out.println("Informing " + ipAddress2 + " of P2P connection with " + ipAddress1);

            // Message to be sent
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
                // Read messages from the socket and process them accordingly
                Object message = inputStream.readObject();
                System.out.println("Received message: " + message);
                // Process the received message...
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessages(Socket socket, String fileName) {
        receiveFiles(socket, fileName);
    }

    private void sendMessages(Socket senderSocket, Socket receiverSocket, String filePath) {
        sendFile(senderSocket, filePath);
    }


    private void sendMessages(Socket senderSocket, Socket receiverSocket) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(senderSocket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(receiverSocket.getInputStream());

            while (true) {
                // Read messages from the sender and write them to the receiver
                Object message = inputStream.readObject();
                outputStream.writeObject(message);
                outputStream.flush();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void receiveFiles(Socket socket, String fileName) {
        try {
            InputStream inputStream = socket.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(fileName.substring(0,fileName.length()-4) + "DOWNLOAD.txt");

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            fileOutputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(Socket socket, String filePath) {
        try {
            File file = new File(filePath);
            byte[] buffer = new byte[(int) file.length()];
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            bufferedInputStream.read(buffer, 0, buffer.length);

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(buffer, 0, buffer.length);
            outputStream.flush();

            bufferedInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void printMap(Map<?, ?> map, String action) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " " + action + " " + entry.getValue());
        }
    }

    public static void main(String[] args) {
        SuperPeer superPeer = new SuperPeer();
    }
}
