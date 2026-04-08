package session1.strings;

import java.util.Arrays;
import java.util.Scanner;

public class CheckAnagram {
    static boolean isAnagram(String str1, String str2) {

        // converting to lowercase
        str1 = str1.toLowerCase();
        str2 = str2.toLowerCase();

        // If lengths are different, the strings cant be anagrams
        if (str1.length() != str2.length()) {
            return false;
        }

        // Convert strings to arrays for easy sorting
        //split is used to break string into individual characters
        String[] arr1 = str1.split("");
        String[] arr2 = str2.split("");


        // Sorting both arrays
        Arrays.sort(arr1);
        Arrays.sort(arr2);

        // Comparing both the sorted arrays
        return Arrays.equals(arr1, arr2);
    }

    public static void main(String[] args) {

        Scanner s = new Scanner(System.in);

        System.out.print("Enter first string: ");
        String str1 = s.nextLine();

        System.out.print("Enter second string: ");
        String str2 = s.nextLine();
        // two strings are anagram if they contain same characters
        // example LEMON -> MELON
        if (isAnagram(str1, str2)) {
            System.out.println("The strings are anagrams.");
        } else {
            System.out.println("The strings are not anagrams.");
        }
        s.close();
    }
}

