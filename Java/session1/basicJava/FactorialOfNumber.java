package session1.basicjava;

import java.util.Scanner;

public class FactorialOfNumber {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the number:");
        int num = s.nextInt();
        int dummy = num;
        int factorial = 1;
        while (dummy != 0) {
            factorial *= dummy;
            dummy--;
        }
        System.out.println("Factorial of "+num+ " is " + factorial);
        s.close();
    }
}