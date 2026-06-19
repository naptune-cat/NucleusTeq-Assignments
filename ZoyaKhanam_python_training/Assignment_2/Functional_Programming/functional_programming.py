"""
Functional Programming
"""

from functools import reduce


def lambda_square() -> None:
    """
    Find square using lambda.
    """
    print("-" * 30)
    print("Lambda Function")

    square = lambda number: number ** 2

    print(square(5))


def map_square() -> None:
    """
    Convert list into squares using map().
    """
    print("-" * 30)
    print("Map Function")

    numbers = [1, 2, 3, 4, 5]

    squares = list(
        map(
            lambda number: number ** 2,
            numbers
        )
    )

    print(squares)


def filter_even_numbers() -> None:
    """
    Extract even numbers using filter().
    """
    print("-" * 30)
    print("Filter Function")

    numbers = [1, 2, 3, 4, 5, 6]

    even_numbers = list(
        filter(
            lambda number: number % 2 == 0,
            numbers
        )
    )

    print(even_numbers)


def reduce_product() -> None:
    """
    Find product of all elements using reduce().
    """
    print("-" * 30)
    print("Reduce Function")

    numbers = [1, 2, 3, 4]

    product = reduce(
        lambda first, second:
        first * second,
        numbers
    )

    print(product)


def factorial(number: int) -> int:
    """
    Calculate factorial recursively.
    """
    if number == 0 or number == 1:
        return 1

    return number * factorial(number - 1)


def recursive_factorial_demo() -> None:
    """
    Demonstrate recursive factorial.
    """
    print("-" * 30)
    print("Recursive Factorial")
    num= int(input("Enter number for finding factorial "))

    print(factorial(num))


def fibonacci(number: int) -> int:
    """
    Calculate Fibonacci recursively.
    """
    if number <= 1:
        return number

    return (
        fibonacci(number - 1)
        + fibonacci(number - 2)
    )


def recursive_fibonacci_demo() -> None:
    """
    Demonstrate recursive Fibonacci.
    """
    print("-" * 30)
    print("Recursive Fibonacci")
    num= int(input("Enter the number of elements in fibonacci series "))

    for index in range(num):
        print(fibonacci(index))


def functional_style_demo() -> None:
    """
    Convert loop-based program into functional style.
    """
    print("-" * 30)
    print("Functional Style")

    numbers = [1, 2, 3, 4, 5, 6]

    even_numbers = list(
        filter(
            lambda number: number % 2 == 0,
            numbers
        )
    )

    print(even_numbers)

if __name__ == '__main__':
    lambda_square()
    map_square()
    filter_even_numbers()
    reduce_product()
    recursive_factorial_demo()
    recursive_fibonacci_demo()
    functional_style_demo()