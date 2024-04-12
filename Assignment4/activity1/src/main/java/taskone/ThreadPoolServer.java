package taskone;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class: ThreadPoolServer
 * Description: Multi-threaded server tasks with a max.
 */

public class ThreadPoolServer {
    private final int maxConnections;
    private final ExecutorService threadPool;
    private final AtomicInteger activeConnections;

    public ThreadPoolServer(int maxConnections) {
        this.maxConnections = maxConnections;
        this.threadPool = Executors.newFixedThreadPool(maxConnections);
        this.activeConnections = new AtomicInteger(0);
    }

public void start(String host, int port, int maxConnections, StringList strings) {
    
    
    try (ServerSocket serverSocket = new ServerSocket(port, 0, InetAddress.getByName(host))) {
        System.out.println("ThreadPoolServer Started on " + host + ":" + port);
        while (true) {
            System.out.println("Accepting a Request...");
            Socket socket = serverSocket.accept();
            // Check if maximum connections reached
            if (activeConnections.get() > maxConnections) {
                System.out.println("Max connections reached. Rejecting new connection.");
                socket.close();
            } else {
                // Submit a task to the thread pool for handling the client connection
                threadPool.submit(new ClientHandler(socket, activeConnections, maxConnections, strings));
                activeConnections.incrementAndGet(); // Increment active connections count
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}


    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: gradle runTask3 -Pport=9099 -PmaxConnections=5 -q --console=plain");
            System.exit(1);
        }
        int port;
        String host = "localhost"; 
        int maxConnections;
        StringList strings = new StringList();

        try {
            port = Integer.parseInt(args[0]);
            maxConnections = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number or maxConnections: " + e.getMessage());
            System.exit(2);
            return;
        }

        ThreadPoolServer server = new ThreadPoolServer(maxConnections);
        server.start(host, port, maxConnections, strings );
    }
}

 
class ClientHandler implements Runnable {
    private final Socket socket;
    private final AtomicInteger activeConnections;
    private final int maxConnections;
    private final StringList strings;

    public ClientHandler(Socket socket, AtomicInteger activeConnections, int maxConnections, StringList strings) {
        this.socket = socket;
        this.activeConnections = activeConnections;
        this.maxConnections = maxConnections;
        this.strings = strings;
    }

    @Override
    public void run() {
        if (socket == null) {
            System.out.println("Socket is null. Exiting thread.");
            return;
        }

        try {
            // Check if maximum connections reached
            if (activeConnections.get()-1 > maxConnections) {
                System.out.println("Max connections reached. Rejecting new connection.");
                socket.close();
                return;
            }

            // Increment active connections count
            activeConnections.incrementAndGet();
            Performer performer = new Performer(socket, strings);
            performer.doPerform();
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Decrement active connections count
                activeConnections.decrementAndGet();
            }
        }
    }
}

