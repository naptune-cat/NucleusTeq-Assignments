"""
Question 30-31
Data Structures - Set
"""

# Question 30
def set_operations():
    """
    Perform union, intersection and difference on sets.
    """

    set_one = {1, 2, 3, 4, 5}
    set_two = {4, 5, 6, 7, 8}

    print("Union:", set_one.union(set_two))
    print("Intersection:", set_one.intersection(set_two))
    print("Difference:", set_one.difference(set_two))


# Question 31
def remove_duplicates():
    """
    Remove duplicates from a list using a set.
    """

    numbers = [1, 2, 2, 3, 4, 4, 5, 6, 6]

    unique_numbers = list(set(numbers))

    print("Original List:", numbers)
    print("Without Duplicates:", unique_numbers)

if __name__ == "__main__":
    set_operations()
    remove_duplicates()