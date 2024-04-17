import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.*;

/**
 * Client 
 * This is the Client thread class, there is a client thread for each peer we are listening to.
 * We are constantly listening and if we get a message we print it. 
 */

 public class ClientThread extends Thread {
    private BufferedReader bufferedReader;
    private int port; // Add port field

    public ClientThread(Socket socket, int port) throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.port = port; // Set the port
    }
public void run() {
        try {
            while (!isInterrupted()) {
                String message = bufferedReader.readLine();
                if (message == null) {
                    break; // Exit loop if end of stream is reached
                }
                JSONObject json = new JSONObject(message);
                System.out.println("[" + json.getString("username") + "]: " + json.getString("message"));
            }
        } catch (IOException e) {
            System.err.println("Error reading from socket: " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        } finally {
            try {
                bufferedReader.close();
				//socket.close();
            } catch (IOException e) {
                System.err.println("Error closing BufferedReader: " + e.getMessage());
            }
        }
    }
}





