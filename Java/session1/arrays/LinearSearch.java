package session1.arrays;
import java.util.Scanner;

public class LinearSearch {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        int[] arr = { 10, 23, 98, 59, 99, 1111, 27, 56,38,49 };

        System.out.println("Enter the number you want to search");
        int target = s.nextInt();
        boolean isFound = false;
        for (int i = 0; i < arr.length; i++) {
            if (target == arr[i]) {
                System.out.println("Element found on Index :" + i);
                isFound = true;
            }
        }
        if (!isFound) {
            System.out.println("Element does not exist in array");
        }
        s.close();
    }
}
