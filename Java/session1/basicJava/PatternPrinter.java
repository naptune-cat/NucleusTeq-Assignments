package session1.basicjava;

import java.util.Scanner;

public class PatternPrinter {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Choose the shape you want to print");
        System.out.println("1. Square");
        System.out.println("2. Traingle");
        int choice = s.nextInt();
        System.out.println("Enter number of rows");
        int rows = s.nextInt();
        switch (choice) {
            case 1:
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < rows; j++) {
                        System.out.print("*");
                    }
                    System.out.println();
                }
                break;
            case 2:
                for (int i = 1; i <= rows; i++) {
                    for (int j = 1; j <= i; j++) {
                        System.out.print("*");
                    }
                    System.out.println();
                }
            default:
                System.out.println("Invalid choice");

        } 
        s.close();
    }
   
}
