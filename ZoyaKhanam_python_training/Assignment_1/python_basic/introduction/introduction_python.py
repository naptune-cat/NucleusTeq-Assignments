"""
Questions 1-3 
Introduction to Python
"""

import sys

#1. Write a program to print 'Welcome to Python Training'.

# def function_name() -> return_type:

def welcome_message()-> None:
    print("Welcome to Python Training")

#2. Write a program to check your Python version.

def check_python_version()-> None:
    print("Python version:", sys.version)

#3. Take user input (name and age) and print a formatted message.
def user_info()-> None:
    name = input("Enter your name: ")
    age = input("Enter your age: ")
    print(f"Hello {name}, you are {age} years old.")

if __name__ == "__main__":
    welcome_message()
    check_python_version()
    user_info()