package session1.basicjava;
import java.util.Scanner;

public class AreaCalculator{
    public static void main(String[] args){
        Scanner s = new Scanner(System.in);
        
        System.out.println("Choose shape: (Enter shape number) ");
        System.out.println("1. Circle");  
        System.out.println("2. Rectangle");  
        System.out.println("3. Triangle");
        System.out.println("Enter Your choice: ");
        int choice = s.nextInt();
        //using switch statment to write specific logic for specific shapes
        switch (choice) {
            case 1:
                System.out.println("Enter the radius: ");
                double radius = s.nextDouble();
                double circleArea = 3.14 * radius * radius;
                System.out.println("Area of Circle = " + circleArea);
                break;
            case 2:
                System.out.println("Enter the length: ");
                double length = s.nextDouble();
                System.out.println("Enter the breadth: ");
                double breadth = s.nextDouble();
                double rectangleArea = length * breadth;
                System.out.println("Area of Rectangle = " + rectangleArea);
                break;
            case 3:
                System.out.println("Enter the base: ");
                double base = s.nextDouble();
                System.out.println("Enter the height: ");
                double height = s.nextDouble();
                double traingleArea = 0.5 * base * height;
                System.out.println("Area of Rectangle = " + traingleArea);
                break;
            default:
                System.out.println("Invalid shape");
        }
        // we should close the scanner object after all inputs are done
        // because java uses a connection to keyboard input stream we need to free
        // the resources 
        s.close();
    }
}