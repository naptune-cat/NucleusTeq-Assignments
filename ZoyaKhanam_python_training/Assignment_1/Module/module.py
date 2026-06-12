"""
Questions 22-24
Modules
"""

import math
import random

from Module.custom_math import cube, square


# Question 22
def math_operations():
    """
    Demonstrate the use of the math module.
    """
    print("-"*30)
    print("Demonstrating use of math module-")
    number = int(input("Enter a number: "))

    print(f"Square Root: {math.sqrt(number)}")
    print(f"Power (number²): {math.pow(number, 2)}")
    print(f"Factorial: {math.factorial(number)}")


# Question 23
def generate_random_numbers():
    """
    Generate random numbers using the random module.
    """
    print("-"*30)
    print("Random number generater")
    print("Random Integer:", random.randint(1, 100))
    print("Random Float:", random.random())


# Question 24
def use_custom_module():
    """
    Import and use functions from a custom module.
    """
    print("-"*30)
    print("Import and use funcions from a custom module")
    number = int(input("Enter a number: "))

    print(f"Square: {square(number)}")
    print(f"Cube: {cube(number)}")


if __name__ == "__main__":
    math_operations()
    generate_random_numbers()
    use_custom_module()