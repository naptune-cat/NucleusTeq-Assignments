"""
Exception Handling Questions
"""

# Question 1
def handle_value_error() -> None:
    """
    Take input and handle ValueError.
    """
    print("-" * 30)
    print("Value Error Handling")

    try:
        number = int(input("Enter an integer: "))
        print(f"You entered: {number}")

    except ValueError:
        print("Invalid input. Please enter a valid integer.")


# Question 2
def handle_zero_division() -> None:
    """
    Divide two numbers and handle ZeroDivisionError.
    """
    print("-" * 30)
    print("Division Program")

    try:
        numerator = float(input("Enter numerator: "))
        denominator = float(input("Enter denominator: "))

        result = numerator / denominator

        print(f"Result: {result}")
    except ValueError:
        print("Invalid input. Required float")
    except ZeroDivisionError:
        print("Cannot divide by zero.")


# Question 3
def read_number_from_file() -> None:
    """
    Read a number from a file and print its square.
    """
    print("-" * 30)
    print("File Reading Example")

    try:
        with open("number.txt", "r", encoding="utf-8") as file:
            number = int(file.read())

    except FileNotFoundError:
        print("File not found.")

    except ValueError:
        print("File does not contain a valid integer.")

    else:
        print(f"Square: {number ** 2}")

    finally:
        print("File operation completed.")


# Question 4
def handle_multiple_exceptions() -> None:
    """
    Handle multiple exceptions.
    """
    print("-" * 30)
    print("Multiple Exception Handling")

    try:
        number = int(input("Enter a number: "))
        result = 100 / number

        print(f"Result: {result}")

    except ValueError:
        print("Invalid integer entered.")

    except ZeroDivisionError:
        print("Division by zero is not allowed.")


# Question 5
def catch_all_exceptions() -> None:
    """
    Catch all exceptions and print error message.
    """
    print("-" * 30)
    print("Catch All Exceptions")

    try:
        number = int(input("Enter a number: "))
        result = 100 / number

        print(result)

    except Exception as error:
        print(f"Error: {error}")


# Question 6
def validate_positive_number(number: int) -> None:
    """
    Raise ValueError if number is negative.
    """
    
    if number < 0:
        raise ValueError(
            "Negative numbers are not allowed."
        )

    print("Valid number.")


# Question 7
class AgeException(Exception):
    """
    Custom exception for age validation.
    """


def validate_age(age: int) -> None:
    """
    Validate age.
    """
    if age < 18:
        raise AgeException(
            "Age must be at least 18."
        )

    print("Age is valid.")


# Question 8
def handle_file_not_found() -> None:
    """
    Handle FileNotFoundError.
    """
    print("-" * 30)
    print("File Not Found Example")

    try:
        with open(
            "sample.txt",
            "r",
            encoding="utf-8"
        ) as file:
            print(file.read())

    except FileNotFoundError:
        print("File not found.")


if __name__ == "__main__":
# Q.1
    handle_value_error()
# Q.2
    handle_zero_division()
# Q.3
    read_number_from_file()
# Q.4
    handle_multiple_exceptions()
# Q.5
    catch_all_exceptions()

# Q.6
    number = int(input("Enter a number: "))
    try:
        
        validate_positive_number(number)

    except ValueError as error:
        print(error)
#  Q.7
    age= input(int("Enter your age "))
    try:
        validate_age(age)
    except ValueError:
        print("Please enter a valid age")
    except AgeException as error:
        print(error)
# Q.8
    handle_file_not_found()

