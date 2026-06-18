"""
Using math operations package.
"""

from addition import add
from subtraction import subtract
from multiplication import multiply
from division import divide


def main() -> None:
    """
    Perform mathematical operations.
    """
    print(f"Addition: {add(10, 5)}")
    print(f"Subtraction: {subtract(10, 5)}")
    print(f"Multiplication: {multiply(10, 5)}")
    print(f"Division: {divide(10, 5)}")


main()