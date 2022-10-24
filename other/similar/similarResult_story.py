
from konlpy.tag import Okt
from sklearn.metrics.pairwise import cosine_similarity
import pandas as pd
import joblib

def okt_tokenizer(text):
    okt = Okt()
    tokens_ko = okt.morphs(text)
    return tokens_ko


tfidf_vect = joblib.load("../model/outputs/similar_tfidf_vect.pkl")
tfidf_matrix_train = joblib.load('../model/outputs/similar_tfidf_matrix_train.pkl')
title_to_index = joblib.load('../model/outputs/similar_title_to_index.pkl')
index_to_title = joblib.load('../model/outputs/similar_index_to_title.pkl')


def get_recommendations_story(story):
    tfidf_matrix_story = tfidf_vect.transform([story])    # 길이가 길면 []해야함
    # data줄거리랑 영화 전체 줄거리 유사도
    cosine_sim = cosine_similarity(tfidf_matrix_story, tfidf_matrix_train)
    # 해당 영화와 모든 영화와의 유사도를 가져온다.
    sim_scores = list(enumerate(cosine_sim[0]))
    # 유사도에 따라 정렬
    sim_scores = sorted(sim_scores, key=lambda x: x[1], reverse=True)
    # 상위 10개
    sim_scores = sim_scores[1:11]
    # 영화 인덱스
    movie_indices = [idx[0] for idx in sim_scores]
    # 영화 유사도
    movie_scores = [idx[1] for idx in sim_scores]
    # 영화 인덱스 -> 타이틀
    movie_name = []
    for i in movie_indices:
      movie_name.append(index_to_title[i])
    # 데이터 프레임 형식으로
    result_df = pd.DataFrame(data = movie_name, index = range(1,11), columns=['title'] )
    result_df['score'] = movie_scores
    
    print(result_df.to_dict())
    return result_df

data = '456억 원의 상금이 걸린 의문의 서바이벌에 참가한 사람들이 최후의 승자가 되기 위해 목숨을 걸고 극한의 게임에 도전하는 이야기를 담은 넷플릭스 시리즈'
print(get_recommendations_story(str(data)))

# do = '''가리봉동 소탕작전 후 4년 뒤, 금천서 강력반은 베트남으로 도주한 용의자를 인도받아 오라는 미션을 받는다.
#
# 괴물형사 ‘마석도’(마동석)와 ‘전일만’(최귀화) 반장은 현지 용의자에게서 수상함을 느끼고, 그의 뒤에 무자비한 악행을 벌이는 ‘강해상’(손석구)이 있음을 알게 된다.
#
# ‘마석도’와 금천서 강력반은 한국과 베트남을 오가며 역대급 범죄를 저지르는 ‘강해상’을 본격적으로 쫓기 시작하는데...
#
# 나쁜 놈들 잡는 데 국경 없다!
# 통쾌하고 화끈한 범죄 소탕 작전이 다시 펼쳐진다!'''
#
# print(get_recommendations_story(str(do)))
