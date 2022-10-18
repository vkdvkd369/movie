from flask import Flask, request, jsonify
from konlpy.tag import Okt
import json
import joblib

app = Flask(__name__)


def tw_tokenizer(text):
    twitter = Okt()
    tokens_ko = twitter.morphs(text)
    return tokens_ko


# json REST Flask API
@app.route('/sentiment/predict', methods=['POST'])
def predict():

    model = joblib.load("../outputs/grid_cv.pkl")
    model = model.best_estimator_

    tfidf_model = joblib.load("../outputs/tfidf_vect.pkl")
    #get map data from spring request
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

    noRepleMovie={}
    repleMovie={}
    for k,v in result.items():
        if v==None:
            noRepleMovie[k]=v
        else:
            repleMovie[k]=v
    sorted_reple_movie = sorted(repleMovie.items(), key=lambda x: x[1], reverse=True)
   
    rtn = {"noRepleMovie":noRepleMovie, "repleMovie":sorted_reple_movie}
    return rtn



if __name__ == '__main__':
    app.run(port=8081)


