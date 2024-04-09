import java.io.*;
import java.net.*;

public class BasicP2P {
    public static void main(String[] args) {
        Thread peerThread = new Thread(new Peer(Integer.parseInt(args[0]),"0.0.0.0" ,Integer.parseInt(args[1])));
        peerThread.start();
    }
}
