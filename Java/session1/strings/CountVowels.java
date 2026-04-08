package session1.strings;

import java.util.Scanner;

public class CountVowels {
    static int countVowels(String str) {
        int count = 0;
        //converting to lowercase inorder to avoid difference of 'A' and 'a'
        str = str.toLowerCase();

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u') {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {

        Scanner s = new Scanner(System.in);

        System.out.print("Enter a string to count vowels: ");
        //nextLine() does not break after empty space (' ')
        String str = s.nextLine();
        //calling the function
        int vowelCount = countVowels(str);
        System.out.println("Number of vowels: " + vowelCount);
        s.close();
    }
}
