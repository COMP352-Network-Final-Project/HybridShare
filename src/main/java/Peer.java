public class Peer implements Runnable {
    public int serverPort;
    public int clientPort;
    private String peerAddress;
    public String [] files;
    public Peer(int serverPort, String peerAddress, int clientPort, String fileH, String fileW) {
        this.serverPort = serverPort;
        this.clientPort = clientPort;
        this.peerAddress = peerAddress;
        this.files = new String[]{fileH, fileW};
    }

    public void run() {
        try {
            Thread serverThread = new Thread(new Server(serverPort));
            serverThread.start();
            Thread.sleep(5000);

            Thread clientThread = new Thread(new Client(peerAddress, clientPort, files));
            clientThread.start();


            serverThread.join();
            clientThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
