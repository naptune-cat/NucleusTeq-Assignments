package session1.exception;
//exception handling is a way to handle errors gracefully without breaking the flow of execution from our program
//try and catch blocks are used for exception handling 
public class ExceptionDemo {
    public static void main(String[] args) {
        int a = 10;
        int b = 0;

        // we write the code which can throw error/exception in futureinside try block

        //catch block catches the exception and allows us to give either error messages to the user or doing more functionality eg- asking user to input another value etc
        try {
            int result = a / b;
            System.out.println(result);
        } catch (ArithmeticException e) {
            System.out.println("Cannot divide by zero");
        }

        // there are two more keywords which are used in exception handling-
        // 1. throw - it is used tomanually create exception 
        int age = 16;
        if (age < 18) {
            throw new ArithmeticException("Not eligible for voting ");
            //this will print an error message while executing in our system
            //which developers can use to debug or be more clear as to what actually went wrong
        }
        System.out.println("Eligible to vote");

        //2. throws - it is used with method signature to tell that this method can throw exception 
        // public static void findFile() throws IOException{
        // ...
        // }
    }
}

