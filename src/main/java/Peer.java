public class Peer implements Runnable {
    public int serverPort;
    public int superPort;
    private String serverAddress;
    public String file;

    /**
     *
     * @param serverPort the port to run the P2P connection server on, this is 12345 by default
     *                   of the BasicP2P class
     * @param serverAddress the IP address of the SuperPeer to connect to
     * @param superPort the Port of the SuperPeer to connect to
     * @param file the file that this client would like to receive
     */
    public Peer(int serverPort, String serverAddress, int superPort, String file) {
        this.serverPort = serverPort;
        this.superPort = superPort;
        this.serverAddress = serverAddress;
        this.file = file;
    }

    /**
     * main running body of the Peer class where we initiate the Server and Client of the peer
     */
    public void run() {
        try {
            Thread server = new Thread(new Server(serverPort));
            server.start();

            //sleep here to make sure the server is running before we try to connect
            Thread.sleep(5000);
            Thread clientThread = new Thread(new Client(serverAddress, superPort, file));
            clientThread.start();

            server.join();
            clientThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
