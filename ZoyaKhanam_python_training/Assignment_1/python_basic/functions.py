"""
Questions 17-20
Python Basics - Functions
"""

#Question 17
def calculate_square(number: float) -> float:
    """
    Calculate the square of a number.
    """
    return number**2


#Question 18
def check_palindrome() -> None:
    """
    Check whether a number or string is a palindrome.

    A palindrome reads the same forwards and backwards.
    Examples:
    - madam
    - 121
    """
    user_input = input("Enter the input to check palindrome")

    # [::-1] creates reverse copy of the provided input 
    reversed_value = user_input[::-1]

    if user_input == reversed_value:
        print(f"{user_input} is a palindrome {type(user_input)}.")
    else:
        print(f"{user_input} is a palindrome.")


# Question 19
def find_maximum_number():
    """
    Return the maximum number from a list.
    """

    numbers = [12, 45, 7, 89, 34, 56]

    maximum_number = max(numbers)

    print(f"List: {numbers}")
    print(f"Maximum number: {maximum_number}")

# Question 20

def greet(name="Guest") -> None:
    """
    Demonstrating the use of default parameters.
    """
    print(f"Hello {name}! \nWelcome to foodie cart.")


if __name__ == "__main__":
    calculate_square(5.0)
    check_palindrome()
    find_maximum_number()
    # this will use default parameter as no arguments are passed
    greet()
    # here we are passing "Shanaya" as argument
    greet("Shanaya")

