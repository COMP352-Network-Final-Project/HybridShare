import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;



public class ServerThread implements Runnable {
    private Socket socket;
    public ServerThread(Socket socket) {
        this.socket = socket;
    }
    public void run() {
        try {
            InputStream is = socket.getInputStream();
            ByteArrayOutputStream binaryData = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                binaryData.write(buffer, 0, bytesRead);
            }

            byte[] imageData = binaryData.toByteArray();
            String message = new String(imageData, StandardCharsets.UTF_8);

            System.out.println(message);

            String[] parsed = message.split(" ");
            if (message.startsWith("SENDING")) {
                parsed = message.split(":");
                String filename = parsed[0].split(" ")[1];

                int newlineIndex = message.indexOf('\n');
                if (newlineIndex != -1) {
                    imageData = Arrays.copyOfRange(imageData, newlineIndex + 1, imageData.length);
                }
                try (FileOutputStream fos = new FileOutputStream(filename)) {
                    fos.write(imageData);
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
    private void handleNeeds(String ip, String file) throws IOException, InterruptedException {
        Socket p2pSocket = new Socket(ip.substring(1),12345);
        Thread.sleep(3000);
        sendFile(p2pSocket, file.trim());
    }
    private void sendFile(Socket socket, String filePath) {
        try {
            File file = new File(filePath);
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
