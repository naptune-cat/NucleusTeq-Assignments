package session1.strings;
import java.util.Scanner;

public class ReverseString {
    public static void main(String[] args) {

        Scanner s = new Scanner(System.in);

        System.out.print("Enter string to reverse: ");
        String str = s.nextLine();

        String reversed = "";

        for (int i = str.length() - 1; i >= 0; i--) {
            reversed += str.charAt(i);
        }

        System.out.println("Reversed string: " + reversed);
        s.close();
    }
}