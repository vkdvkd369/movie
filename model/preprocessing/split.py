import pandas as pd
import xlrd


temp = pd.read_excel('../outputs/comment2.xlsx', engine='openpyxl')
print(temp.head(3))