from bs4 import BeautifulSoup
import requests
import os

import threading
from multiprocessing import Process

savePath = r'/content'


def getMovieCodeByYear(year):
    movieCode = []

    url = f'https://movie.naver.com/movie/sdb/browsing/bmovie.naver?open={year}&page=10000'

    response = requests.get(url)

    if response.status_code == 200:
        html = response.text
        soup = BeautifulSoup(html, 'lxml')

        # html에서 div 태그 중 class이름이 pagenavigation 태그를 찾아라
        pagenavigation = soup.find('div', 'pagenavigation')
        # pagenavigation내 <a> 태그 중 마지막 태그의 text 값
        lastPage = pagenavigation.find_all('a')[-1].text

        for i in range(1, int(lastPage) + 1):
            url = f'https://movie.naver.com/movie/sdb/browsing/bmovie.naver?open={year}&page={i}'
            response2 = requests.get(url)

            if response2.status_code == 200:
                html = response2.text
                soup = BeautifulSoup(html, 'lxml')

                # html에서 ul 태그 중 class이름이 directory_list 태그를 찾아라
                directory_list = soup.find('ul', 'directory_list')
                allA = directory_list.findAll('a')
                for a in allA:
                    if '?code=' in str(a):
                        movieCode.append(str(a).split('?code=')[1].split('"')[0])
            else:
                print(response2.status_code)
    else:
        print(response.status_code)

    return year, movieCode


def getMovieInfo(year_codes):
    year = year_codes[0]
    codes = year_codes[1]

    delim = '\t'  # 각 요소 별 구분자, 콤마(,)는 제목,장르 등에 쓰이기 때문에 탭(\t)으로 구분.
    f = open(os.path.join(f'{year}.csv'), 'w', encoding='UTF-8')
    f.write('year' + delim)
    f.write('code' + delim)
    f.write('title' + delim)
    f.write('genre' + delim)
    f.write('directors' + delim)
    f.write('actors' + delim)
    f.write('story' + delim + '\n')

    # t = threading.Thread(target=getImage, args=(os.path.join(savePath, str(year)), codes))
    # t.start()

    for i, code in enumerate(codes):
        # print(f'{i}/{len(codes)}', end='\r')
        url = f'https://movie.naver.com/movie/bi/mi/point.naver?code={code}'
        response = requests.get(url)

        if response.status_code == 200:
            html = response.text
            soup = BeautifulSoup(html, 'lxml')

            title = soup.find('head').find('title').text.replace(' : 네이버 영화', '')

            info_spec = soup.find('dl', 'info_spec')

            f.write(str(year) + delim)  # year
            f.write(code + delim)  # code
            f.write(title + delim)  # title

            if info_spec is not None:
                info_spec = getInfoSpec(info_spec)
                f.write(info_spec['장르'] + delim)  # genre

                f.write(info_spec['감독'] + delim)  # directors
                f.write(info_spec['출연'] + delim)  # actors

            story = getStory(f'https://movie.naver.com/movie/bi/mi/basic.naver?code={code}')
            if story:
                f.write(story)

            f.write('\n')

        else:
            print(response.status_code)

    f.close()


def getStory(url):
    response = requests.get(url)

    if response.status_code == 200:
        html = response.text
        soup = BeautifulSoup(html, 'lxml')
        story = soup.find('p', 'con_tx')
        if story is not None:
            story = str(story).replace('<p class="con_tx">', '').replace('</p>', '').replace('\n', '').replace('\t',
                                                                                                               '').replace(
                '<br/>', '')
            # <br/>뒤에 있는 공백은 ' '이 아니라 '\xa0'이다.
            # 공백이 html에서 &nbsp;로 표현되서 그렇나 봄?
            story = story.replace('&lt;', '<').replace('&gt;', '>').replace('\r', '').replace('\xa0', '')

            return story
        return False
    else:
        print(response.status_code)


# def getImage(path, codes):
#     if not os.path.isdir(os.path.join(path)):
#         os.makedirs(path)

#     for code in codes:
#         url = f'https://movie.naver.com/movie/bi/mi/photoViewPopup.naver?movieCode={code}'
#         response = requests.get(url)

#         if response.status_code == 200:
#             html = response.text
#             soup = BeautifulSoup(html, 'lxml')
#             imgUrl = soup.find('img', id='targetImage')
#             if imgUrl is not None:
#                 imgUrl = imgUrl.attrs['src']
#                 with open(os.path.join(path, code+'.png'), 'wb') as poster:
#                     poster.write(requests.get(imgUrl).content)
#         else:
#             print(response.status_code)

def getInfoSpec(html):
    dtList = html.find_all('dt')
    ddList = html.find_all('dd')

    # 딕셔너리로 가져올 정보를 미리 정해 놓는다.
    info = {'장르': '',
            '감독': '',
            '출연': ''}

    # dt, dd의 개수만큽
    for dt, dd in zip(dtList, ddList):
        if '개요' in dt.text:
            for span in dd.find_all('span'):
                for a in span.find_all('a'):
                    if 'genre' in str(a):
                        if info['장르'] != '':
                            info['장르'] += ', ' + a.text
                        else:
                            info['장르'] = a.text

        elif '감독' in dt.text:
            for a in dd.find_all('a'):
                # 감독이 여러명인 경우가 있어서 - 루소 형제 등?
                if info['감독'] != '':
                    info['감독'] += ', ' + a.text
                else:
                    info['감독'] = a.text
        elif '출연' in dt.text:
            for a in dd.find_all('a'):
                # 메인 출연진이 아니라면 pass
                if '더보기' in a.text:
                    continue
                if info['출연'] != '':
                    info['출연'] += ', ' + a.text
                else:
                    info['출연'] = a.text

    return info


# def crawling(s, e):
#     for i in range(s, e+1):
#         getMovieInfo(getMovieCodeByYear(i))

def crawling(i):
    getMovieInfo(getMovieCodeByYear(i))


if __name__ == "__main__":
    # p1 = Process(target=crawling, args=(2000, 2012))
    # p2 = Process(target=crawling, args=(2013, 2022))

    # p1.start()
    # p2.start()

    # p1.join()
    # p2.join()
    errorYear=[2015,2018,2019,2020,2021,2022]
    for year in errorYear:
        crawling(year)
        print(f"{year}크롤링 완료")
