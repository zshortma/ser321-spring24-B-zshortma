import java.io.*;
/**
 * Purpose: demonstrate simple Java Fraction class with command line,
 * jdb debugging, and Ant build file.
 *
 * Ser321 Foundations of Distributed Applications
 * see http://pooh.poly.asu.edu/Ser321
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version January 2020
 */
public class Fraction {

   private int numerator, denominator;

   public Fraction(){
      numerator = denominator = 0;
   }

   public void print() {
    System.out.print(numerator + "/" + denominator );
   }

   public void setNumerator (int n ){
      numerator = n;
   }

   public void setDenominator (int d) {
      denominator = d;
   }

   public int getDenominator() {
      return denominator;
   }

   public int getNumerator() {
      return numerator;
   }

   public static void main (String args[]) {
	    try {
	        if (args.length == 2) {
	            int numerator = Integer.parseInt(args[0]);
	            int denominator = Integer.parseInt(args[1]);
	            
	           
	            Fraction frac = new Fraction();

	 
	            frac.setNumerator(numerator);
	            frac.setDenominator(denominator);

	    
	            System.out.print("The fraction is: ");
	            frac.print();
	            System.out.println("");
	        } else {
	            System.out.println("Please provide two arguments: numerator and denominator");
	        }
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	}
}

