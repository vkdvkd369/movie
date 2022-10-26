from flask import Flask, request
from konlpy.tag import Okt
from sklearn.metrics.pairwise import cosine_similarity
import joblib

# 터미널에 python sentiment_Api.py 로 실행.
# 1. 의존성 패키지가 설치되어 있는지 확인할 것. anaconda 사용 시, 의존성 패키지가 설치된 가상환경을 activate 한 뒤에 실행
# 2. joblib이 로드하는 pkl파일이 저장되어 있는 Path 확인할 것.
# 2. python 3.8, joblib 1.2.0, konlpy 0.6.0, sklearn 1.1.2, flask 2.1.3  jpype1 1.4.0


app = Flask(__name__)


def tw_tokenizer(text):
    twitter = Okt()
    tokens_ko = twitter.morphs(text)
    return tokens_ko


okt_tokenizer = tw_tokenizer


# json REST Flask API
@app.route('/sentiment/predict', methods=['POST'])
def predict():
    model = joblib.load("../outputs/grid_cv.pkl")
    model = model.best_estimator_

    tfidf_model = joblib.load("../outputs/tfidf_vect.pkl")
    # get map data from spring request
    data = request.get_json()

    result = {}
    for movieId, reviews in data.items():
        if len(reviews) > 0:
            tfidf = tfidf_model.transform(reviews)
            predict_result = model.predict(tfidf)
            positiveRatio = sum(predict_result) / len(predict_result)
            result[movieId] = positiveRatio
        else:
            result[movieId] = None

    noRepleMovie = {}
    repleMovie = {}
    for k, v in result.items():
        if v == None:
            noRepleMovie[k] = v
        else:
            repleMovie[k] = v
    sorted_reple_movie = sorted(repleMovie.items(), key=lambda x: x[1], reverse=True)

    rtn = {"noRepleMovie": noRepleMovie, "repleMovie": sorted_reple_movie}
    return rtn


@app.route('/similar/predict', methods=['POST'])
def predict_similar():
    tfidf_vect = joblib.load("../outputs/similar_tfidf_vect.pkl")
    tfidf_matrix_train = joblib.load('../outputs/similar_tfidf_matrix_train.pkl')
    title_to_index = joblib.load('../outputs/similar_title_to_index.pkl')
    index_to_title = joblib.load('../outputs/similar_index_to_title.pkl')

    # get movieStory string data from request
    data = request.get_json("movieStory")

    tfidf_matrix_story = tfidf_vect.transform([data['movieStory']])  # 길이가 길면 []해야함
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
    movie_id = []
    for i in movie_indices:
        movie_name.append(index_to_title[i])
        movie_id.append(i+1)
    send = {}
    for i, mn, ms, mi in zip(range(len(movie_name)), movie_name, movie_scores, movie_id):
        send[i] = {"title": mn, "score": ms, "id": int(mi)}

    return send


if __name__ == '__main__':
    app.run(port=8081)



