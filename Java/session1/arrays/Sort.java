package session1.arrays;
import java.util.Scanner;
public class Sort {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        int[] arr = new int[10];

        System.out.println("Enter the array elements");

        for (int i = 0; i < 10; i++) {
            arr[i] = s.nextInt();
        }
        //using bubble sort for sorting 
        // we iteratively check for smaller element and keep on swapping 
        for (int i = 0; i < 9; i++) {
            for (int j = i + 1; j < 10; j++) {
                if (arr[i] > arr[j]) {
                    int temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }

        System.out.println("After sorting -");
        for (int i = 0; i < 10; i++) {
            System.out.print(arr[i]+" ");
        }
        s.close();

    }
}
