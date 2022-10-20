from konlpy.tag import Okt
import joblib
from sklearn.metrics.pairwise import cosine_similarity

def okt_tokenizer(text):
    twitter = Okt()
    tokens_ko = twitter.morphs(text)
    return tokens_ko

tfidf_vect = joblib.load("../outputs/similar_tfidf_vect.pkl")
tfidf_matrix_train = joblib.load('../outputs/similar_tfidf_matrix_train.pkl')
title_to_index = joblib.load('../outputs/similar_title_to_index.pkl')
index_to_title = joblib.load('../outputs/similar_index_to_title.pkl')

story="포켓몬 극장판 사상 초유의 레전드 배틀이 지금 시작된다!지우와 피카츄 일행은 '무엇이든지 소환할 수 있는 링'을 가진 환상의 포켓몬 후파를 만난다. 그때, 데세르시티에서는 엄청난 포켓몬을 봉인한 '굴레의 항아리'에 사악한 기운이 흐르고...!'굴레의 항아리'를 열고 분노의 힘으로 되살아난 '검은 그림자'! 그 그림자의 정체는 바로 '후파'의 본래 모습, 초후파였다! 초후파는 힘을 되찾기 위해 포켓몬계의 최강 파워를 자랑하는 전설의 포켓몬들을 소환하기 시작하고! 지우와 후파 일행은 위기에 빠진 마을을 지키키 위해 이를 대적할 가공할만한 파워의 다른 전설의 포켓몬을 소환해내는데...! 후파 VS 초후파! 격돌하는 전설 VS 전설! 지우와 친구들은 과연 후파와 함께 데세르시티를 구할 수 있을까?!"
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

send={}
for i,mn, ms in zip(range(len(movie_name)),movie_name, movie_scores):
    send[i]= {"movie name" : mn, "score" : ms}

print(send)