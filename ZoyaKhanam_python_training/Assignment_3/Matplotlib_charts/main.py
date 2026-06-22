import matplotlib.pyplot as plt

departments = ["HR", "IT", "Finance"]
employees = [5, 12, 7]

# Bar Chart
plt.figure(figsize=(5, 4))
plt.bar(departments, employees)
plt.title("Employees by Department")
plt.xlabel("Department")
plt.ylabel("Employees")
plt.show()

# Line Chart
plt.figure(figsize=(5, 4))
plt.plot(departments, employees, marker="o")
plt.title("Employees by Department")
plt.xlabel("Department")
plt.ylabel("Employees")
plt.show()

# Histogram
salaries = [30000, 40000, 50000, 60000, 45000]

plt.figure(figsize=(5, 4))
plt.hist(salaries, bins=5)
plt.title("Salary Distribution")
plt.xlabel("Salary")
plt.ylabel("Frequency")
plt.show()

# Scatter Plot
age = [25, 30, 28, 35]
salary = [30000, 50000, 45000, 60000]

plt.figure(figsize=(5, 4))
plt.scatter(age, salary)
plt.title("Age vs Salary")
plt.xlabel("Age")
plt.ylabel("Salary")
plt.show()