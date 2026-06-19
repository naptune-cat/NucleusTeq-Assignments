"""
Iterators and Generators
"""


def list_iterator() -> None:
    """
    Create an iterator for a list and print elements using next().
    """
    print("-" * 30)
    print("List Iterator")

    numbers = [10, 20, 30, 40]

    iterator = iter(numbers)

    print(next(iterator))
    print(next(iterator))
    print(next(iterator))
    print(next(iterator))


class NumberIterator:
    """
    Custom iterator that returns numbers from 1 to N.
    """

    def __init__(self, limit: int):
        self.limit = limit
        self.current = 1

    def __iter__(self):
        return self

    def __next__(self):
        if self.current <= self.limit:
            value = self.current
            self.current += 1
            return value

        raise StopIteration


def custom_iterator_demo() -> None:
    """
    Demonstrate custom iterator.
    """
    print("-" * 30)
    print("Custom Iterator")

    for number in NumberIterator(5):
        print(number)


def square_generator(limit: int):
    """
    Generate square numbers up to N.
    """
    for number in range(1, limit + 1):
        yield number ** 2


def square_generator_demo() -> None:
    """
    Demonstrate square generator.
    """
    print("-" * 30)
    print("Square Generator")

    for value in square_generator(5):
        print(value)


def fibonacci_generator(limit: int):
    """
    Generate Fibonacci numbers.
    """
    first = 0
    second = 1

    for _ in range(limit):
        yield first
        first, second = second, first + second


def fibonacci_generator_demo() -> None:
    """
    Demonstrate Fibonacci generator.
    """
    print("-" * 30)
    print("Fibonacci Generator")

    for value in fibonacci_generator(10):
        print(value)


def even_generator_expression() -> None:
    """
    Generate even numbers from 1 to 50 using generator expression.
    """
    print("-" * 30)
    print("Generator Expression")

    even_numbers = (
        number
        for number in range(1, 51)
        if number % 2 == 0
    )

    for number in even_numbers:
        print(number)


def iterator_vs_generator() -> None:
    """
    Explain iterator and generator with example.
    """
    print("-" * 30)
    print("Iterator vs Generator")

    numbers = [1, 2, 3]

    iterator = iter(numbers)

    print("Iterator:")
    print(next(iterator))
    print(next(iterator))

    def generate():
        yield 1
        yield 2
        yield 3

    generator = generate()

    print("Generator:")
    print(next(generator))
    print(next(generator))


def large_dataset_generator():
    """
    Generate large dataset without storing all values.
    """
    for number in range(1, 1000001):
        yield number


def process_large_dataset() -> None:
    """
    Process large dataset using generator.
    """
    print("-" * 30)
    print("Large Dataset Processing")

    count = 0

    for value in large_dataset_generator():
        print(value)

        count += 1

        if count == 5:
            break


def built_in_generator_demo() -> None:
    """
    Demonstrate a built-in generator expression.
    """
    print("-" * 30)
    print("Built-in Generator")

    generator = (
        number
        for number in range(1, 6)
    )

    for value in generator:
        print(value)

if __name__ == "__main__":
    list_iterator()
    custom_iterator_demo()
    square_generator_demo()
    fibonacci_generator_demo()
    even_generator_expression()
    iterator_vs_generator()
    process_large_dataset()
    built_in_generator_demo()