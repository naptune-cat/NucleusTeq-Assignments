"""
Questions 4-6
Variables and Data Types
"""


# Question 4
def display_variable_types()-> None:
    """
    Create variables of different data types
    and display their types.
    """

    age = 23
    height = 5.4
    name = "Zoya"
    is_employee = True

    print(f"Value: {age}, Type: {type(age)}")
    print(f"Value: {height}, Type: {type(height)}")
    print(f"Value: {name}, Type: {type(name)}")
    print(f"Value: {is_employee}, Type: {type(is_employee)}")


# Question 5
def swap_numbers()-> None:
    """
    Swap two numbers using Python
    """

    first_number = int(input("Enter first number: "))
    second_number = int(input("Enter second number: "))

    print(f"Before swapping: {first_number}, {second_number}")

    first_number, second_number = second_number, first_number

    print(f"After swapping: {first_number}, {second_number}")


# Question 6
def arithmetic_operations()-> None:
    """
    Perform basic arithmetic operations
    on two user-provided numbers.
    """

    first_number = float(input("Enter first number: "))
    second_number = float(input("Enter second number: "))

    print(f"Sum: {first_number + second_number}")
    print(f"Difference: {first_number - second_number}")
    print(f"Multiplication: {first_number * second_number}")

    if second_number != 0:
        print(f"Division: {first_number / second_number}")
    else:
        print("Division by zero is not allowed.")


if __name__ == "__main__":
    display_variable_types()
    swap_numbers()
    arithmetic_operations()