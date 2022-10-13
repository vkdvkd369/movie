from flask import Flask, request, jsonify
import joblib
import numpy as np
from konlpy.tag import Okt

app = Flask(__name__)


def tw_tokenizer(text):
    twitter = Okt()
    tokens_ko = twitter.morphs(text)
    return tokens_ko


# json REST Flask API
@app.route('/sentiment/predict', methods=['POST'])
def predict():
    model = joblib.load('outputs/model.pkl')
    model = model.best_estimator_
    tfidf_model = joblib.load('outputs/tfidf_vect.pkl')
    # get HashMap<Integer,List<String>> data from request
    data = request.get_json()
    # get List<String> data from HashMap<Integer,List<String>>
    print(data)
    
    tfidf = tfidf_model.transform(review_texts)
    result = model.predict(tfidf)
    return jsonify(result.tolist())


if __name__ == '__main__':
    app.run(port=8081)

