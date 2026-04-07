package session1.datatypes;
import java.util.Scanner;

public class TemperatureConverter {
    public static void main(String[] args) {

        Scanner s = new Scanner(System.in);
        //I am simply taking choice from user and using switch statement to show specified conversion
        System.out.println("Choose conversion type:");
        System.out.println("1. Celsius to Fahrenheit");
        System.out.println("2. Fahrenheit to Celsius");

        int choice = s.nextInt();

        switch (choice) {
            case 1:{
                System.out.print("Enter temperature in Celsius: ");
                double celsius = s.nextDouble();
                double fahrenheit = (celsius * 9 / 5) + 32;
                System.out.println("Temperature in Fahrenheit: " + fahrenheit);
                break;}
            case 2:{
                System.out.print("Enter temperature in Fahrenheit: ");
                double fahrenheit = s.nextDouble();
                double celsius = (fahrenheit - 32) * 5 / 9;
                System.out.println("Temperature in Celsius: " + celsius);
                break;
            }
            default:{
                System.out.println("Invalid choice");
            }
        }
        s.close();
    }
}
