package Assign32starter;

import java.awt.Dimension;

import org.json.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

/**
 * The ClientGui class is a GUI frontend that displays an image grid, an input text box,
 * a button, and a text area for status. 
 * 
 * Methods of Interest
 * ----------------------
 * show(boolean modal) - Shows the GUI frame with current state
 *     -> modal means that it opens GUI and suspends background processes. 
 * 		  Processing still happens in the GUI. If it is desired to continue processing in the 
 *        background, set modal to false.
 * newGame(int dimension) - Start a new game with a grid of dimension x dimension size
 * insertImage(String filename, int row, int col) - Inserts an image into the grid
 * appendOutput(String message) - Appends text to the output panel
 * submitClicked() - Button handler for the submit button in the output panel
 * 
 * Notes
 * -----------
 * > Does not show when created. show() must be called to show he GUI.
 * 
 */
public class ClientGui implements Assign32starter.OutputPanel.EventHandlers {
	JDialog frame;
	PicturePanel picPanel;
	OutputPanel outputPanel;
	String currentMess;

	Socket sock;
	OutputStream out;
	ObjectOutputStream os;
	BufferedReader bufferedReader;

	int loops = 0;
	String host = "localhost";
	int port = 8080;
	private int counter = 0;

	/**
	 * Construct dialog
	 * @throws IOException 
	 */
	public ClientGui(String host, int port) throws IOException {
		this.host = host; 
		this.port = port; 
	
		frame = new JDialog();
		frame.setLayout(new GridBagLayout());
		frame.setMinimumSize(new Dimension(500, 500));
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		// setup the top picture frame
		picPanel = new PicturePanel();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.25;
		frame.add(picPanel, c);

		// setup the input, button, and output area
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 0.75;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		outputPanel = new OutputPanel();
		outputPanel.addEventHandlers(this);
		frame.add(outputPanel, c);

		picPanel.newGame(1);
		insertImage("img/Berlin1.png", 0, 0);

		open(); // opening server connection here
		currentMess = "{'type': 'start'}"; // very initial start message for the connection

		try {
			os.writeObject(currentMess);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String string = this.bufferedReader.readLine();
		System.out.println("Got a connection to server");
		JSONObject json = new JSONObject(string);
		outputPanel.appendOutput(json.getString("value")); // putting the message in the outputpanel

		close(); 
	}

	/**
	 * Shows the current state in the GUI
	 * @param makeModal - true to make a modal window, false disables modal behavior
	 */
	public void show(boolean makeModal) {
		frame.pack();
		frame.setModal(makeModal);
		frame.setVisible(true);
	}

	/**
	 * Creates a new game and set the size of the grid 
	 * @param dimension - the size of the grid will be dimension x dimension
	 * No changes should be needed here
	 */
	public void newGame(int dimension) {
		picPanel.newGame(1);
	}

	/**
	 * Insert an image into the grid at position (col, row)
	 * 
	 * @param filename - filename relative to the root directory
	 * @param row - the row to insert into
	 * @param col - the column to insert into
	 * @return true if successful, false if an invalid coordinate was provided
	 * @throws IOException An error occured with your image file
	 */
	public boolean insertImage(String filename, int row, int col) throws IOException {

		System.out.println("Image insert");
		String error = "";

		try {
			// insert the image
			if (picPanel.insertImage(filename, row, col)) {

				return true;

			}

			error = "File(\"" + filename + "\") not found.";

		} catch(PicturePanel.InvalidCoordinateException e) {
			// put error in output
			error = e.toString();
		}

		outputPanel.appendOutput(error);
		return false;
	}


	
	/**
	 * Submit button handling
	 * 
	 * TODO: This is where your logic will go or where you will call appropriate methods you write. 
	 * Right now this method opens and closes the connection after every interaction, if you want to keep that or not is up to you. 
	 */
	@Override
	public void submitClicked() {
		try {

		open();

		System.out.println("submit clicked ");
	
		String input = outputPanel.getInputText();
      
		JSONObject request = new JSONObject();

		/* Allows users to select choice from menu. */
		if (counter == 0){
        request.put("type", "name"); // Set the type of request
        request.put("value", input); // Set the value to the input text
		counter++;
		} else if (counter >= 1){
			request.put("type", "selection"); // Set the type of request
			request.put("value", input); // Set the value to the input text
		}

        // Send the JSON request to the server
        os.writeObject(request.toString());

        // Wait for a response from the server
        String response = bufferedReader.readLine();
        System.out.println("Response from server: " + response);

		JSONObject jsonResponseVal = new JSONObject(response);
		outputPanel.appendOutput(jsonResponseVal.getString("value"));

		String responseJSON = bufferedReader.readLine();


		/* Handle responses from server */
		if (responseJSON != null) {

		JSONObject jsonResponse = new JSONObject(responseJSON);
		

		/**
		 * Handle based on type!
		 */
        if (jsonResponse.getString("type").equals("message")) {

		} else if (jsonResponse.getString("type").equals("quit")) {

			outputPanel.appendOutput(jsonResponse.getString("value"));
			outputPanel.disableSubmitButton();

		} else if (jsonResponse.getString("type").equals("timer")) {

			loops++;
			int imageCounter = 0;
			Boolean Incorrect = false;
			if (loops == 1){
			insertImage("img/ASU1.png", 0, 0);
			
			}
			imageCounter++;
			System.out.println(loops-1);
			if (loops == 2){
				insertImage("img/Berlin2.png", 0, 0);
				outputPanel.setPoints(loops-1);
			}

			if (loops == 3){
				insertImage("img/Paris3.png", 0, 0);
				outputPanel.setPoints(loops-1);
			}

			if( loops == 4){
				if(Incorrect){
					insertImage("img/lose.jpg", 0, 0);
				} else {
					insertImage("img/win.jpg", 0, 0);
					outputPanel.setPoints(loops-1);
					outputPanel.appendOutput("Always a winner 3 out of 3");
					outputPanel.disableSubmitButton();
				}
			}
		} else {
            outputPanel.appendOutput(jsonResponse.getString("value"));
        }
	}

	   outputPanel.setInputText(""); // Clears input textbox
		close(); // Close connection
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/*
	 * Allows access to disable subit button. 
	 */
	@Override
	public void disableSubmitButton() {
		outputPanel.disableSubmitButton(); 

	}


	/**
	 * Key listener for the input text box
	 * 
	 * Change the behavior to whatever you need
	 */
	@Override
	public void inputUpdated(String input) {
		if (input.equals("surprise")) {
			outputPanel.appendOutput("You found me!");
		}
	}

	public void open() throws UnknownHostException, IOException {
		this.sock = new Socket(host, port); // connect to host and socket

		// get output channel
		this.out = sock.getOutputStream();
		// create an object output writer (Java only)
		this.os = new ObjectOutputStream(out);
		this.bufferedReader = new BufferedReader(new InputStreamReader(sock.getInputStream()));

	}
	

	public void close() {
        try {
            if (out != null)  out.close();
            if (bufferedReader != null)   bufferedReader.close(); 
            if (sock != null) sock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public static void main(String[] args) throws IOException {
		// create the frame
		try {
			String host = "localhost";
			int port = 8888;
			ClientGui main = new ClientGui(host, port);
			main.newGame(1);
			main.insertImage("img/hi.png", 0, 0);
			main.show(true);
		} catch (Exception e) {e.printStackTrace();}
	}
}
