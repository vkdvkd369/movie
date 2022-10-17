# https://blog.naver.com/jjs1608/222879115999 참고, 책(파이썬 머신러닝 완벽 가이드)
import pandas as pd
import re
from konlpy.tag import Okt
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import joblib
import pymysql

_database = {'host': 'asiente-db.czm25bubs03r.ap-northeast-2.rds.amazonaws.com',
            'port' : 3306,
            'user' : 'minji',
            'passwd' : '1234',
            'db' : 'movie',
            'charset' : 'utf8'}

conn = pymysql.connect(host=_database['host'],
                       port=_database['port'],
                       user=_database['user'],
                       passwd=_database['passwd'],
                       db=_database['db'],
                       charset=_database['charset'])

cur = conn.cursor()

query = "SELECT movieTitle, movieStory FROM movies"
cur.execute(query)

data = cur.fetchall()
train_df = pd.DataFrame(data)
conn.commit()
cur.close()
conn.close()
train_df.columns = ["title", "story"]




train_df = train_df.fillna(' ')
# 전처리
# 숫자 제거
train_df['story'] = train_df['story'].apply(lambda x: re.sub(r"\d+", " ", x))

# 중복되는 특수문자 제거 (ex: ~~ !! ... ??)
train_df['story'] = train_df['story'].apply(lambda x:re.sub(r"[\/?.,;~!^]{2,}", "", x))

# 처리 안하면 결과 영화 달라짐, 유사도 올라감
# train_df['story'] = train_df['story'].apply(lambda x : re.sub(r"[^ㄱ-ㅎㅏ-ㅣ가-힣0-9a-zA-Z ]"," ", x))

okt = Okt()
def okt_tokenizer(text):
    tokens_ko = okt.morphs(text)
    return tokens_ko


tfidf_vect = TfidfVectorizer(tokenizer=okt_tokenizer, ngram_range=(1, 2), min_df=3, max_df=0.9)
#tfidf_vect = TfidfVectorizer(ngram_range=(1,2))
tfidf_vect.fit(train_df['story'])


tfidf_matrix_train = tfidf_vect.transform(train_df['story'])
#print(tfidf_matrix_train.shape) # 몇개 문장에서 몇개 단어인지

cosine_sim = cosine_similarity(tfidf_matrix_train, tfidf_matrix_train)
# print(cosine_sim.shape) # 모든 문장 유사도 연산 결과 (자기 자신 포함)

title_to_index = dict(zip(train_df['title'], train_df.index))

# 영화 제목 355의 인덱스를 리턴
# idx = title_to_index['355']
# print(idx)

# 영화 제목 입력하면 가장 유사한 10개의 영화 찾아내는 함수


def get_recommendations(title, cosine_sim=cosine_sim):
    # 선택한 영화의 타이틀로부터 해당 영화의 인덱스를 받아온다.
    idx = title_to_index[title]
    # 해당 영화와 모든 영화와의 유사도를 가져온다.
    sim_scores = list(enumerate(cosine_sim[idx]))
    # 유사도에 따라 영화들을 정렬한다.
    sim_scores = sorted(sim_scores, key=lambda x: x[1], reverse=True)
    # 가장 유사한 10개의 영화를 받아온다.
    sim_scores = sim_scores[1:11]
    # 가장 유사한 10개의 영화의 인덱스를 얻는다.
    movie_indices = [idx[0] for idx in sim_scores]
    # 가장 유사한 10개의 영화의 제목을 리턴한다.
    #    return train_df['title'].iloc[movie_indices]

    # 데이터프레임 형식으로 만들었음. 유사도 출력 추가
    result_df = train_df.iloc[movie_indices].copy()
    result_df['score'] = [idx[1] for idx in sim_scores]

    del result_df['story']
    return result_df


print(get_recommendations('공조2: 인터내셔날'))