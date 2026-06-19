import pandas as pd

data = {
    "Name": ["Rahul", "Priya", "Amit", "Anuj"],
    "Age": [25, 30, 28, 35],
    "Department": ["HR", "IT", "Finance", "IT"],
    "Salary": [30000, 50000, 45000, 60000]
}

df = pd.DataFrame(data)

print("DataFrame:")
print(df)

# First 2 rows
print("\nFirst 2 Rows:")
print(df.head(2))

# Summary Statistics
print("\nSummary Statistics:")
print(df.describe())

# Only IT Employees
print("\nIT Employees:")
print(df[df["Department"] == "IT"])

# Add Bonus Column
df["Bonus"] = df["Salary"] * 0.10

print("\nDataFrame with Bonus:")
print(df)