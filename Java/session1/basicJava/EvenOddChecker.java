package session1.basicjava;

import java.util.Scanner;

public class EvenOddChecker {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the number:");
        int number = s.nextInt();

        if(number%2==0)
            System.out.println(number + " is Even number");
        else
            System.out.println(number + " is Odd number");
        
        s.close();
    }
}
