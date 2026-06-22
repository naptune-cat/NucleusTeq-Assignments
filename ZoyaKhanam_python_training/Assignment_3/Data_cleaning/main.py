import pandas as pd
import numpy as np

# Create dataset with missing values
data = {
    "Name": ["Rahul", "Priya", "Anuj"],
    "Age": [25, np.nan, 29],
    "Salary": [30000, 40000, np.nan]
}

df = pd.DataFrame(data)

print("Original Data:")
print(df)

# Check for missing values
print("\nMissing Values:")
print(df.isnull())

# Replace missing age with average age
mean_age = df["Age"].mean()
df["Age"] = df["Age"].fillna(mean_age)

# Replace missing salary with 0
df["Salary"] = df["Salary"].fillna(0)

print("\nCleaned Data:")
print(df)