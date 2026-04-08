package session1.arrays;

import java.util.Scanner;
public class FindElementAverage {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        int[] arr = new int[10];
        double average = 0;
        int total = 0;
        System.out.println("Enter array elements (upto 10)");

        for (int i = 0; i < 10; i++) {
            arr[i] = s.nextInt();
            total += arr[i];
        }
        average = total / 10;
        System.out.println("Average of all elements is " + average);
        s.close();
    }
}
