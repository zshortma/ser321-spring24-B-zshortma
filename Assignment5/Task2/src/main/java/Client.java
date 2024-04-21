import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    
    private static BufferedReader bufferedReader;
    private PrintWriter out = null;
    private Socket socket = null;
    public  int counter = 0;
    public static int updated;
    private static Node node;
    public static String input;
    
    public Client(Socket socket, Node node) throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        this.node = node;
        this.socket = socket;
    }
    
    public static void main(String[] args) throws Exception {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            //String input;
            node = new Node(bufferedReader, args[0], null);

            System.out.println("You started a client! Please enter a string [space] char");
            System.out.println("example : hello o");
            

            while (true) {
                // Read user input
                input = bufferedReader.readLine();

                // Check for exit command
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting client...");
                    break;
                }

                if (input != null) {
                    Node.sendToLeader(input);
                    break;
                } else {
                    System.out.println("node is not open");
                }

            }
            Thread.sleep(30000);
            /* Printing leader sum. */
            System.out.println(node.readLastLineFromFile());
            
            /* Call leader/node to start validation checking. */
            System.out.println(node.leaderChecker(input));
            
            if(node.readLastLineFromFile() == node.leaderChecker(input)) {
                System.out.println("These matched!!");
            } else {
                System.out.println("Not correct, please try again!");
            }
            
            System.out.println("Thanks for using! Closing now!");
            
            System.exit(0);
            // Close resources
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
