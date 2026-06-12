"""
Questions 25-27
Data Structures - List
"""


# Question 25
def list_operations():
    """
    Create a list of numbers and perform
    sum, maximum, sorting, and duplicate removal.
    """
    print("-"*30)
    print("Create a list of numbers and perform\nsum, maximum, sorting, and duplicate removal.")
    numbers = [10, 5, 20, 15, 5, 25, 30, 20, 40, 10]

    print("Original List:", numbers)
    print("Sum:", sum(numbers))
    print("Maximum Number:", max(numbers))
    print("Sorted List:", sorted(numbers))

    unique_numbers = list(set(numbers))
    print("List Without Duplicates:", unique_numbers)


# Question 26
def count_even_odd():
    """
    Count even and odd numbers in a list.
    """
    print("-"*30)
    print("Count even and odd numbers in a list.")
    numbers = [10, 5, 20, 15, 5, 25, 30, 20, 40, 10]

    even_count = 0
    odd_count = 0

    for number in numbers:
        if number % 2 == 0:
            even_count += 1
        else:
            odd_count += 1

    print("Even Numbers:", even_count)
    print("Odd Numbers:", odd_count)


# Question 27
def reverse_list():
    """
    Reverse a list without using reverse().
    """
    print("-"*30)
    print("Reverse a list without using reverse().")
    numbers = [10, 5, 20, 15, 5, 25, 30, 20, 40, 10]

    reversed_list = numbers[::-1]

    print("Original List:", numbers)
    print("Reversed List:", reversed_list)


if __name__ == "__main__":
    list_operations()
    count_even_odd()
    reverse_list()