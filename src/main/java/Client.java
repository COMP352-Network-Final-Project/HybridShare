import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;
public class Client implements Runnable {
    private int port;
    private String serverAddress;


    public String file;

    /**
     *
     * @param serverAddress the IP address of the SuperPeer to connect to
     * @param port the Port number of the server to connect to
     * @param file the file that this client wants to receive
     */
    public Client(String serverAddress, int port, String file) {
        this.port = port;
        this.serverAddress = serverAddress;
        this.file= file;

    }

    /**
     * main running body of the Client class where the wanted and available files are sent to the SuperPeer
     * this is done automatically every 8 seconds
     */
    public void run() {
        try {
            Socket socket = new Socket(serverAddress, port);

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();


            Runnable sendMessageTask = () -> {
                try {
                    String currentDirectory = System.getProperty("user.dir");
                    currentDirectory = currentDirectory +"/share";
                    File directory = new File(currentDirectory);

                    // Get an array of all files in the directory
                    String[] files = directory.list();

                    // Print the filenames
                    if (files != null) {
                        for (String filename : files) {
                            String messageH = "HAS " + socket.getInetAddress().toString() +  " " + filename;
                            out.println(messageH);
                        }
                    }
                    String messageW = "WANTS " + socket.getInetAddress().toString() +  " " + file;

                    out.println(messageW);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            executor.scheduleAtFixedRate(sendMessageTask, 0, 8, TimeUnit.SECONDS);

            String message;
            while ((message = userInput.readLine()) != null) {
                out.println(message);
            }
            userInput.close();
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}