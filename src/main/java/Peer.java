public class Peer implements Runnable {
    public int serverPort;
    public int clientPort;
    private String peerAddress;

    public Peer(int serverPort, String peerAddress, int clientPort) {
        this.serverPort = serverPort;
        this.clientPort = clientPort;
        this.peerAddress = peerAddress;
    }

    public void run() {
        try {
            Thread serverThread = new Thread(new Server(serverPort));
            serverThread.start();
            Thread.sleep(5000);

            Thread clientThread = new Thread(new Client(peerAddress, clientPort));
            clientThread.start();

            serverThread.join();
            clientThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
