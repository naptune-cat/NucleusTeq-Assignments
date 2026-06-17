"""
Question 41-45
Testing and Debugging
"""


def add_numbers(a: int, b: int) -> int:
    """
    Add two numbers.
    """
    return a + b



def is_prime(number: int) -> bool:
    """
    Check whether a number is prime.
    """
    if number < 2:
        return False

    for i in range(2, number):
        if number % i == 0:
            return False

    return True



import pdb


def calculate_average(numbers: list) -> float:
    """
    Calculate average of numbers.
    """
    total = 0

    for num in numbers:
        total += num

    pdb.set_trace()

    average = total // len(numbers)  # this will give Logical bug

    return average



def display_numbers() -> None:
    """
    Display numbers from 1 to 5.
    """
    for i in range(1, 6):
        pdb.set_trace()
        print(i)

#Output in debugger for display_numbers()

"""
> display_numbers()
(Pdb) i
1

(Pdb) n
1

(Pdb) i
2

(Pdb) n
2

(Pdb) i
3

...

The value of variable 'i' can be inspected at each iteration
using pdb commands.
"""



# theory question
"""
Advantages of IDE Debugger over Print Statements

1. Allows step-by-step execution of code.
2. Supports breakpoints at specific lines.
3. Helps inspect variable values instantly.
4. Makes finding logical errors easier.
5. No need to add and remove print statements.
6. Provides better visualization of program flow.
"""


# print(calculate_average([10, 20, 30]))
# display_numbers()