package client;

import java.net.*;
import java.io.*;

import org.json.*;

import proto.RequestProtos.Request;
import proto.ResponseProtos.Response;
import proto.ResponseProtos.Leader;

import java.util.*;
import java.util.stream.Collectors;

class SockBaseClient {
    
/**
 * Method : Main
 * @param args
 * @throws Exception
 */
    public static void main (String args[]) throws Exception {
        Socket serverSock = null;
        OutputStream out = null;
        InputStream in = null;
        int i1=0, i2=0;
        int port = 9099; // default port

        // Make sure two arguments are given
        if (args.length != 2) {
            System.out.println("Expected arguments: <host(String)> <port(int)>");
            System.exit(1);
        }
        
        String host = args[0];
        
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be integer");
            System.exit(2);
        }
        
       
        System.out.println("Please provide your name for the server. :-)");
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String strToSend = stdin.readLine();

      
        Request op = Request.newBuilder()
                .setOperationType(Request.OperationType.NAME)
                .setName(strToSend).build();
        
        Response response;
        
        
        try {
          
            serverSock = new Socket(host, port);
            String serverState = "going";
            out = serverSock.getOutputStream();
            in = serverSock.getInputStream();

            op.writeDelimitedTo(out);

            response = Response.parseDelimitedFrom(in);

            System.out.println(response.getHello());
            
            /**
             * Menu Logic
             */
            while(true)
            {
                try{
                    
                    strToSend = stdin.readLine();
                    
                while(!strToSend.equals("1") && !strToSend.equals("2") && !strToSend.equals("3"))
                    
                {
                    
                    strToSend = stdin.readLine();
                }
                
                /**
                 * Leaderboard Logic Call
                 */
                if(strToSend.equals("1"))
                {

                    op = Request.newBuilder()
                            .setOperationType(Request.OperationType.LEADERBOARD).build();
                    op.writeDelimitedTo(out);
                    response = Response.parseDelimitedFrom(in);
                    System.out.println(response.getMessage());
                    response = Response.parseDelimitedFrom(in);
                     System.out.println(response.getHello());
                     
                }
                
                /**
                 * Game Logic
                 */
                if(strToSend.equals("2"))
                {
                    
                    
                    op = Request.newBuilder()
                            .setOperationType(Request.OperationType.START).build();
                    op.writeDelimitedTo(out);
                    response = Response.parseDelimitedFrom(in);

                    boolean guessing = true;
                
                    System.out.println("Phrase: \n" + response.getPhrase());
                    System.out.println("Task: \n" + response.getTask());

                    while(guessing)
                    {

                        System.out.println("Please guess a letter or type exit to quit");
                        strToSend = stdin.readLine().toUpperCase();

                        
                        if (strToSend.equalsIgnoreCase("exit")) {
                            op = Request.newBuilder()
                                    .setOperationType(Request.OperationType.QUIT).build();
                            op.writeDelimitedTo(out);
                            response = Response.parseDelimitedFrom(in);
                            System.out.println("Thanks for playing!");
                            return;
                        }
                        
                        
                        op = Request.newBuilder()
                                .setOperationType(Request.OperationType.START)
                                .setGuess(strToSend).build();
                            op.writeDelimitedTo(out);
                            response = Response.parseDelimitedFrom(in);
                       
                        
                            String phrase = response.getPhrase();
                            System.out.println("Please guess a letter or type exit to quit");
                            System.out.println("Phrase: \n" + response.getPhrase());
                            System.out.println("Task: \n" + response.getTask());
                           
                            
                            /**
                             * Letter guess checker
                             */
                            boolean allLettersGuessed = false;
                            for (char c : phrase.toCharArray()) {
                                if (c != ' ' && c != '_' && !Character.isLetter(c)) {
                                    continue;
                                }
                                if (c == '_') {
                                    allLettersGuessed = false;
                                }
                                if (c != '_') {
                                    allLettersGuessed = true;
                                   
                                }
                            }
                            
                            
                            if (allLettersGuessed) {
                                break;
                            }
                    }
                   
                    /**
                     * Restart menu when done
                     */
                    System.out.println(response.getHello());
                }
                
                
                /**
                 * Quit Logic
                 */
                if(strToSend.equals("3"))
                {

                    op = Request.newBuilder()
                            .setOperationType(Request.OperationType.QUIT).build();
                    op.writeDelimitedTo(out);
                    response = Response.parseDelimitedFrom(in);
                    System.out.println(response.getMessage());
                    break;
                }
                
                if (!strToSend.matches("[1-3]")) {
                    System.out.println("Invalid input. Please enter 1, 2, or 3.");
                    continue;
                  }
                
                }
                 catch(Exception e)
                {
                    e.printStackTrace();
                }
                
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null)   in.close();
            if (out != null)  out.close();
            if (serverSock != null) serverSock.close();
        }
    }
}