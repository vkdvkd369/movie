import requests
from bs4 import BeautifulSoup as Bs
import pandas as pd
import webbrowser

year=196
maxpage=100

allLinks=[]
for i in range(maxpage):
    mainReq = requests.get(f"https://movie.naver.com/movie/sdb/browsing/bmovie.naver?open={year}&{i}")
    mainBs = Bs(mainReq.content, 'html.parser')
    linkas = mainBs.select('.directory_list > li > a')
    for a in linkas:
        allLinks.append(a['href'])