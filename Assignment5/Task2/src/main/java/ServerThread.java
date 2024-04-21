import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;


/**
 * SERVER
 * This is a ServerThread bascially waiting for a client to connect
 * and then opening up another server to "server" the client in parallel
 * it then keeps listening
 */

public class ServerThread extends Thread{
	private ServerSocket serverSocket; // Socket we listen on
	private SocketInfo socket; // socket info of our own socket (host,port)
	private Node node = null; // throwing in the peer so we can call methods on it
	
	public ServerThread(String peer) throws IOException {
	
		String[] hostPort = peer.split(":");
		int port = Integer.valueOf(hostPort[1]);
		String host = hostPort[0];
		System.out.println("     host: " + host);
		socket = new SocketInfo(host, port);

		serverSocket = new ServerSocket(port);

		System.out.println("     Listening on: " + host + ":" + port);
	}
	
	public void setPeer(Node peer){
		this.node = peer;
	}

	public String getHost(){
		return socket.getHost();
	}

	public int getPort(){
		return socket.getPort();
	}
	
	/**
	 * Starting the thread, we are waiting for clients wanting to talk to us, then create a thread for that client to interact
	 */
	public void run() {
		try {
			while (true) {
				Socket sock = serverSocket.accept();
				new ServerTask(sock, node).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
