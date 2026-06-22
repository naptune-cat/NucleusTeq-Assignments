import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt

data = {
    "Name": ["Rahul", "Priya", "Amit", "Anuj"],
    "Age": [25, 30, 28, 35],
    "Department": ["HR", "IT", "Finance", "IT"],
    "Salary": [30000, 50000, 45000, 60000]
}

df = pd.DataFrame(data)

# Barplot
plt.figure(figsize=(5, 4))
sns.barplot(data=df, x="Department", y="Salary")
plt.show()

# Boxplot
plt.figure(figsize=(5, 4))
sns.boxplot(data=df, y="Salary")
plt.show()

# Heatmap
correlation = df[["Age", "Salary"]].corr()

plt.figure(figsize=(5, 4))
sns.heatmap(correlation, annot=True)
plt.show()