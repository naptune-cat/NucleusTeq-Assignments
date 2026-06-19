"""
Using utility module.
"""

from utility import greet, square


def main() -> None:
    """
    Call utility functions.
    """
    greet("Zoya")
    print(f"Square: {square(5)}")


main()