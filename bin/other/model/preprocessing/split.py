import pandas as pd
import csv

csvs = []
with open("../outputs/comment3.csv", 'r', encoding="utf-8") as r:
    temp = csv.reader(r)
    for p in temp:
        csvs.append(p)

l1 = csvs[0:2000]
l2 = csvs[2000:4000]
l3 = csvs[4000:6000]
l4 = csvs[6000:8000]
l5 = csvs[8000:]



for l,i in zip([l1, l2, l3, l4, l5], range(5)):
    with open("../outputs/split"+str(i)+".csv", 'w', encoding='utf-8', newline='') as f:
        writer = csv.writer(f)
        writer.writerows(l)

