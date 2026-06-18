"""
Using package modules.
"""

from question3.module1 import show_message as message1
from question3.module2 import show_message as message2


def main() -> None:
    """
    Call package modules.
    """
    message1()
    message2()


main()