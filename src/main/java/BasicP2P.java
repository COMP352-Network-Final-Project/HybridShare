;public class BasicP2P {
    /**
     * @param args P2P connection port is 12345 by default
     */
    public static void main(String[] args) {
        Thread peerThread = new Thread(new Peer(12345,args[1] ,Integer.parseInt(args[0]), args[2]));
        peerThread.start();
    }
}