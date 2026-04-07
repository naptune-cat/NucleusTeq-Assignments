package session1.controlflow;

import java.util.Scanner;

public class CheckPrime {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Check whether number is prime or not");
        System.out.println("Enter the number: ");
        int num = s.nextInt();

        //maintaining an isPrime var which will store whether the numis prime or not
        boolean isPrime = true;

        //negative numbers or 1 & 0 are not primenumbers so
        if(num<=1)
            isPrime = false;
        else {
            //looping from 2 to num/2
            for (int i = 2; i <= num / 2; i++) {
                if (num % i == 0) {
                    isPrime = false;
                    break;
                }
            }
        }

        if (isPrime) {
            System.out.println(num + " is a prime number");
        }
        else {
            System.out.println(num + " is not a prime number");
        }
        s.close();
    }
}
