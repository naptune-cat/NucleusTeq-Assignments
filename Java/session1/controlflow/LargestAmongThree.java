package session1.controlflow;
import java.util.Scanner;
public class LargestAmongThree {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        System.out.print("Enter first number: ");
        int firstNumber = s.nextInt();

        System.out.print("Enter second number: ");
        int secondNumber = s.nextInt();

        System.out.print("Enter third number: ");
        int thirdNumber = s.nextInt();

        int largestNumber;
        //I am using if,else if, else block to check conditions 
        if (firstNumber >= secondNumber && firstNumber >= thirdNumber) {
            largestNumber = firstNumber;
        } else if (secondNumber >= firstNumber && secondNumber >= thirdNumber) {
            largestNumber = secondNumber;
        } else {
            largestNumber = thirdNumber;
        }

        System.out.println("Largest number is: " + largestNumber);

        s.close();
    }

}
