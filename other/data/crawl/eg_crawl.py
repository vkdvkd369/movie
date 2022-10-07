import requests
from bs4 import BeautifulSoup as Bs
import pandas as pd
import webbrowser

# naver 영화 줄거리 페이지
# 현재 상영중 영화 : https://movie.naver.com/movie/running/current.naver
# .thumb a

mainReq = requests.get("https://movie.naver.com/movie/running/current.naver")

mainBs = Bs(mainReq.content, 'html.parser')
currentMovie = mainBs.select('.thumb > a')
currentMovieLink = []
for a in currentMovie:
    currentMovieLink.append("https://movie.naver.com"+a['href'])

entire = {}
for url in currentMovieLink:
    req = requests.get(url)
    bs = Bs(req.content, 'html.parser')


    movieName = bs.select_one('.h_movie > a').text
    directorName = bs.select_one('.step2 + dd > p > a').text
    performerNames = bs.select('.step3 + dd > p > a')
    names = []
    for a in performerNames:
        names.append(a.text)
    storyStr = bs.select_one('.con_tx').text
    allInfo = {'movieName': movieName, 'directorName': directorName, 'performerNames': names, 'storyStr': storyStr}
    entire[movieName] = allInfo

html = pd.DataFrame(entire).to_html()

with open("resultHtml.html", 'w', encoding="utf-8") as w:
    w.write(html)
webbrowser.open("resultHtml.html")