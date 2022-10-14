import requests
import pandas as pd


url = 'http://localhost:8081/sentiment/predict'

seperated_review = pd.read_csv("../../data/labeled/split3_label.csv", sep=",")
df = seperated_review.iloc[:200]
texts = df["document"].tolist()
label = df["label"].tolist()

data = {"review_texts": texts}
# send post request including data
response = requests.post(url, json=data)
print(response.json())


