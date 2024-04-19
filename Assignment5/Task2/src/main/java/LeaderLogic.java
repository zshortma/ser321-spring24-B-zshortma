import java.util.ArrayList;
import java.util.List;

public class LeaderLogic {
    
    /* Take input and node */
    public static List<String> splitString(String input, int numNodes) {
        
        /* Split the string into parts. */
        List<String> parts = new ArrayList<>();
        int length = input.length();
        int partLength = (int) Math.ceil((double) length / numNodes);
        for (int i = 0; i < length; i += partLength) {
            parts.add(input.substring(i, Math.min(length, i + partLength)));
        }
        return parts;
    }
}
