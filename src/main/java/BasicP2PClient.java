public class BasicP2PClient {
    public static void main(String[] args) {
        Thread peerThread = new Thread(new Peer(12346, "0.0.0.0", 12347));
        peerThread.start();
    }
}
