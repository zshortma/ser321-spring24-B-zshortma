import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.io.PrintWriter;
import org.json.*;

/**
 * This is the main class for the peer2peer program.
 * It starts a client with a username and host:port for the peer and host:port of the initial leader
 * This Peer is basically the client of the application, while the server (the one listening and waiting for requests)
 * is in a separate thread ServerThread
 * In here you should handle the user input and then send it to the server of annother peer or anything that needs to be done on the client side
 * YOU CAN MAKE ANY CHANGES YOU LIKE: this is a very basic implementation you can use to get started
 * 
 */

public class Node {
	private String username;
	private BufferedReader bufferedReader;
	private ServerThread serverThread;

	private Set<SocketInfo> nodes = new HashSet<SocketInfo>();
	private boolean leader = false;
	private SocketInfo leaderSocket;

	
	public Node(BufferedReader bufReader, String username,ServerThread serverThread){
		this.username = username;
		this.bufferedReader = bufReader;
		this.serverThread = serverThread;
	}

	public void setLeader(boolean leader, SocketInfo leaderSocket){
		this.leader = leader;
		this.leaderSocket = leaderSocket;
	}

	public boolean isLeader(){
		return leader;
	}

	public void addNode(SocketInfo si){
		nodes.add(si);
	}
	
	// get a string of all peers that this peer knows
	public String getNode(){
		String s = "";
		for (SocketInfo p: nodes){
			s = s +  p.getHost() + ":" + p.getPort() + " ";
		}
		return s; 
	}
	
    public void handleTask(String sentencePart, char character) {
        // Count the occurrence of the character in the sentence part
        int count = 0;
        for (char c : sentencePart.toCharArray()) {
            if (c == character) {
                count++;
            }
        }
        // Send the count back to the leader
        pushMessage("{'type': 'result', 'count': " + count + "}");
    }
    

	/**
	 * Adds all the peers in the list to the peers list
	 * Only adds it if it is not the currect peer (self)
	 *
	 * @param list String of peers in the format "host1:port1 host2:port2"
	 */
	public void updateListenToPeers(String list) throws Exception {
		String[] peerList = list.split(" ");
		for (String p: peerList){
			String[] hostPort = p.split(":");

			// basic check to not add ourself, since then we would send every message to ourself as well (but maybe you want that, then you can remove this)
			if ((hostPort[0].equals("localhost") || hostPort[0].equals(serverThread.getHost())) && Integer.valueOf(hostPort[1]) == serverThread.getPort()){
				continue;
			}
			SocketInfo s = new SocketInfo(hostPort[0], Integer.valueOf(hostPort[1]));
			nodes.add(s);
		}
	}
	
	/**
	 * Client waits for user to input can either exit or send a message
	 */
	public void askForInput() throws Exception {
		try {
		    
	        // Hardcoded string and character inputs
	        //String inputString = "example string for testing";
	        // char inputChar = 'e';
	        
			
			System.out.println("> You can now start chatting (exit to exit)");
			while(true) {
			    
			    String message = bufferedReader.readLine();
			    
	            // Split the message into string and character parts
	            String[] parts = message.split(" ");
	            if (parts.length != 2) {
	                System.out.println("Invalid input format. Please provide a string followed by a character.");
	                continue; // Skip processing if input format is invalid
	            }
	            
	            String inputString = parts[0];
	            char inputChar = parts[1].charAt(0); // Get the first character of the second part

		       
	            List<String> nodeParts = LeaderLogic.splitString(inputString, inputChar);
	            System.out.println(nodeParts);
	            pushMessage("{'type': 'message', 'username': '"+ username +"', 'part': '" + inputString + "', 'character': '" + inputChar + "'}");
	            
				if (message.equals("exit")) {
					System.out.println("bye, see you next time");
					break;
				} else {
				    int numNodes = nodes.size();
	                int numMessages = message.length();

	             // Distribute messages equally among connected nodes
	                int i = 0; // Counter for iterating through the message characters
	                while (i < numMessages && message.charAt(i) != ' ') {
	                    for (SocketInfo nodeSocket : nodes) {
	                        // Output the determined node and message to the console
	                        System.out.println("Message '" + message.charAt(i) + "' would be sent to node: " + nodeSocket.getHost() + ":" + nodeSocket.getPort());

	                        // Simulate sending the message (print the message that would have been sent)
	                        System.out.println("Simulated message sending: " + message.charAt(i));
	                        pushMessageToNode("{'type': 'message', 'username': '"+ username +"', 'message': '" + message.charAt(i)  + inputChar + "'}", nodeSocket);
	                        // Move to the next character in the message
	                        i++;

	                        // If all characters in the message have been distributed, break out of the loop
	                        if (i >= numMessages || message.charAt(i) == ' ') {
	                            break;
	                        }
	                    }
	                }

				for (String part : nodeParts) {
		          //  pushMessage("{'type': 'message', 'username': '"+ username +"', 'message': '" + part + inputChar + "'}");
		        }
				
				//pushMessage("{'type': 'message', 'username': '"+ username +"','message':'" + nodeParts + "'}");
		             
				}	
			}
			System.exit(0);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
     * Send a message to every peer in the peers list, if a peer cannot be reached remove it from list
     *
     * @param message String that peer wants to send to other peers
     */
    public void pushMessage(String message) {
        try {
            System.out.println("     Trying to send to peers: " + nodes.size());

            // Split the nodes set into two halves
            List<SocketInfo> nodeList = new ArrayList<>(nodes);
            List<SocketInfo> firstHalf = nodeList.subList(0, nodeList.size() / 2);

            Set<SocketInfo> toRemove = new HashSet<>();
            int counter = 0;

            // Send the entire message to the first half of the nodes
            for (SocketInfo s : firstHalf) {
                try {
                    Socket socket = new Socket(s.getHost(), s.getPort());
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(message); // Send the entire message
                    counter++;
                    socket.close();
                } catch (Exception e) {
                    // Handle connection errors
                    System.out.println("Could not connect to " + s.getHost() + ":" + s.getPort());
                    toRemove.add(s);
                }
            }

            // Remove disconnected nodes from the set
            nodes.removeAll(toRemove);

            System.out.println("     Message was sent to " + counter + " peers");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void pushMessageToNode(String message, SocketInfo node) {
        try {
            System.out.println("     Trying to send to peers: " + nodes.size());


            Set<SocketInfo> toRemove = new HashSet<>();
            int counter = 0;

                try {
                    Socket socket = new Socket(node.getHost(), node.getPort());
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(message); // Send the entire message
                    counter++;
                    socket.close();
                } catch (Exception e) {
                    // Handle connection errors
                    System.out.println("Could not connect to " + node.getHost() + ":" + node.getPort());
                    toRemove.add(node);
                }
            

            // Remove disconnected nodes from the set
            nodes.removeAll(toRemove);

            System.out.println("     Message was sent to " + counter + " peers");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// ####### You can consider moving the two methods below into a separate class to handle communication
	// if you like (they would need to be adapted some of course)


	/**
	 * Send a message only to the leader 
	 *
	 * @param message String that peer wants to send to the leader node
	 * this might be an interesting point to check if one cannot connect that a leader election is needed
	 */
	public void commLeader(String message) {
		try {
			BufferedReader reader = null; 
				Socket socket = null;
				try {
					socket = new Socket(leaderSocket.getHost(), leaderSocket.getPort());
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					
				} catch (Exception c) {
					if (socket != null) {
						socket.close();
					} else {
						System.out.println("Could not connect to " + leaderSocket.getHost() + ":" + leaderSocket.getPort());
					}
					return; // returning since we cannot connect or something goes wrong the rest will not work. 
				}

				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				out.println(message);

				JSONObject json = new JSONObject(reader.readLine());
				System.out.println("     Received from server " + json);
				String list = json.getString("list");
				updateListenToPeers(list); // when we get a list of all other peers that the leader knows we update them

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	
    private Set<SocketInfo> nodeSet; 
    private Map<Integer, SocketInfo> nodeMap; 
    





	/**
	 * Main method saying hi and also starting the Server thread where other peers can subscribe to listen
	 *
	 * @param args[0] username
	 * @param args[1] port for server
	 */
	public static void main (String[] args) throws Exception {

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		String username = args[0];
		System.out.println("Hello " + username + " and welcome! Your port will be " + args[1]);

		int size = args.length;
		System.out.println(size);
		if (size == 4) {
			System.out.println("Started");
        } else {
            System.out.println("Expected: <name(String)> <peer(String)> <leader(String)> <isLeader(bool-String)>");
            System.exit(0);
        }

        System.out.println(args[0] + " " + args[1]);
        ServerThread serverThread = new ServerThread(args[1]);
        Node node = new Node(bufferedReader, username, serverThread);

        String[] hostPort = args[2].split(":");
        SocketInfo s = new SocketInfo(hostPort[0], Integer.valueOf(hostPort[1]));
        System.out.println(args[3]);
        if (args[3].equals("true")){
			System.out.println("Leader");
			node.setLeader(true, s);
		} else {
			System.out.println("Node");

			// add leader to list 
			node.addNode(s);
			node.setLeader(false, s);

			// send message to leader that we want to join
			node.commLeader("{'type': 'join', 'username': '"+ username +"','ip':'" + serverThread.getHost() + "','port':'" + serverThread.getPort() + "'}");

		}
		serverThread.setPeer(node);
		serverThread.start();
		node.askForInput();

	}
	
}
