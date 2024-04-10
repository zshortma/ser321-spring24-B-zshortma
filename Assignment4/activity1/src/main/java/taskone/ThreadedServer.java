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
         int port = 8000;
         String host = "localhost";
        StringList strings = new StringList();

        if (args.length != 2) {
            System.out.println("Usage: gradle runServer -Phost=localhost -Pport=9099 -q --console=plain");
            System.exit(1);
             port = 8000;
             host = "localhost";
        } else {
            host = args[0];
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException nfe) {
                System.out.println("[Port] must be an integer");
                System.exit(2);
            }
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
        Thread clientThread = new Thread(() -> {
            try {

            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);
            String clientRequest = new String(buffer, 0, bytesRead);
            OutputStream outputStream = socket.getOutputStream();
            String response = "Response to client request";
            outputStream.write(response.getBytes());

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        clientThread.start();
    }
}
