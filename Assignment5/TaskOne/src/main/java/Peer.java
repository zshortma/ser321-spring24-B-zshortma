import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;

/**
 * This is the main class for the peer2peer program.
 * It starts a client with a username and port. Next the peer can decide who to listen to. 
 * So this peer2peer application is basically a subscriber model, we can "blurt" out to anyone who wants to listen and 
 * we can decide who to listen to. We cannot limit in here who can listen to us. So we talk publicly but listen to only the other peers
 * we are interested in. 
 * 
 */

public class Peer {
	private String username;
	private BufferedReader bufferedReader;
	private ServerThread serverThread;
	private static Socket socket10; 
	
	public Peer(BufferedReader bufReader, String username, ServerThread serverThread){
		this.username = username;
		this.bufferedReader = bufReader;
		this.serverThread = serverThread;
	}
	/**
	 * Main method saying hi and also starting the Server thread where other peers can subscribe to listen
	 *
	 * @param args[0] username
	 * @param args[1] port for server
	 */
	public static void main (String[] args) throws Exception {

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		String username = args[0];
		String port = args[1];
		writeDataToFile(username, port);
		System.out.println("Hello " + username + " and welcome! Your port will be " + args[1]);
		ServerThread serverThread = new ServerThread(args[1]);
		serverThread.start();
		Peer peer = new Peer(bufferedReader, args[0], serverThread);
		peer.updateListenToPeers();
		
		
	}
	
	/**
	 * This helps with testing the connected peers and ensuring they are 
	 * getting added to the nodes. 
	 * @param username
	 * @param port
	 */
	private static void writeDataToFile(String username, String port) {
        try {
            FileWriter fileWriter = new FileWriter("peer_tracker.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(username + "," + port);
            bufferedWriter.newLine();

            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	/**
	 * User is asked to define who they want to subscribe/listen to
	 * Per default we listen to no one
	 *
	 */
    public void updateListenToPeers() throws Exception {
        while (true) {

            BufferedReader fileReader = new BufferedReader(new FileReader("peer_tracker.txt"));
            String line;
            String secondToLastLine = null;
            String lastLine = null;

            while ((line = fileReader.readLine()) != null) {
                secondToLastLine = lastLine;
                lastLine = line;
            }
            fileReader.close();

            if (secondToLastLine != null && lastLine != null) {
                String[] parts1 = secondToLastLine.split(",");
                String[] parts2 = lastLine.split(",");

                if (parts1.length == 2 && parts2.length == 2) {
                    String port1 = parts1[1];
                    String port2 = parts2[1];

                    Socket socket1 = null;
                    while (socket1 == null) {
                        try {

                           socket1 = new Socket("localhost", Integer.parseInt(port1)); 
						   new ClientThread(socket1, Integer.parseInt(port2)).start();

                       } catch (IOException e) {
                            System.out.println("Retrying connection to " + port1);
                            Thread.sleep(1000); 
                        }
                    }

                    Socket socket2 = null;
                    while (socket2 == null) {
                        try {
                            socket2 = new Socket("localhost", Integer.parseInt(port2));
							socket10 = socket2;
							System.out.println(socket1 + port1 + port2 + "socket2 while loop");
                            new ClientThread(socket2, Integer.parseInt(port1)).start(); 
                        } catch (IOException e) {
                            System.out.println("Retrying connection to " + port2);
                            Thread.sleep(1000);
                        }
                    }

					askForInput();
					retryConnectionFromFirstPeer(port1, port2);
					retryConnectionFromFirstPeer(port2, port1);
					
                }
            } else {
			
                System.out.println("Not enough peers found. Waiting for another connection.");
            }

            Thread.sleep(2000); 
        }
    }
	
    /**
     * This is to allow the peers to reach back out to newer nodes to get connected to all peers. 
     * @param port1
     * @param port2
     */
	private void retryConnectionFromFirstPeer(String port1, String port2) {
		new Thread(() -> {
			while (true) {
				try {
					Socket socket1 = new Socket("localhost", Integer.parseInt(port2));
					new ClientThread(socket1, Integer.parseInt(port2)).start(); 
					break; 
				} catch (IOException | NumberFormatException e) {
					System.out.println("Retrying connection from the first peer to " + port1);
					try {
						Thread.sleep(2000); 
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	/**
	 * Client waits for user to input their message or quit
	 *
	 * @param bufReader bufferedReader to listen for user entries
	 * @param username name of this peer
	 * @param serverThread server thread that is waiting for peers to sign up
	 */
	public void askForInput() throws Exception {
		try {
			System.out.println("> You can now start chatting (exit to exit)");
			while(true) {
				String message = bufferedReader.readLine();
				if (message.equals("exit")) {
					System.out.println("bye, see you next time");
					break;
				} else {
					serverThread.sendMessage("{'username': '"+ username +"','message':'" + message + "'}");
				}	
			}
			System.exit(0);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
