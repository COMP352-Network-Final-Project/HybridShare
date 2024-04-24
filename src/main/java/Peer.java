public class Peer implements Runnable {
    public int serverPort;
    public int clientPort;
    private String serverAddress;
    public String file;
    public Peer(int serverPort, String serverAddress, int clientPort, String file) {
        this.serverPort = serverPort;
        this.clientPort = clientPort;
        this.serverAddress = serverAddress;
        this.file = file;
    }

    public void run() {
        try {
            Thread server = new Thread(new Server(serverPort));
            server.start();
            Thread.sleep(5000);

            Thread clientThread = new Thread(new Client(serverAddress, clientPort, file));
            clientThread.start();


            server.join();
            clientThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
