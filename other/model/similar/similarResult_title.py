import pandas as pd
import joblib


cosine_sim = joblib.load('similar_cosine_sim.pkl')

title_to_index = joblib.load('similar_title_to_index.pkl')
index_to_title = joblib.load('similar_index_to_title.pkl')


def get_recommendations_title(title, cosine_sim=cosine_sim):
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
    # 가장 유사한 10개 영화 유사도
    movie_scores =  [idx[1] for idx in sim_scores]
    # 인덱스를 제목으로
    movie_name = []
    for i in movie_indices:
        movie_name.append(index_to_title[i])
    # 데이터프레임 형식으로
    result_df = pd.DataFrame(data = movie_name, index = range(1,11), columns = ['title'])
    result_df['score'] = movie_scores
    return result_df
print(get_recommendations_title('공조2: 인터내셔날'))
print(get_recommendations_title('범죄도시2'))
