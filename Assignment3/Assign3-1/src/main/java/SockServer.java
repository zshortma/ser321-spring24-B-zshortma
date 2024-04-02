import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

/**
 * A class to demonstrate a simple client-server connection using sockets.
 *
 */
public class SockServer {
  static Socket sock;
  static DataOutputStream os;
  static ObjectInputStream in;

  static int port = 8888;
  private static final Map<String, Integer> inventory = new HashMap<>();

  public static void main (String args[]) {

    if (args.length != 1) {
      System.out.println("Expected arguments: <port(int)>");
      System.exit(1);
    }

    try {
      port = Integer.parseInt(args[0]);
    } catch (NumberFormatException nfe) {
      System.out.println("[Port|sleepDelay] must be an integer");
      System.exit(2);
    }

    try {
      //open socket
      ServerSocket serv = new ServerSocket(port);
      System.out.println("Server ready for connections");

      /**
       * Simple loop accepting one client and calling handling one request.
       *
       */


      while (true){
        System.out.println("Server waiting for a connection");
        sock = serv.accept(); // blocking wait
        System.out.println("Client connected");

        // setup the object reading channel
        in = new ObjectInputStream(sock.getInputStream());

        // get output channel
        OutputStream out = sock.getOutputStream();

        // create an object output writer (Java only)
        os = new DataOutputStream(out);

        boolean connected = true;
        while (connected) {
          String s = "";
          try {
            s = (String) in.readObject(); // attempt to read string in from client
          } catch (Exception e) { // catch rough disconnect
            System.out.println("Client disconnect");
            connected = false;
            continue;
          }

          JSONObject res = isValid(s);

          if (res.has("ok")) {
            writeOut(res);
            continue;
          }

          JSONObject req = new JSONObject(s);

          res = testField(req, "type");
          if (!res.getBoolean("ok")) { // no "type" header provided
            res = noType(req);
            writeOut(res);
            continue;
          }
          // check which request it is (could also be a switch statement)
          if (req.getString("type").equals("echo")) {
            res = echo(req);
          } else if (req.getString("type").equals("add")) {
            res = add(req);
          } else if (req.getString("type").equals("addmany")) {
            res = addmany(req);
          } else if (req.getString("type").equals("roller")) {
        	res = roller(req);
          } else if (req.getString("type").equals("inventory")) {
        	res = inventory(req);
          } else {
            res = wrongType(req);
          }
          writeOut(res);
        }
        // if we are here - client has disconnected so close connection to socket
        overandout();
      }
    } catch(Exception e) {
      e.printStackTrace();
      overandout(); // close connection to socket upon error
    }
  }


  /**
   * Checks if a specific field exists
   *
   */
  static JSONObject testField(JSONObject req, String key){
    JSONObject res = new JSONObject();

    // field does not exist
    if (!req.has(key)){
      res.put("ok", false);
      res.put("message", "Field " + key + " does not exist in request");
      return res;
    }
    return res.put("ok", true);
  }

  // handles the simple echo request
  static JSONObject echo(JSONObject req){
    System.out.println("Echo request: " + req.toString());
    JSONObject res = testField(req, "data");
    if (res.getBoolean("ok")) {
      if (!req.get("data").getClass().getName().equals("java.lang.String")){
        res.put("ok", false);
        res.put("message", "Field data needs to be of type: String");
        return res;
      }

      res.put("type", "echo");
      res.put("echo", "Here is your echo: " + req.getString("data"));
    }
    return res;
  }

  // handles the simple add request with two numbers
  static JSONObject add(JSONObject req){
    System.out.println("Add request: " + req.toString());
    JSONObject res1 = testField(req, "num1");
    if (!res1.getBoolean("ok")) {
      return res1;
    }

    JSONObject res2 = testField(req, "num2");
    if (!res2.getBoolean("ok")) {
      return res2;
    }

    JSONObject res = new JSONObject();
    res.put("ok", true);
    res.put("type", "add");
    try {
      res.put("result", req.getInt("num1") + req.getInt("num2"));
    } catch (org.json.JSONException e){
      res.put("ok", false);
      res.put("message", "Field num1/num2 needs to be of type: int");
    }
    return res;
  }

// implement me in assignment 3
//handles the inventory request
static JSONObject inventory(JSONObject req) {
	
	
	 // Craft request
	 System.out.println("Inventory request: " + req.toString());
	  JSONObject res = new JSONObject(); 
      res.put("type", "inventory"); 


      // Retrieve the task type from the request
      String task = req.getString("task");
      res.put("task", task); 

     
      switch (task) {
          case "add":
              // Get the product name and quantity from the request
              String productName = req.getString("productName");
              int quantity = req.getInt("quantity");

              // Add the product to inventory
              inventory.put(productName, quantity);

              // Sucess
              res.put("ok", true);
              res.put("message", "Product added to inventory successfully.");
              break;

          case "view":
        	  
              // craft view logic based on items in the system.
              JSONArray inventoryList = new JSONArray();
              for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                  JSONObject item = new JSONObject();
                  item.put("product", entry.getKey());
                  item.put("quantity", entry.getValue());
                  inventoryList.put(item);
              }
              //sucess
              res.put("ok", true);
              res.put("result", inventoryList);
              break;

          case "buy":
              // Get the product name and quantity 
              productName = req.getString("productName");
              quantity = req.getInt("quantity");

              // Is there any items?
              if (!inventory.containsKey(productName)) {
                  res.put("ok", false);
                  res.put("message", "Product " + productName + " not in inventory");
                  break;
              }

         
              int availableQuantity = inventory.get(productName);
              if (quantity > availableQuantity) {
                  res.put("ok", false);
                  res.put("message", "Product " + productName + " not available in quantity " + quantity);
                  break;
              }

            
              inventory.put(productName, availableQuantity - quantity);

              // success response
              res.put("ok", true);
              res.put("message", "Product(s) bought successfully.");
              break;

          default:
             
              res.put("ok", false);
              res.put("message", "Invalid task provided.");
              break;
      }

   // Return the response
      return res; 

}


// implement me in assignment 3
static JSONObject roller(JSONObject req) {

// Craft message request
 System.out.println("Roller request: " + req.toString());
 JSONObject res = new JSONObject();
 res.put("type", "roller");
 res.put("ok", false); 

 try {
 
     int dieCount = req.getInt("dieCount");
     int faces = req.getInt("faces");

     // error handling if items are entered negativly   
     if (dieCount <= 0 || faces <= 0) {
         res.put("ok", false);
         res.put("message", "Die count and faces must be positive integers.");
         return res;
     }

     // Roll logic
     Map<Integer, Integer> result = new HashMap<>();
     for (int i = 0; i < dieCount; i++) {
         int roll = (int) (Math.random() * faces) + 1;
         result.put(roll, result.getOrDefault(roll, 0) + 1);
     }

     //Create JSON
     JSONObject resultJson = new JSONObject();
     for (Map.Entry<Integer, Integer> entry : result.entrySet()) {
         resultJson.put(entry.getKey().toString(), entry.getValue());
     }

     // Sucess!
     res.put("ok", true);
     res.put("result", resultJson);
     
 } catch (NumberFormatException e) {
     res.put("ok", false);
     res.put("message", "Invalid input format: must be integers.");
     
 } catch (JSONException e) {
     res.put("ok", false);
     res.put("message", "Invalid request format: " + e.getMessage());
 }

 // Return reposnse
 return res;
}


  // handles the simple addmany request
  static JSONObject addmany(JSONObject req){
	  
    System.out.println("Add many request: " + req.toString());
    JSONObject res = testField(req, "nums");
    if (!res.getBoolean("ok")) {
      return res;
    }

    int result = 0;
    JSONArray array = req.getJSONArray("nums");
    for (int i = 0; i < array.length(); i ++){
      try{
        result += array.getInt(i);
      } catch (org.json.JSONException e){
        res.put("ok", false);
        res.put("message", "Values in array need to be ints");
        return res;
      }
    }

    res.put("ok", true);
    res.put("type", "addmany");
    res.put("result", result);
    return res;
  }

  // creates the error message for wrong type
  static JSONObject wrongType(JSONObject req){
    System.out.println("Wrong type request: " + req.toString());
    JSONObject res = new JSONObject();
    res.put("ok", false);
    res.put("message", "Type " + req.getString("type") + " is not supported.");
    return res;
  }

  // creates the error message for no given type
  static JSONObject noType(JSONObject req){
    System.out.println("No type request: " + req.toString());
    JSONObject res = new JSONObject();
    res.put("ok", false);
    res.put("message", "No request type was given.");
    return res;
  }

  // From: https://www.baeldung.com/java-validate-json-string
  public static JSONObject isValid(String json) {
    try {
      new JSONObject(json);
    } catch (JSONException e) {
      try {
        new JSONArray(json);
      } catch (JSONException ne) {
        JSONObject res = new JSONObject();
        res.put("ok", false);
        res.put("message", "req not JSON");
        return res;
      }
    }
    return new JSONObject();
  }

  // sends the response and closes the connection between client and server.
  static void overandout() {
    try {
      os.close();
      in.close();
      sock.close();
    } catch(Exception e) {e.printStackTrace();}

  }

  // sends the response and closes the connection between client and server.
  static void writeOut(JSONObject res) {
    try {
      os.writeUTF(res.toString());
      // make sure it wrote and doesn't get cached in a buffer
      os.flush();

    } catch(Exception e) {e.printStackTrace();}

  }
}