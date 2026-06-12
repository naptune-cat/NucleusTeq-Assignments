"""
Questions 35-39
File Handling
"""

# Question 35
def write_name_to_file():
    """
    Create a file and write a name into it.
    """
    print("-"*30)
    print("Create a file and write a name into it.")

    # "w" mode creates the file if it does not exist.
    # If the file already exists, its contents are overwritten.
    with open("Assignment_1/File-Handling/student.txt", "w") as file:
        file.write("Zoya Khanam")

    print("Name written successfully.")


# Question 36
def count_file_statistics():
    """
    Read a file and count words, lines and characters.
    """
    print("-"*30)
    print("Read a file and count words, lines and characters.")

    # r is read-only mode
    with open("Assignment_1/File-Handling/student.txt", "r") as file:
        content = file.read()

    # split() separates the text into words spliting using whitespace.
    # we can also split using special chars for that we just give split(".") , split(",") etc for any char
    word_count = len(content.split())

    # len() gives the total number of characters.
    character_count = len(content)

    # readlines() returns all lines as a list.
    with open("Assignment_1/File-Handling/student.txt", "r") as file:
        line_count = len(file.readlines())

    print(f"Words: {word_count}")
    print(f"Lines: {line_count}")
    print(f"Characters: {character_count}")


# Question 37
def append_data_to_file():
    """
    Append data to an existing file.
    """
    print("-"*30)
    print("Append data to an existing file.")

    # "a" mode appends data to the end of the file
    # without removing existing content.
    with open("Assignment_1/File-Handling/student.txt", "a") as file:
        file.write("\nPython Training")

    print("Data appended successfully.")


# Question 38
def copy_file_content():
    """
    Copy content from one file to another.
    """
    print("-"*30)
    print("Copy content from one file to another.")

    # Read content from the source file.
    with open("Assignment_1/File-Handling/student.txt", "r") as source_file:
        content = source_file.read()

    # Write the content into a new file.
    with open("Assignment_1/File-Handling/copied_student.txt", "w") as destination_file:
        destination_file.write(content)

    print("File copied successfully.")


# Question 39
def search_word_in_file():
    """
    Search a word in a file.
    """
    print("-"*30)
    print("Search a word in a file.")

    search_word = input("Enter word to search: ")
    # making sure the word is found irrespective of the case that's why converting to lower case
    search_word = search_word.lower()

    with open("Assignment_1/File-Handling/student.txt", "r") as file:
        content = file.read().lower()


    # Check whether the entered word exists in the file.
    if search_word in content:
        print(f"'{search_word}' found in file.")
    else:
        print(f"'{search_word}' not found in file.")

if __name__ == "__main__":
    write_name_to_file()
    count_file_statistics()
    append_data_to_file()
    copy_file_content()
    search_word_in_file() 