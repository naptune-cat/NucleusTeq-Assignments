package session1.controlflow;


public class SumOfEvenNumbers {
    public static void main(String[] args) {
        int result = 0;
        int i=1;
        while (i <= 10) {
            if (i % 2 == 0) {
               result += i; 
            }
            i++;
        }
        System.out.println("Sum of Even numbers from 1 to 10 is "+result);
    }
}
