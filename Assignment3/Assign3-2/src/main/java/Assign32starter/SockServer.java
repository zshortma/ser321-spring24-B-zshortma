package Assign32starter;
import java.net.*;
import java.util.Base64;
import java.util.Set;
import java.util.Stack;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import java.awt.image.BufferedImage;
import java.io.*;
import org.json.*;


/**
 * A class to demonstrate a simple client-server connection using sockets.
 * Ser321 Foundations of Distributed Software Systems
 */
public class SockServer {
	static Stack<String> imageSource = new Stack<String>();

	public static void main (String args[]) {
		Socket sock;
		try {
			
			//opening the socket here, just hard coded since this is just a bas example
			ServerSocket serv = new ServerSocket(8888); 
			System.out.println("Server ready for connetion");

			// placeholder for the person who wants to play a game
			String name = "Unnamed";
			String selection = "";
			String time = "0";
			int points = 0;
			int count = 0;
			boolean requesting = true;

			JSONObject req = new JSONObject();

			// read in one object, the message. we know a string was written only by knowing what the client sent. 
			// must cast the object from Object to desired type to be useful
			while(true) {

				sock = serv.accept(); // blocking wait

				
				ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
				OutputStream out = sock.getOutputStream();

				String s = (String) in.readObject();
				JSONObject json = new JSONObject(s); // the requests that is received

				JSONObject response = new JSONObject();


				/**
				 * Based on type. 
				 */
				if (json.getString("type").equals("start")){
					
					System.out.println("- Got a start");
				
					response.put("type","hello" );
					response.put("value","Hello, please tell me your name." );
					
					
				} else if (json.getString("type").equals("name")) {

                 System.out.println("- Got a name");
                 name = json.getString("value");
                 response.put("type", "message");
                 response.put("value", "Hello, " + name + "! Welcome to the game.");

				 PrintWriter outWrite = new PrintWriter(sock.getOutputStream(), true); 

				 outWrite.println(response.toString());

				 response.put("type", "instructions");
                 response.put("value", "Type in textbox what you would like to do : (see leaderboard) : -l     (play game) : -g    (quit game) : -q");
				 outWrite.println(response.toString());
			
			} else if (json.getString("type").equals("selection")) {

				System.out.println("- Got a chioce");
				selection = json.getString("value");
				response.put("type", "message");
				response.put("value", "You chose " + selection);

				/**
				 * Handling for choice of selection in menu. 
				 */
				if (selection.equals("-l") || selection.equals("-g") || selection.equals("-q")  ) {

			     PrintWriter outWrite = new PrintWriter(sock.getOutputStream(), true); 
			     outWrite.println(response.toString());
				 count++;

			 if (selection.equals("-l")) {

				response.put("type", "leaderboard");
				response.put("value", "You are viewing the leaderboard : ");
				PrintWriter outWrite2 = new PrintWriter(sock.getOutputStream(), true); 
				outWrite2.println(response.toString());

				leaderBoardView();

			 } else if (selection.equals("-q")) {

				response.put("type", "quit");
				response.put("value", "Goodbye. Thanks for playing! Further interaction is disabled, reconnect to restart.");
				PrintWriter outWrite2 = new PrintWriter(sock.getOutputStream(), true); 
				outWrite2.println(response.toString());
				requesting = false;

			 } else if (selection.equals("-g") ) {

				response.put("type", "startgame");
				response.put("value", "Before the game starts, enter the amount of seconds you want for the timer: ");
				PrintWriter outWrite2 = new PrintWriter(sock.getOutputStream(), true); 
				outWrite2.println(response.toString());
			
				time = json.getString("value");
				response.put("type", "timer");
				response.put("value", "You chose " + time);
				outWrite2.println(response.toString());
			
				String imageFilename = "ASU1.png"; 
				JSONObject imageResponse = sendImg(imageFilename);
				outWrite2.println(imageResponse.toString());

			 }
			} else {

				if ( count == 0){

				response.put("type", "instructions");
				response.put("value", "(-l -g -q) are the only options avalible. (case sensitive :)");
				PrintWriter outWrite = new PrintWriter(sock.getOutputStream(), true); 
				outWrite.println(response.toString());

				} else {
					response.put("type", "instructions");
				    response.put("value", "Your time starts now. Make a guess or type : next, left, right.");

					time = json.getString("value");
					PrintWriter outWrite2 = new PrintWriter(sock.getOutputStream(), true); 
				    outWrite2.println(response.toString());
					response.put("type", "timer");
					response.put("value", "You chose " + time);
					outWrite2.println(response.toString());
				
					
					String imageFilename = "img/ASU1.png"; 
					JSONObject imageResponse = sendImg(imageFilename);
					outWrite2.println(imageResponse.toString());
				}

			}

		} 
				else {
					System.out.println("not sure what you meant");
					response.put("type","error" );
					response.put("message","unknown response" );
				}
				PrintWriter outWrite = new PrintWriter(sock.getOutputStream(), true); 
				outWrite.println(response.toString());
			}

		} catch(Exception e) {e.printStackTrace();}
	}

	public static void leaderBoardView(){
		return;
	}

	public static JSONObject sendImg(String filename) throws Exception {
		File file = new File(filename);
		JSONObject obj = new JSONObject();
	
		if (file.exists()) {

			// Read the image file
			BufferedImage image = ImageIO.read(file);
			
			// Convert image to string
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", baos);
			byte[] imageBytes = baos.toByteArray();
			String base64Image = Base64.getEncoder().encodeToString(imageBytes);
			
			// Create the JSON object with image data
			obj.put("type", "image");
			obj.put("value", base64Image);
		} else {
			obj.put("type", "error");
			obj.put("message", "Image file not found");
		}
		return obj;
	}
	
}
