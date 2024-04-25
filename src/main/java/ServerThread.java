import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class ServerThread implements Runnable {
    private Socket socket;
    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    /**
     * main running body of the ServerThread, here the peer process connections with another
     * peer when sharing a file.
     * It waits for incoming messages and stores them both as a StringBuilder object and
     * as a ByteArray, this way the messages can be processed lexically when they contain
     * information, and handled as raw data when files are being transmitted
     */
    public void run() {
        try {
            InputStream is = socket.getInputStream();
            ByteArrayOutputStream binaryData = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                binaryData.write(buffer, 0, bytesRead);
            }

            byte[] fileData = binaryData.toByteArray();
            String message = new String(fileData, StandardCharsets.UTF_8);

            System.out.println(message);

            /**
             * if an incoming message begins with SENDING then it is the beginning
             * of a file transmission, and we should create a new file and write the
             * data that follows that keyword token to the file
             */
            String[] parsed = message.split(" ");
            if (message.startsWith("SENDING")) {
                parsed = message.split(":");
                String filename = parsed[0].split(" ")[1];

                int newlineIndex = message.indexOf('\n');
                if (newlineIndex != -1) {
                    fileData = Arrays.copyOfRange(fileData, newlineIndex + 1, fileData.length);
                }
                try (FileOutputStream fos = new FileOutputStream("share/"+filename)) {
                    fos.write(fileData);
                }

            }
            if (parsed.length == 3 && message.startsWith("WANTS")) {
                String ip = parsed[1];
                String file = parsed[2];
                handleNeeds(ip, file);
            }

            is.close();
            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param ip the IP address of the peer that would like a file
     * @param file the file that is wanted by a peer
     * @throws IOException
     * @throws InterruptedException
     *
     * This method opens the peer's p2p socket and waits briefly before it
     * begins transmitting the file
     */
    private void handleNeeds(String ip, String file) throws IOException, InterruptedException {
        Socket p2pSocket = new Socket(ip.substring(1),12345);
        Thread.sleep(3000);
        sendFile(p2pSocket, file.trim());
    }

    /**
     * This method appends SENDING and filePath to the data of the file
     * so that the client will know how to process it, and then sends the file
     * @param socket socket of the receiving peer
     * @param filePath path to the file being sent
     */
    private void sendFile(Socket socket, String filePath) {
        try {
            File file = new File("share/"+filePath);
            byte[] buffer = new byte[(int) file.length()];
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            bufferedInputStream.read(buffer, 0, buffer.length);

            OutputStream outputStream = socket.getOutputStream();

            String pre = "SENDING " + filePath + ":\n";
            outputStream.write(pre.getBytes());
            outputStream.flush();

            outputStream.write(buffer, 0, buffer.length);
            outputStream.flush();

            bufferedInputStream.close();
            fileInputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
