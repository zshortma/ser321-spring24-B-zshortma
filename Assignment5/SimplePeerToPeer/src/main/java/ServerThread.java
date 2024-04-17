import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * SERVER
 * This is the ServerThread class that has a socket where we accept clients contacting us.
 * We save the clients ports connecting to the server into a List in this class. 
 * When we wand to send a message we send it to all the listening ports
 */

public class ServerThread extends Thread{
	private ServerSocket serverSocket;
	private static ServerThread instance; 
	private static Set<Socket> listeningSockets = new HashSet<Socket>();
	
	public ServerThread(String portNum) throws IOException {
		serverSocket = new ServerSocket(Integer.valueOf(portNum));
	}
	
	public static synchronized ServerThread getInstance(String portNum) throws IOException {
        if (instance == null) {
            instance = new ServerThread(portNum);
            instance.start();
        }
        return instance;
    }

	/**
	 * Starting the thread, we are waiting for clients wanting to talk to us, then save the socket in a list
	 */
	public void run() {
		try {
			while (true) {
				FileWriter fileWriter = new FileWriter("connected_sockets.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				Socket sock = serverSocket.accept();
				listeningSockets.add(sock);
				System.out.println("New client connected: " + sock.getInetAddress() + ":" + sock.getPort());
				bufferedWriter.write(sock.getInetAddress() + ":" + sock.getPort());
                bufferedWriter.newLine();
				bufferedWriter.close();
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void sendMessage(String message) {
		try {
			for (Socket s : listeningSockets) {
				PrintWriter out = new PrintWriter(s.getOutputStream(), true);
				out.println(message);
		     }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void sendToServer(Socket clientSocket, String message) {
		try {
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			out.println(message);
			System.out.println("Message received from " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + ": " + message);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


}
