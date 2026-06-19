"""
Regular Expressions
"""

import re


def extract_numbers() -> None:
    """
    Extract all numbers from a string.
    """
    print("-" * 30)
    print("Extract Numbers")

    text = "I have 2 cats, 3 dogs and 15 birds."

    numbers = re.findall(r"\d+", text)

    print(numbers)


def validate_email() -> None:
    """
    Validate an email address.
    """
    print("-" * 30)
    print("Email Validation")

    email = input("Enter email: ")

    pattern = r"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"

    if re.fullmatch(pattern, email):
        print("Valid email")
    else:
        print("Invalid email")


def validate_mobile_number() -> None:
    """
    Validate a 10-digit mobile number.
    """
    print("-" * 30)
    print("Mobile Number Validation")

    mobile = input("Enter mobile number: ")

    pattern = r"^\d{10}$"

    if re.fullmatch(pattern, mobile):
        print("Valid mobile number")
    else:
        print("Invalid mobile number")


def search_word() -> None:
    """
    Check whether a word exists in a sentence.
    """
    print("-" * 30)
    print("Search Word")

    sentence = """I recently visited Delhi, Mumbai, Bangalore and Hyderabad.\nPython is one of the most Popular Programming Languages.\nJohn, Alice, Michael and Sarah attended the meeting."""

    print(f"sentence is \n{sentence}")
    word = input("Enter word to find  ")

    result = re.search(word, sentence.lower())

    if result:
        print("Word found")
    else:
        print("Word not found")


def find_capital_words() -> None:
    """
    Extract words starting with a capital letter.
    """
    print("-" * 30)
    print("Capitalized Words")

    sentence = input("Enter your sentence to find capitalized words:\n")

    words = re.findall(r"\b[A-Z][a-zA-Z]*\b", sentence)
    if len(words)==0:
        print("No Capitalized words found.")
        return
    print(words)


def replace_multiple_spaces() -> None:
    """
    Replace multiple spaces with a single space.
    """
    print("-" * 30)
    print("Replace Multiple Spaces")

    text = input("Enter text to remove spaces:\n")

    updated_text = re.sub(r"\s+", " ", text)

    print(f"Updated text is: {updated_text}")


def check_alphabets_only() -> None:
    """
    Check if string contains only alphabets.
    """
    print("-" * 30)
    print("Alphabet Check")

    text = input("Enter text: ")

    pattern = r"^[A-Za-z]+$"

    if re.fullmatch(pattern, text):
        print("Contains only alphabets")
    else:
        print("Contains other characters")


def validate_password() -> None:
    """
    Validate password using regex.
    """
    print("-" * 30)
    print("Password Validation")

    password = input("Your password must be atleast 8 characters long\nMax 16 characters allowed\nShould have atleast one special character\n\nEnter password: ")

    pattern = (
        r"^(?=.*[0-9])"
        r"(?=.*[!@#$%^&*])"
        r".{8,16}$"
    )

    if len(password) < 8:
        print("Minimum 8 characters required.")
    elif len(password) > 16:
        print("Maximum 16 characters allowed.")
    elif not re.search(r"[0-9]", password):
        print("At least one digit is required.")
    elif not re.search(r"[!@#$%^&*]", password):
        print("At least one special character is required.")
    else:
        print("Valid password")


if __name__ == "__main__":
    extract_numbers()
    validate_email()
    validate_mobile_number()
    search_word()
    find_capital_words()
    replace_multiple_spaces()
    check_alphabets_only()
    validate_password()