from main import add_numbers


def test_positive_numbers():
    assert add_numbers(10, 20) == 30


def test_negative_numbers():
    assert add_numbers(-5, -3) == -8


def test_mixed_numbers():
    assert add_numbers(-5, 10) == 5


def test_zero():
    assert add_numbers(0, 0) == 0