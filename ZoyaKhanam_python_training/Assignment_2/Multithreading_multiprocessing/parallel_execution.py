"""
Question 1
Create two threads that print numbers from 1 to 5 simultaneously.
"""

import threading
import multiprocessing
import time
from concurrent.futures import ThreadPoolExecutor
from concurrent.futures import ProcessPoolExecutor


# Question 1
def print_numbers() -> None:
    """
    Print numbers from 1 to 5.
    """
    for i in range(1, 6):
        print(f"{threading.current_thread().name}: {i}")


"""
Question 2
Create a thread that calculates the sum of numbers from 1 to 100.
"""


def calculate_sum() -> None:
    """
    Calculate sum from 1 to 100.
    """
    total = sum(range(1, 101))
    print(f"Sum: {total}")


"""
Question 3
Demonstrate join() method.
"""


def task() -> None:
    """
    Simple task.
    """
    print("Thread started")
    time.sleep(2)
    print("Thread completed")


"""
Question 4
Simulate file downloading.
"""


def download_file(file_name: str) -> None:
    """
    Simulate file download.
    """
    print(f"Downloading {file_name}")
    time.sleep(2)
    print(f"{file_name} downloaded")


"""
Question 5
Create two processes and print their Process IDs.
"""


def show_process_id() -> None:
    """
    Print process id.
    """
    print(f"Process ID: {multiprocessing.current_process().pid}")


"""
Question 6
Calculate squares using Process class.
"""


def calculate_square(number: int) -> None:
    """
    Calculate square.
    """
    print(f"Square of {number}: {number ** 2}")


"""
Question 7 & 8
ThreadPoolExecutor and ProcessPoolExecutor
"""


def cube(number: int) -> int:
    """
    Calculate cube.
    """
    return number ** 3


if __name__ == "__main__":

    # Question 1
    thread1 = threading.Thread(target=print_numbers)
    thread2 = threading.Thread(target=print_numbers)

    thread1.start()
    thread2.start()

    thread1.join()
    thread2.join()

    print("-" * 30)

    # Question 2
    thread = threading.Thread(target=calculate_sum)

    thread.start()
    thread.join()

    print("-" * 30)

    # Question 3
    thread = threading.Thread(target=task)

    thread.start()
    thread.join()

    print("Main program continues")

    print("-" * 30)

    # Question 4
    thread1 = threading.Thread(
        target=download_file,
        args=("File1",)
    )

    thread2 = threading.Thread(
        target=download_file,
        args=("File2",)
    )

    thread3 = threading.Thread(
        target=download_file,
        args=("File3",)
    )

    thread1.start()
    thread2.start()
    thread3.start()

    thread1.join()
    thread2.join()
    thread3.join()

    print("-" * 30)

    # Question 5
    process1 = multiprocessing.Process(
        target=show_process_id
    )

    process2 = multiprocessing.Process(
        target=show_process_id
    )

    process1.start()
    process2.start()

    process1.join()
    process2.join()

    print("-" * 30)

    # Question 6
    process1 = multiprocessing.Process(
        target=calculate_square,
        args=(5,)
    )

    process2 = multiprocessing.Process(
        target=calculate_square,
        args=(10,)
    )

    process1.start()
    process2.start()

    process1.join()
    process2.join()

    print("-" * 30)

    # Question 7
    with ThreadPoolExecutor() as executor:
        results = executor.map(
            cube,
            [1, 2, 3, 4, 5]
        )

    print("ThreadPoolExecutor Results:")

    for result in results:
        print(result)

    print("-" * 30)

    # Question 8
    with ProcessPoolExecutor() as executor:
        results = executor.map(
            cube,
            [1, 2, 3, 4, 5]
        )

    print("ProcessPoolExecutor Results:")

    for result in results:
        print(result)