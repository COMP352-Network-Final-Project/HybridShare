;public class BasicP2P {
    public static void main(String[] args) {
        Thread peerThread = new Thread(new Peer(12345,args[1] ,Integer.parseInt(args[0]), args[2]));
        peerThread.start();
    }
}