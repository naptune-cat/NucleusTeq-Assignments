"""
Using package modules.
"""

from module1 import show_message as message1
from module2 import show_message as message2


def main() -> None:
    """
    Call package modules.
    """
    message1()
    message2()


main()