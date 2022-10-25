import pymysql as sql
import pandas as pd
import csv
import os

allMovies =[]
movieDirs = os.listdir("./movie")
for dir in movieDirs:
    movieCsvs = os.listdir(f"./movie/{dir}")
    for file in movieCsvs:
        with open(f"./movie/{dir}/{file}", "r", encoding="utf-8") as r:
            reader = csv.reader(r, delimiter='\t')
            for record in reader:
                if len(record) != 7 or record==["year", "code", "title", "genre", "directors", "actors", "story"]:
                    continue
                allMovies.append(record)

conn = sql.connect(host="asiente-db.czm25bubs03r.ap-northeast-2.rds.amazonaws.com", user="asiente",
                   password="narangd0", charset="utf8", db="movie")
cursor = conn.cursor()

for i,record in enumerate(allMovies):
    # #movies insert
    # storyStr=record[6].replace("\'","\\'")
    # storyStr=record[6].replace('\"', '\\"')
    # query = f"INSERT INTO movies(movieTitle, movieCode, movieYear, movieStory) VALUES (\"{record[2]}\",\"{record[1]}\"" \
    #         f", \"{record[0]}\", \"{storyStr}\")"
    # cursor.execute(query)

    # moviesGenre insert
    # genreName=record[3].split(', ')
    # for gn in genreName:
    #     query2 = f"INSERT INTO movieGenre(movieId, genreId) SELECT m.movieId, g.genreId FROM movies m, genre g where " \
    #              f"m.movieTitle=\"{record[2]}\" and g.genreName=\"{gn}\""
    #     try:
    #         cursor.execute(query2)
    #     except sql.err.IntegrityError:
    #         continue

    # moviesPeople insert
    directorsName = record[4].split(', ')
    actorsName = record[5].split(',')
    peopleName = directorsName + actorsName

    for name in peopleName:
        query3 = f"INSERT INTO moviePeople(movieId, personId) SELECT m.movieId, p.personId FROM movies m, " \
                 f"people p where m.movieTitle=\"{record[2]}\" and p.personName=\"{name}\""
        try:
            cursor.execute(query3)
        except sql.err.IntegrityError:
            continue

    if i % 2000 == True:
        print(f"{i}번째 완료")
conn.commit()
conn.close()
