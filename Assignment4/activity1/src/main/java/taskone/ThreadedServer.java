package taskone;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class: ThreadedServer
 * Description: Multi-threaded server tasks.
 */
public class ThreadedServer {

    public static void main(String[] args) throws Exception {
        int port;
        String host = "localhost"; 
        StringList strings = new StringList();

        if (args.length != 2) {
            System.out.println("Usage: gradle runTask2 -Pport=9099 -Phost=localhost -q --console=plain");
            port = 8000; // Default port
            host = "localhost"; 
        } else {
            port = Integer.parseInt(args[1]);
            host = args[0];
        }
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server Started...");
            while (true) {
                System.out.println("Accepting a Request...");
                Socket sock = serverSocket.accept();

                handleClient(sock, strings);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket, StringList strings) {
        Runnable clientTask = () -> {
            try {

                Performer performer = new Performer(socket, strings);
                performer.doPerform();
            } finally {
                try {
                    // Close the socket
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        // Start a new thread to handle the client
        Thread clientThread = new Thread(clientTask);
        clientThread.start();
    }

}
