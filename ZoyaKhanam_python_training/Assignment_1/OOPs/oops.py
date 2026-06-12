"""
Questions 40-44
Object Oriented Programming
"""


# Question 40
class Student:
    """
    Student class with attributes and display method.
    """

    # init is constructor in python 
    # it is used to initialize the object of class
    def __init__(self, name, age, course):
        # Save the student's name, age and course.
        # self is like this keyword from cpp
        # it gives the current instance of class
        self.name = name
        self.age = age
        self.course = course

    def display_details(self):
        print("-"*30)
        print("Student Details")
        print(f"Name: {self.name}")
        print(f"Age: {self.age}")
        print(f"Course: {self.course}")


# Question 41
class Car:
    """
    Car class demonstrating constructor usage.
    """

    def __init__(self, brand, model):
        # Constructor initializes object attributes
        # when a new object is created.
        self.brand = brand
        self.model = model

    def display_details(self):
        print("-"*30)
        print("Car Details")
        print(f"Brand: {self.brand}")
        print(f"Model: {self.model}")


# Question 42
class Person:
    """
    Parent class.
    """

    def __init__(self, name):
        self.name = name


class Employee(Person):
    """
    Child class inheriting from Person.
    """

    def __init__(self, name, employee_id):

        # super() calls the constructor of the
        # parent class and reuses its initialization.
        super().__init__(name)

        self.employee_id = employee_id

    def display_details(self):
        print("-"*30)
        print("Employee Details")
        print(f"Name: {self.name}")
        print(f"Employee ID: {self.employee_id}")

"""
Access Modifiers in Python

1. public ->    name

2. private ->   __name (Double undescore)

3. protected->  _name (single underscore)
"""

# Question 43
class Bank:
    """
    Demonstrate encapsulation using private variables.
    """

    def __init__(self, balance):

        # Double underscore makes the variable private.
        # It should be accessed through methods instead
        # of directly from outside the class.
        self.__balance = balance

    def deposit(self, amount):
        self.__balance += amount

    def get_balance(self):

        # Getter method provides controlled access
        # to the private variable.
        return self.__balance


# Question 44
class Dog:
    """
    Demonstrate polymorphism.
    """

    def sound(self):
        print("Dog barks")


class Cat:
    """
    Demonstrate polymorphism.
    """

    def sound(self):
        print("Cat meows")


if __name__ == "__main__":

    print("-"*30)
    print("Student class with attributes and display method.")
    student = Student("Soumya", 21, "Computer Science")
    student.display_details()

    print("-"*30)
    print("Car class demonstrating constructor usage.")
    car = Car("Toyota", "Camry Hybrid")
    car.display_details()

    print("-"*30)
    print("Demonstrating Inheritance in classes.")
    employee = Employee("Zoya", "EMP101")
    employee.display_details()

    print("-"*30)
    print("Demonstrating Encapsulation using private variables.")
    bank = Bank(1000)
    bank.deposit(500)

    print("-"*30)
    print("Balance:", bank.get_balance())

    # Both Dog and Cat implement a method named sound().
    # The same method call behaves differently depending
    # on the object, demonstrating polymorphism.
    dog = Dog()
    cat = Cat()

    print("Demonstrating polymophism.")
    print("-"*30)
    dog.sound()
    cat.sound()