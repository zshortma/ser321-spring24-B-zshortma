/**
  File: Client.java
  Author: Student in Fall 2020B
  Description: Client class in package taskone.
*/

package taskone;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import org.json.JSONObject;

/**
 * Class: Client
 * Description: Client tasks.
 * Basic methods are given but you can change them and make adjustements as you see fit.
 */
public class Client {
    private static BufferedReader stdin;

    // The functions to build the requests do not have to include more error handling, you can assume
    // we will input the correct data
    /**
     * Function JSONObject for add() request.
     */
    public static JSONObject add() {
        String strToSend = null;
        JSONObject request = new JSONObject();
        request.put("selected", 1);
        try {
            System.out.print("Please input the string: ");
            strToSend = stdin.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject data = new JSONObject();
        data.put("string", strToSend);
        request.put("data", data);
        return request;
    }


    /**
     * Function JSONObject for display() request.
     */
    public static JSONObject display() {
        JSONObject request = new JSONObject();
        request.put("selected", 2);
        return request;
    }

    /**
     * Function JSONObject for sort request.
     */
    public static JSONObject sort() {

        JSONObject request = new JSONObject();
        request.put("selected", 3);
        return request;
    }

    /**
     * Function JSONObject for switch request
     */
    public static JSONObject switching() {
        JSONObject request = new JSONObject();
        request.put("selected", 4);
        int numInput1 = 0;
        int numInput2 = 0;
        System.out.println("Indexing is not 0 based, please enter above 0! ");
        try {
            System.out.print("Please input the first index: ");
            numInput1 = Integer.parseInt(stdin.readLine());
            if (numInput1 <= 0) {
                System.out.println("Index must be above 0!");
                return null; // Return null to indicate an invalid input
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Invalid input!");
            e.printStackTrace();
            return null; 
        }


        try {
            System.out.print("Please input the second index: ");
            numInput2 = Integer.parseInt(stdin.readLine());
            if (numInput2 <= 0) {
                System.out.println("Index must be above 0!");
                return null; // Return null to indicate an invalid input
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Invalid input!");
            e.printStackTrace();
            return null; // Return null in case of invalid input
        }

        JSONObject data = new JSONObject();
        data.put("index1", numInput1);
        data.put("index2", numInput2);
        request.put("data", data);
        return request;
    }

    /**
     * Function JSONObject quit().
     */
    public static JSONObject quit() {
        JSONObject request = new JSONObject();
        request.put("selected", 0);
        return request;
    }

    /**
     * Function main().
     */
    public static void main(String[] args) throws IOException {
        String host;
        int port;
        Socket sock;
        stdin = new BufferedReader(new InputStreamReader(System.in));
        try {
            if (args.length != 2) {
                // gradle runClient -Phost=localhost -Pport=9099 -q --console=plain
                System.out.println("Usage: gradle runClient -Phost=localhost -Pport=9099");
                System.exit(0);
            }

            host = args[0];
            port = -1;
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException nfe) {
                System.out.println("[Port] must be an integer");
                System.exit(2);
            }

            sock = new Socket(host, port);
            OutputStream out = sock.getOutputStream();
            InputStream in = sock.getInputStream();
            Scanner input = new Scanner(System.in);
            int choice;
            do {
                System.out.println();
                // TODO: you will need to change the menu based on the tasks for this assignment, see Readme!
                System.out.println("Client Menu");
                System.out.println("Please select a valid option (1-4). 0 to disconnect the client");
                System.out.println("1. add");
                System.out.println("2. display");
                System.out.println("3. sort");
                System.out.println("4. switch");
                System.out.println("0. quit");
                System.out.println();
                choice = input.nextInt(); // do not have to error handle in case no int is given, we will input the correct thing
                JSONObject request = null;
                switch (choice) {
                    case (1):
                        request = add();
                        break;
                    case (2):
                        request = display();
                        break;
                    case (3):
                        request = sort();
                        break;
                    case (4):
                        request = switching();
                        break;
                    case (0):
                        request = quit();
                        break;
                    default:
                        System.out.println("Please select a valid option (0-6).");
                        break;
                }

                if (request != null) {
                    System.out.println(request);
                    NetworkUtils.send(out, JsonUtils.toByteArray(request));
                    byte[] responseBytes = NetworkUtils.receive(in);
                    JSONObject response = JsonUtils.fromByteArray(responseBytes);

                    if (!response.getBoolean("ok")) { 
                       // System.out.println(response.getString("data"));

                       if (response.has("data")) {
                        // Check if "data" field is a JSONObject
                        if (response.get("data") instanceof JSONObject) {
                            JSONObject errorData = response.getJSONObject("data");
                            // Print the error message and details if available
                            if (errorData.has("error")) {
                                System.out.println("Error: " + errorData.getString("error"));
                            }
                            if (errorData.has("details")) {
                                System.out.println("Details: " + errorData.getString("details"));
                            }
                        } else {
                            // Handle other cases if needed
                            System.out.println("Unexpected data format in response");
                        }
                    }
                    } else {
                        System.out.println();
                        System.out.println("The response from the server: ");
                        System.out.println("type: " + response.getInt("type"));


                        System.out.println("data: " + response.getString("data"));
                        int type = response.getInt("type");
                        if (type == 0) {
                            sock.close();
                            out.close();
                            in.close();
                            System.exit(0);
                        }
                    }
                }
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}