package session1.oops;
// polymorphism is the ability of functions to have same name but behave differently depending upon the arguments passed.

//There are two types of polymorphism 
// 1. Compile time polymorphism - method overloading / operator overloading
//   which fn is to be called is known during compile time

// 2. runtime polymorphism - method overriding
//   which fn is to be called is known during run time depending on the object that calls the fn

class Animal {
   
    //  Method with one parameter (method overloading)
    void sound(String animalType) {
        System.out.println(animalType + " makes a sound");
    }
    // this fn is being overridden by child class fn
    void name() {
        System.out.println("Animal have different names");
    }
}

class Cat extends Animal {

    // Same method name but different parameter (method overloading)
    void sound(String animalType, int times) {
        for (int i = 1; i <= times; i++) {
            System.out.println(animalType + " Meow");
        }
    }
    // we use override keyword to denote that the following fn is overriding fn from parent class
    @Override
    void name() {
        System.out.println("My cat name is Mau");
    }
}

public class Polymorphism {
    public static void main(String[] args) {
        // ---method overloading---

        //calling parent class fn
        Animal animal = new Animal();
        animal.sound("Animal");

        //calling child class fn
        Cat cat = new Cat();
        cat.sound("Cat", 2);

        // ---method overriding---

        //we are referencing our parent class object to our child class
        Animal animal1 = new Cat();
        // now calling this name fn will call the child class fn
        animal1.name();

        //if we normally call the name from animal object we will get name() fn from parent class only
        animal.name();
    }
}