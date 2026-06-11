"""
Questions 32-34
Data Structures - Dictionary
"""

# Question 32
def student_dictionary():
    """
    Create a student dictionary and access values.
    """
    print("------------------------------------------")
    print("Create a student dictionary and access values.")
    student = {
        "name": "Priyanka",
        "age": 23,
        "course": "Computer Science"
    }

    print("Name:", student["name"])
    print("Age:", student["age"])
    print("Course:", student["course"])


# Question 33
def character_frequency():
    """
    Count frequency of characters in a string.
    """
    print("------------------------------------------")
    print("Count frequency of characters in a string.")
    text = input("Enter a string: ")

    frequency = {}

    for character in text:
        # If the character is already existing in frequency-
        # we increment its count; otherwise initialize it.
        if character in frequency:
            frequency[character] += 1
        else:
            frequency[character] = 1

    print(f"Character Frequency: {frequency}")


# Question 34
def merge_dictionaries():
    """
    Merge two dictionaries.
    """
    print("------------------------------------------")
    print("Merge two dictionaries.")
    student = {
        "name": "Shirley",
        "age": 23
    }

    course = {
        "course": "Computer Science",
        "city": "Ujjain"
    }

    # The assignment requires merging two dictionaries.
    # I am adding a third dictionary is to demonstrate that
    # dictionary unpacking can merge multiple dictionaries.
    
    location ={
        "city": "Coonoor",
        "address": "308 B, New Sapient Heights"
    }

    #  **dictionary_name is known as dictionary unpacking
    #  dictionary unpacking allows us to merge two or more dictionaries together
    
    merged_dictionary = {**student, **course, **location}

    # if there are same keys in 2 dictionaries like "city" in course and location
    # the later one will be taken into account

    print(f"Merged Dictionary: {merged_dictionary}")

if __name__ == "__main__":
    character_frequency()
    student_dictionary()
    merge_dictionaries()