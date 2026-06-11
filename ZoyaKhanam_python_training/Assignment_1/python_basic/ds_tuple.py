"""
Question 28-29
Data Structures - Tuple
"""

# Question 28
def tuple_operations():
    """
    Create a tuple and access its elements.
    """

    fruits = ("Apple", "Banana", "Mango", "Orange")

    print("First Element:", fruits[0])
    print("Second Element:", fruits[1])
    print("Last Element:", fruits[-1])


# Question 29
def modify_tuple():
    """
    Convert a tuple into a list and modify it.
    """

    fruits = ("Apple", "Banana", "Mango")

    fruit_list = list(fruits)

    fruit_list.append("Orange")

    print("Modified List:", fruit_list)

if __name__ == "__main__":
    tuple_operations()
    modify_tuple()