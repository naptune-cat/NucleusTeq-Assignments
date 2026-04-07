package session1.controlflow;
import java.util.Scanner;

public class MultiplicationTable {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the number you want multiplication table of: ");
        int num = s.nextInt();
        for (int i = 1; i <= 10; i++) {
            System.out.println(num +"  x  "+i+"  = "+ num*i);
        }
        s.close();
    }
}
