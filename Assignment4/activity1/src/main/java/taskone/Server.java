/**
  File: Server.java
  Author: Student in Fall 2020B
  Description: Server class in package taskone.
*/
package taskone;

import java.net.ServerSocket;
import java.net.Socket;


/**
 * Class: Server
 * Description: Server tasks.
 */
class Server {

    public static void main(String[] args) throws Exception {
        int port;
        String host = "localhost"; 
        StringList strings = new StringList();

        if (args.length != 2) {
            System.out.println("Usage: gradle runTask1 -Pport=9099 -Phost=localhost -q --console=plain");
            port = 8000; // Default port
            host = "localhost"; 
        } else {
            port = Integer.parseInt(args[1]);
            host = args[0];
        }

        ServerSocket server = new ServerSocket(port);
        System.out.println("Server Started...");
        while (true) {
            System.out.println("Accepting a Request...");
            Socket sock = server.accept();

            Performer performer = new Performer(sock, strings);
            performer.doPerform();
            try {
                System.out.println("close socket of client ");
                sock.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
