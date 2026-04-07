package session1.datatypes;

public class PrimitiveAndReferenceDataType {
    public static void main(String[] args) {
        //----Primitive Data Type----
        //These are the basic data type which ae provided by java.
        // they store actual values directly in memory
        // these cannot store null

        //example of primitive datatypes

        
        //byte is used to store smallinteger values
        byte age = 20;
        
        // short is larger than byte smaller than int
        short year = 2026;

        //int is most commonly used in programming
        int booksCount = 100000;

        // long has larger capacity than int
        long mobileNumber = 9876543210L;
        
        // float is used to store decimal number
        float temperature = 36.5f;
        
        //double also stores decimal it has bigger capacity than float. 
        // By default every decimal number is stored as double to avoid data loss
        double salary = 45000.75;
        
        //char is used to store characters like 'a' '1' '-' etc
        char grade = 'A';

        //boolean stores either true or false
        boolean isPassed = true;

        // ----Reference Data Types:----

        //  Reference data types do not store the actual value directly. 
        // They store the memory address of the object. These are created using classes, arrays, and strings.

        //example of reference datatypes

        //String is used to store the sequence of characters like "Hello World"
        String name = "Zoya";

        // Array
        //Array is a linear Data structure which is used to store the multiple values of same data type it can be int , char,string etc
        //we have index to access the content of arr starting from 0 to size-1
        int[] numbers = { 1, 2, 3, 4 };

    }
}
