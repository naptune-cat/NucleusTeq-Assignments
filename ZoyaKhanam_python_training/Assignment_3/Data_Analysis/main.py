import pandas as pd

data = {
    "Name": ["Rahul", "Priya", "Amit", "Anuj"],
    "Age": [25, 30, 28, 35],
    "Department": ["HR", "IT", "Finance", "IT"],
    "Salary": [30000, 50000, 45000, 60000]
}

df = pd.DataFrame(data)

# Average Salary by Department
print("Average Salary:")
print(df.groupby("Department")["Salary"].mean())

# Max Salary by Department
print("\nMax Salary:")
print(df.groupby("Department")["Salary"].max())

# Count Employees by Department
print("\nEmployee Count:")
print(df.groupby("Department")["Name"].count())