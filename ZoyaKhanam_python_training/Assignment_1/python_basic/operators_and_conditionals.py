"""
Question 7-11
Operators and Conditionals
"""

# Question 7
def check_even_odd()-> None:
    """
    Check if a number is even or odd.
    """
    number = int(input("Enter a number: "))
    if number%2==0:
        print(f"{number} is even.")
    else:
        print(f"{number} is odd.")

# Question 8
def check_positive_negative()-> None:
    """
    Check if a number is positive, negative, or zero.
    """
    number = float(input("Enter a number:"))
    if number > 0:
        print(f"{number} is positive.")
    elif number < 0:
        print(f"{number} is negative.")
    else:
        print("The number is zero.")

# Question 9
def find_largest()-> None:
    """
    Find the largest of three numbers.
    """
    num1 = float(input("Enter first number: "))
    num2 = float(input("Enter second number: "))
    num3 = float(input("Enter third number: "))

    largest = max(num1, num2, num3)
    print(f"The largest number is: {largest}")

# Question 10
def calculate_grade()-> None:
    """
    Calculate grade based on marks.(A/B/C/Fail)
    """
    marks = float(input("Enter your marks: "))
    if marks >= 80:
        print("Grade: A")
    elif marks >= 70:
        print("Grade: B")
    elif marks >= 60:
        print("Grade: C")
    else:
        print("Grade: Fail")

# Question 11
def check_leap_year()-> None:
    """
    Check if a year is a leap year.
    """
    year = int(input("Enter a year: "))
    if (year % 4 == 0 and year % 100 != 0) or (year % 400 == 0):
        print(f"{year} is a leap year.")
    else:
        print(f"{year} is not a leap year.")

if __name__ == "__main__":
    check_even_odd()
    check_positive_negative()
    find_largest()
    calculate_grade()
    check_leap_year()