package session1.datatypes;

public class OperatorsDemo {
    public static void main(String[] args) {
        int firstNumber = 10;
        int secondNumber = 2;
        
        //Arithmetic operator
          
        //These are the basic mathematical operators
        System.out.println("Arithmetic Operators:");
        System.out.println("Addition: " + (firstNumber + secondNumber));
        System.out.println("Subtraction: " + (firstNumber - secondNumber));
        System.out.println("Multiplication: " + (firstNumber * secondNumber));
        System.out.println("Division: " + (firstNumber / secondNumber));
        //modulus % is used to give remainder
        System.out.println("Modulus: " + (firstNumber % secondNumber));



        // Relational Operators

        //These operators are basically used to compare between the two variables
        
        System.out.println("\nRelational Operators:");

        //greater than operator
        System.out.println(firstNumber + " > " + secondNumber + ": " + (firstNumber > secondNumber));

        //less than operator
        System.out.println(firstNumber + " < " + secondNumber + ": " + (firstNumber < secondNumber));
        
        //equals to operator
        System.out.println(firstNumber + " ==" + secondNumber + ": " + (firstNumber == secondNumber));
        
        //not equals to operator
        System.out.println(firstNumber + " != " + secondNumber + ": " + (firstNumber != secondNumber));
        
        //greater than equals to operator
        System.out.println(firstNumber + " >= " + secondNumber + ": " + (firstNumber >= secondNumber));
        
        //less than equals to operator
        System.out.println(firstNumber + " <= " + secondNumber + ": " + (firstNumber <= secondNumber));

        
        // Logical Operators

        //these operators give either true or false after the execution
        boolean isTodayFun = true;
        boolean isTodaySunday = false;

        System.out.println("\nLogical Operators:");

        // logical AND operator
        //only returns true if both are true else false
        System.out.println("isTodayFun && isTodaySunday: " + (isTodayFun && isTodaySunday));

        //Logical OR operator
        // returns true if either is true
        System.out.println("isTodayFun || isTodaySunday: " + (isTodayFun || isTodaySunday));

        //Logical NOT operator
        //returns negation of the given value
        System.out.println("!isTodaySunday: " + (!isTodaySunday));
    }
}
