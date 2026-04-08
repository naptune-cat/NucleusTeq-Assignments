package session1.abstractiontools;

//Abstract Class are those classes which can contain abstract as well as non abstract methods

//abstract class provide some data hiding 

// abstract keyword is used to define an abstract class
abstract class Car {
    abstract void engine();

    void color() {
        System.out.println("Blue color is trending");
    }
}

// all the child classes inheriting from Car parent class MUST implement the abstract method engine
class Bugatti extends Car {
    void engine() {
        System.out.println(" Bugatti has 8.0-liter W16 powerhouse 16-cylinde");
    }
}

class Supra extends Car {
    void engine() {
        System.out.println("Supra has 3.0L turbocharged inline 6-cylinder (B58)");
    }
}

public class AbstractClass {
    public static void main(String[] args) {
        Bugatti b = new Bugatti();
        b.engine();
        b.color();

        Supra s = new Supra();
        s.engine();
        s.color();
    }
}
