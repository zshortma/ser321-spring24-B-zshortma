package server;

import java.net.*;
import java.io.*;
import java.util.*;
import org.json.*;
import java.lang.*;

import proto.RequestProtos.Request;
import proto.RequestProtos.Logs;
import proto.RequestProtos.Message;
import proto.ResponseProtos.Response;
import proto.ResponseProtos.Leader;

class SockBaseServer {
    static String logFilename = "logs.txt";

    ServerSocket serv = null;
    InputStream in = null;
    OutputStream out = null;
    Socket clientSocket = null;
    int port = 9099; // default port
    Game game;
    int counter;


    /**
     * Sock Base Server
     * @param sock
     * @param game
     */
    public SockBaseServer(Socket sock, Game game){
        this.clientSocket = sock;
        this.game = game;
        try {
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
        } catch (Exception e){
            System.out.println("Error in constructor: " + e);
        }
    }
    
    
    /**
     * Error Response Method
     * @param out
     * @param errorMessage
     * @param errorType
     * @throws IOException
     */
    private void sendErrorResponse(OutputStream out, String errorMessage, int errorType) throws IOException {
        Response response = Response.newBuilder()
                .setResponseType(Response.ResponseType.ERROR)
                .setMessage(errorMessage)
                .build();
        response.writeDelimitedTo(out);
    }
    
    
    

    /**
     * Handle Request
     * @throws IOException
     */
    public void handleRequests() throws IOException {
        String name = "";
        int wins = 0;
        int correctBonus = 0;
        System.out.println("Ready...");
        try {

                Request op = Request.parseDelimitedFrom(in);
                String result = null;

                if (op == null || !op.hasOperationType()) {
                    sendErrorResponse(out, "Required field missing: operationType", 1);
                    return; 
                }
            

                if (op.getOperationType() == Request.OperationType.NAME) {
                    name = op.getName();
                    
                    if (!op.hasName()) {
                        sendErrorResponse(out, "Required field missing: name", 1);
                        return; 
                    } 
                    

                    writeToLog(name, Message.CONNECT);
                    System.out.println("Got a connection and a name: " + name);
                    Response response = Response.newBuilder()
                            .setResponseType(Response.ResponseType.WELCOME)
                            .setHello("Hello " + name + " and welcome. \nWhat would you like to do? \n 1 - to see the leader board \n 2 - to enter a game \n 3 - to quit")
                            .build();
                    response.writeDelimitedTo(out);
                    
                }
                
                while(true)
                {
                    boolean answeredCorrectly = false;
                  
                    op = Request.parseDelimitedFrom(in);
                    if(op.getOperationType() == Request.OperationType.LEADERBOARD) 
                    {
                        Response response = Response.newBuilder()
                            .setResponseType(Response.ResponseType.LEADERBOARD)
                            .setMessage("Number of wins: " + wins)
                            .build();
                        response.writeDelimitedTo(out);
                            response = Response.newBuilder()
                            .setResponseType(Response.ResponseType.WELCOME)
                            .setHello("Hello " + name + " and welcome. \nWhat would you like to do? \n 1 - to see the leader board \n 2 - to enter a game \n 3 - to quit")
                            .build();
                        response.writeDelimitedTo(out);
                    }
                    if(op.getOperationType() == Request.OperationType.START )
                    {
                        if (counter == 0 ) {
                        game.newGame(); 
                        counter++;
                        }
                        
                        while(true)
                        {
                          
                            Response response2 = Response.newBuilder()
                                    .setResponseType(Response.ResponseType.TASK)
                                    .setPhrase(game.getPhrase())
                                    .build();
                            response2.writeDelimitedTo(out);

                            System.out.println("Task: " + response2.getResponseType());
                            System.out.println("Phrase: \n" + response2.getPhrase());
                            System.out.println("Task: \n" + response2.getTask());
                            
                            op = Request.parseDelimitedFrom(in);
                            String guess = op.getGuess();
                            
                            game.markGuess(guess.charAt(0));
                            
                            
                            String phrase = game.getPhrase();
                            boolean allCorrect = true;
                            for (int i = 0; i < phrase.length(); i++) {
                                char letter = phrase.charAt(i);
                                
                                if (!game.isGuessed(guess.charAt(0))) {
                                    allCorrect = false;
                                    break;
                                }
                                
                       
                            }
                            
                            response2 = Response.newBuilder()
                                    .setResponseType(Response.ResponseType.TASK)
                                    .setPhrase(game.getPhrase())
                                    .setTask("you guessed :" + guess)
                                    .build();
                            response2.writeDelimitedTo(out);
                       
                            break;

                        }
                        Response response = Response.newBuilder()
                                    .setResponseType(Response.ResponseType.WON)
                                    .build();
                        response.writeDelimitedTo(out);
                        
                        response = Response.newBuilder()
                                
                            .setResponseType(Response.ResponseType.WELCOME)
                            .setHello("Hello " + name + " and welcome. \nWhat would you like to do? \n 1 - to see the leader board \n 2 - to enter a game \n 3 - to quit")
                            .build();
                        response.writeDelimitedTo(out);
                    }  if(op.getOperationType() == Request.OperationType.GUESS )
                    {
                        
                     
                        Response response2 = Response.newBuilder()
                                .setResponseType(Response.ResponseType.TASK)
                                .setPhrase(game.getPhrase())
                                .setTask("task1")
                                .build();
                        response2.writeDelimitedTo(out);
                        
                        
                        op = Request.parseDelimitedFrom(in);
                        String guess = op.getGuess();
                        System.out.println(guess);
                        
                        if (guess.length() != 1) {
                  
                            sendErrorResponse(out, "Invalid guess. Please enter a single letter.", 2);
                            continue; 
                        }
                    
                        game.markGuess(guess.charAt(0));
                        String phrase = game.getPhrase();
                        boolean allCorrect = true;
              
                        for (int i = 0; i < phrase.length(); i++) {
                            char letter = phrase.charAt(i);
                            
                            if (!game.isGuessed(guess.charAt(0))) {
                                allCorrect = false;
                                break;
                            }
                            
                   
                        }
                      
                        response2 = Response.newBuilder()
                                .setResponseType(Response.ResponseType.TASK)
                                .setPhrase(game.getPhrase())
                                .setTask("test2")
                                .build();
                        response2.writeDelimitedTo(out);
                        
                 
                        System.out.println("Task guess: " + response2.getResponseType());
                        System.out.println("Phrase: \n" + response2.getPhrase());
                        System.out.println("Task: \n" + response2.getTask());
                        break;
                        
                    }
                    
                    if(op.getOperationType() == Request.OperationType.QUIT) {
                        System.out.println("Client quitting");
                        Response response = Response.newBuilder()
                            .setResponseType(Response.ResponseType.BYE)
                            .setMessage("Thanks for playing! See you next time!")
                            .build();
                        response.writeDelimitedTo(out);
                        break;
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (out != null)  out.close();
                if (in != null)   in.close();
                if (clientSocket != null) clientSocket.close();
            }
    }


    /**
     * Writing a new entry to our log
     * @param name - Name of the person logging in
     * @param message - type Message from Protobuf which is the message to be written in the log (e.g. Connect) 
     * @return String of the new hidden image
     */
    public static void writeToLog(String name, Message message){
        try {
            // read old log file 
            Logs.Builder logs = readLogFile();

            // get current time and data
            Date date = java.util.Calendar.getInstance().getTime();

            // we are writing a new log entry to our log
            // add a new log entry to the log list of the Protobuf object
            logs.addLog(date.toString() + ": " +  name + " - " + message);

            // open log file
            FileOutputStream output = new FileOutputStream(logFilename);
            Logs logsObj = logs.build();

            // This is only to show how you can iterate through a Logs object which is a protobuf object
            // which has a repeated field "log"

            for (String log: logsObj.getLogList()){

                System.out.println(log);
            }

            // write to log file
            logsObj.writeTo(output);
        }catch(Exception e){
            System.out.println("Issue while trying to save");
        }
    }

    /**
     * Reading the current log file
     * @return Logs.Builder a builder of a logs entry from protobuf
     */
    public static Logs.Builder readLogFile() throws Exception{
        Logs.Builder logs = Logs.newBuilder();

        try {
            // just read the file and put what is in it into the logs object
            return logs.mergeFrom(new FileInputStream(logFilename));
        } catch (FileNotFoundException e) {
            System.out.println(logFilename + ": File not found.  Creating a new file.");
            return logs;
        }
    }


    /**
     * Main method
     * @param args
     * @throws Exception
     */
    public static void main (String args[]) throws Exception {
        Game game = new Game();
        game.newGame();

        if (args.length != 2) {
            System.out.println("Expected arguments: <port(int)> <delay(int)>");
            System.exit(1);
        }
        int port = 9099; // default port
        Socket clientSocket = null;
        ServerSocket socket = null;

        try {
            port = Integer.parseInt(args[0]);
          
        } catch (NumberFormatException nfe) {
            System.out.println("[Port|sleepDelay] must be an integer");
            System.exit(2);
        }
        try {
            socket = new ServerSocket(port);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
        try {
            clientSocket = socket.accept();
            SockBaseServer server = new SockBaseServer(clientSocket, game);
            server.handleRequests();
        }
        catch(Exception e) {
            clientSocket = socket.accept();
            SockBaseServer server = new SockBaseServer(clientSocket, game);
            server.handleRequests();
        }
        while (true) {
            try{
                System.out.println("Accepting a Client...");
                clientSocket = socket.accept();
                SockBaseServer server = new SockBaseServer(clientSocket, game);
                server.handleRequests();
            }
            catch(Exception e){
                System.out.println("Server encountered an error while connecting to a client.");
            }
        }
    }

}