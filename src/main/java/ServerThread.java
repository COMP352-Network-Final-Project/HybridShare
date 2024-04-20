import java.io.*;
import java.net.*;
]



public class ServerThread implements Runnable {
    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);


            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);

                out.println(message);

                String [] parsed = message.split(" ");
                if (parsed.length == 4) {
                    String ip = parsed[1];
                    String action = parsed[2];
                    String file = parsed[3];

                    if ("NEEDS".equals(action)) {
                        handleNeeds(ip, file);
                    } else if ("HAS".equals(action)) {
                        handleHas(ip, file);
                    } else {

                    }
                    break;
                } else {

                }
            }

            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void handleNeeds(String ip, String file) throws IOException{
        Socket p2pSocket = new Socket(ip.substring(1),12346);
        sendFile(p2pSocket, file);
    }

    private void handleHas(String ip, String file) throws IOException {
        Socket p2pSocket = new Socket(ip.substring(1),12345);
        receiveFiles(p2pSocket, file);
    }
    private void receiveFiles(Socket socket, String fileName) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName.substring(0, fileName.length() - 4) + "DOWNLOAD.txt");
            InputStream inputStream = socket.getInputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            fileOutputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(Socket socket, String filePath) {
        try {
            File file = new File(filePath);
            byte[] buffer = new byte[(int) file.length()];
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            bufferedInputStream.read(buffer, 0, buffer.length);

            OutputStream outputStream = socket.getOutputStream();
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
