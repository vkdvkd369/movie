import pymysql as sql
import pandas as pd
import csv


with open("../data/people_position.csv", "r", encoding="utf-8") as r:
    iterator = csv.reader(r)
    relist = []
    for line in iterator:
        relist.append(line[0])

with open('../data/people_position_relist.csv', 'w', encoding='utf-8', newline='') as w:
    writer = csv.writer(w)
    for line in relist:
        writer.writerow(str(line).split(',', 2))

df = pd.read_csv('../data/people_position_relist.csv')
df.drop('num', axis=1, inplace=True)

allPosition = df['position'].to_list()
allPeople = df['name'].to_list()

conn = sql.connect(host="asiente-db.czm25bubs03r.ap-northeast-2.rds.amazonaws.com", user="asiente",
                   password="narangd0", charset="utf8", db="movie")
cursor = conn.cursor()

allQuery =[]
for pos in set(allPosition):
    query1 = "insert into positions (positionName) values (\"" + pos +"\");"
    allQuery.append(query1)

for person, position in zip(allPeople, allPosition):
    query2 = "insert into people (personName, personPosition) values(\"" + str(person) + "\", \"" + str(position) + \
             "\");"
    allQuery.append(query2)

# ssstr = '\n'.join(allQuery)
# with open("str.txt", "w", encoding="utf-8") as ww:
#     ww.write(ssstr)
# cursor.execute(ssstr)
n=50
for quer, i in zip(allQuery, range(len(allQuery))):
    cursor.execute(quer)
    if i % 2000 == 0:
        print(str(i)+"번째 record 처리됨")
conn.commit()
conn.close()