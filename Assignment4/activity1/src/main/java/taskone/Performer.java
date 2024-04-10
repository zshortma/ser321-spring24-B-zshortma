/**
  File: Performer.java
  Author: Student in Fall 2020B
  Description: Performer class in package taskone.
*/

package taskone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;


/**
 * Class: Performer 
 * Description: Performer for server tasks.
 */
class Performer {

    private StringList state;
    private Socket conn;

    public Performer(Socket sock, StringList strings) {
        this.conn = sock;
        this.state = strings;
    }

    public JSONObject add(JSONObject req) {
        System.out.println("In add");
        JSONObject resp = new JSONObject();
        resp.put("type", 1);
        boolean missing = false;
        if (!req.has("data")){
            missing = true;
        }
        if(!req.getJSONObject("data").has("string")) {
            missing = true;
        }
        if(missing) {
            resp.put("ok", false);
            resp.put("type", 1);
            JSONObject error = new JSONObject();
            error.put("error", "required data missing");
            error.put("details", "field data or string missing");
            resp.put("data", error);
            return resp;
        }

        String str = req.getJSONObject("data").getString("string");
        state.add(str);
        resp.put("ok", true);
        resp.put("type", 1);
        resp.put("data", state.toString());
        return resp;
    }


    public JSONObject display(JSONObject req) {

        JSONObject resp = new JSONObject();
        resp.put("type", 2);

        // Get the list of strings
        List<String> stringList = state.strings;
        
        // Convert to a JSON array
        JSONArray dataArray = new JSONArray(stringList);

        // Convert the JSON array  back to a string. 
        String dataString = dataArray.toString();

        resp.put("ok", true);
        resp.put("data", dataString);
    
        return resp;
    }



    public JSONObject sort(JSONObject req) {

        JSONObject resp = new JSONObject();
        resp.put("type", 3);

        if (!containsOnlyIntegers(state.strings)) {
            // Return error response if list contains non-integer elements
            resp.put("ok", false);
            resp.put("data", "List contains non-integer elements");
            return resp;
        }
    
       // Get the list of integers from string
       List<Integer> intList = new ArrayList<>();

       for (String str : state.strings) {
          intList.add(Integer.parseInt(str));
       }
    
      // Sort the list of integers
      Collections.sort(intList);

      JSONArray dataArray = new JSONArray(intList);

      // Convert the JSON array to a string.
      String dataString = dataArray.toString();

     resp.put("ok", true);
     resp.put("data", dataString);
    
     return resp;
    }

    private boolean containsOnlyIntegers(List<String> list) {
        for (String str : list) {
            try {
                Integer.parseInt(str);
            } catch (NumberFormatException e) {
                return false; // Non-integer element found
            }
        }
        return true; // All elements are integers
    }

    public JSONObject switching(JSONObject req) {

        JSONObject resp = new JSONObject();
        resp.put("type", 4);
    
        // Check if the request contains the "data" field
        if (!req.has("data")) {
            resp.put("ok", false);
            JSONObject error = new JSONObject();
            error.put("error", "required data missing");
            error.put("details", "field 'data' missing");
            resp.put("data", error);
            return resp;
        }
    
        try{
        // Get the indices to switch from the request data
        JSONObject requestData = req.getJSONObject("data");
        int index1 = requestData.getInt("index1");
        int index2 = requestData.getInt("index2");

        // Check if indices are valid (greater than 0)
        if (index1 <= 0 || index2 <= 0) {
            System.out.println("Invalid indices. Please enter positive integers.");
            return null;
        }

         // Check if both indices are the same
        if (index1 == index2) {
            System.out.println("Indices cannot be the same. Please provide different indices.");
            return null;
         }
    
        // Check if the indices are valid
        if (index1 < 0 || index1 >= state.size()+1 || index2 < 0 || index2 >= state.size()+1) {
            resp.put("ok", false);
            JSONObject error = new JSONObject();
            error.put("error", "invalid indices");
            error.put("details", "indices should be within the range of the list");
            resp.put("data", error);
            return resp;
        }
    
    
        // Switch the strings at the specified indices in the list
        Collections.swap(state.strings, index1-1, index2-1);
    
        // Convert the updated list to a string representation
        String dataString = state.toString();
    
        // Put the string representation of the updated list into the response data
        resp.put("ok", true);
        resp.put("data", dataString);
    
        return resp;
    } catch (JSONException e) {
        // Handle JSONException
        resp.put("ok", false);
        resp.put("data", "Error: Invalid JSON data in request");
        return resp;
    }
    }
    

    public static JSONObject unknown(int type) {
        JSONObject json = new JSONObject();
        json.put("type", type); // echo initial type
        json.put("ok", false);
        JSONObject data = new JSONObject();
        data.put("error", "unknown request");
        json.put("data", data);
        return json;
    }

    public static JSONObject quit() {
        JSONObject json = new JSONObject();
        json.put("type", 0); // echo initial type
        json.put("ok", true);
        json.put("data", "Bye");
        return json;
    }

    public void doPerform() {
        boolean quit = false;
        OutputStream out = null;
        InputStream in = null;
        try {
            out = conn.getOutputStream();
            in = conn.getInputStream();
            System.out.println("Server connected to client:");
            while (!quit) {
                byte[] messageBytes = NetworkUtils.receive(in);
                JSONObject message = JsonUtils.fromByteArray(messageBytes);

                JSONObject returnMessage = new JSONObject();
   
                int choice = message.getInt("selected");
                    switch (choice) {
                        case (4):
                        returnMessage = switching(message);
                        break;
                        case (3):
                        returnMessage = sort(message);
                        break;
                        case (2):
                        returnMessage = display(message);
                        break;
                        case (1):
                            returnMessage = add(message);
                            break;
                        case (0):
                            returnMessage = quit();
                            quit = true;
                            break;
                        default:
                            returnMessage = unknown(choice);
                            break;
                    }
                    System.out.println(returnMessage);
                // we are converting the JSON object we have to a byte[]
                byte[] output = JsonUtils.toByteArray(returnMessage);
                NetworkUtils.send(out, output);
            }
            // close the resource
            System.out.println("close the resources of client ");
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
