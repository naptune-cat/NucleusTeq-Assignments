"""
Questions 1-3 
Introduction to Python
"""

import sys

# Question 1
""" Write a program to print 'Welcome to Python Training'."""

# def function_name() -> return_type:

def welcome_message()-> None:
    print("-"*30)
    print("Welcome to Python Training")

# Question 2


def check_python_version()-> None:
    """ Write a program to check your Python version."""
    print("-"*30)
    print("Python version:", sys.version)

# Question 3

def user_info()-> None:
    """ 
    Take user input (name and age) and print a formatted message.
    """
    print("-"*30)
    print("Take user input (name and age) and print a formatted message.")
    name = input("Enter your name: ")
    age = input("Enter your age: ")
    print(f"Hello {name}, you are {age} years old.")

if __name__ == "__main__":
    welcome_message()
    check_python_version()
    user_info()