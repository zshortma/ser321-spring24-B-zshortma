package client;
/**
 * Class: Player 
 */

public class Player implements Comparable<Player> {

    private int wins;
    private String name;

    // constructor, getters, setters
    public Player(String name, int wins){
      this.wins = wins;
      this.name = name;
    }

    public int getWins(){
      return wins;
    }

    // override equals and hashCode
    @Override
    public int compareTo(Player player) {
        return (int)(player.getWins() - this.wins);
    }

    @Override
       public String toString() {
            return ("\n" +this.wins + ": " + this.name);
       }
}