from main import is_prime


def test_prime_number():
    assert is_prime(7) is True


def test_non_prime_number():
    assert is_prime(8) is False


def test_one():
    assert is_prime(1) is False


def test_two():
    assert is_prime(2) is True