import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.PrintWriter;

import org.json.*;

/**
 * This is the class that handles communication with a peer/client that has connected to use
 * and wants something from us
 * 
 */

public class ServerTask extends Thread {
	private BufferedReader bufferedReader;
	private Node node = null; // so we have access to the peer that belongs to that thread
	private PrintWriter out = null;
	private Socket socket = null;
    public  int counter = 0;
    public static int updated;
	// Init with socket that is opened and the peer
	public ServerTask(Socket socket, Node node) throws IOException {
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		this.node = node;
		this.socket = socket;
	}
	
	
	// basically wait for an input, right now we can only handle a join request
	// and a message
	// More requests will be needed to make everything work
	// You can enhance this or totally change it, up to you. 
	// I used simple JSON here, you can use your own protocol, use protobuf, anything you want
	// in here this is not done especially pretty, I just use a PrintWriter and BufferedReader for simplicity
	public void run() {
	
		while (true) {
			try {
			    JSONObject json = new JSONObject(bufferedReader.readLine());

			    if (json.getString("type").equals("join")){
			    	System.out.println("     " + json); // just to show the json

			    	System.out.println("     " + json.getString("username") + " wants to join the network");
			    	node.updateListenToPeers(json.getString("ip") + ":" + json.getInt("port"));
			    	out.println(("{'type': 'join', 'list': '"+ node.getNode() +"'}"));

			    	if (node.isLeader()){
			    		node.pushMessage(json.toString());
			    	}

			    	
			    } else if (json.getString("type").equals("task")) {

                    String sentencePart = json.getString("sentencePart");
                    char character = json.getString("character").charAt(0);
                    int result = node.processMessage(sentencePart);
                    System.out.println(result);
                    node.handleTask(sentencePart, character);
			    
			    } else {
			        
			    	System.out.println("[" + json.getString("username")+"]: " + json.getString("message"));
			    	 int result = node.processMessage(json.getString("message"));
			         if (result == 1) {
		                    counter = 1;
		                }
			         
		                System.out.println("Result: " + result);
		                break;
			    }
			    
		        
		        try {
		            
		        FileWriter fileWriter = new FileWriter("countTracker.txt", true);
		        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

		        bufferedWriter.write("count:" + counter);
		        bufferedWriter.newLine();

		        bufferedWriter.close();

		         } catch (IOException e) {
		             
		                e.printStackTrace();
		        }
			    
			} catch (Exception e) {
				interrupt();
				break;
			}
		}
		
 
	}

}
