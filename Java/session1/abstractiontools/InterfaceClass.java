package session1.abstractiontools;

// interface is a fuly abstract class we can only declare a method but not implement it  
//interface provides full data hiding 
interface Vehicle {
    void start();
}

//we use implements keyword for inheriting interfaces
//we MUSt implement all the methods while inheriting from interface
class Car implements Vehicle {
    public void start() {
        System.out.println("Car starts with key");
    }
}

public class InterfaceClass {
    
    public static void main(String[] args) {
        Car c = new Car();
        c.start();
    }
}

