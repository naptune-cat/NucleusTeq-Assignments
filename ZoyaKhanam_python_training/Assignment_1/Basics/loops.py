"""
Questions 12-16
Python Basics - Loops
"""


# Question 12
def print_numbers():
    """
    Print numbers from 1 to 100 using a loop.
    """
    print("-"*30)
    print("printing numbers from 1 to 100 using a loop.")
    for number in range(1, 101):
        print(number)


# Question 13
def multiplication_table(number: int):
    """
    Display the multiplication table of a given number.
    """
    print("-"*30)
    print("displaying multiplication table of a given number.")

    for multiplier in range(1, 11):
        print(f"{number} x {multiplier} = {number * multiplier}")


# Question 14
def factorial():
    """
    Calculate the factorial of a number using a loop.
    """
    print("-"*30)
    print("Calculate the factorial of a number using a loop.")
    number = int(input("Enter a number: "))

    if number < 0:
        print("Factorial is not defined for negative numbers.")
        return

    result = 1

    for value in range(1, number + 1):
        result *= value

    print(f"Factorial of {number} is {result}")


# Question 15
def reverse_number():
    """
    Reverse a number using a loop.
    """
    print("-"*30)
    print("reverse a numbere.")
    number = int(input("Enter a number: "))

    reversed_number = 0

    while number > 0:
        digit = number % 10
        reversed_number = reversed_number * 10 + digit
        number //= 10

    print(f"Reversed number: {reversed_number}")


# Question 16
def is_prime():
    """
    Check whether a number is prime.
    """
    print("-"*30)
    print("Checking whether number is prime or not.")
    number = int(input("Enter a number: "))

    if number <= 1:
        print(f"{number} is not a prime number.")
        return

    for divisor in range(2, int(number ** 0.5) + 1):
        if number % divisor == 0:
            print(f"{number} is not a prime number.")
            return

    print(f"{number} is a prime number.")


if __name__ == "__main__":
    print_numbers()
    number = int(input("Enter a number to get multiplication table: "))
    multiplication_table(number)
    factorial()
    reverse_number()
    is_prime()