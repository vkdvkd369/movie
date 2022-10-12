import pandas as pd
import re
from konlpy.tag import Twitter
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import GridSearchCV
from sklearn.svm import LinearSVC
import joblib
import os

OVERWRITE=[0,0,0]

train_df = pd.read_csv('ratings_train.txt', sep='\t', encoding='utf-8')
test_df = pd.read_csv('ratings_test.txt', sep='\t', encoding='utf-8')

print(train_df.head(3))
train_df['label'].value_counts()

train_df = train_df.fillna('')
test_df = test_df.fillna('')

# Id컬럼 삭제
train_df.drop('id', axis=1, inplace=True)
test_df.drop('id', axis=1, inplace=True)


# 전처리
# 숫자 제거
train_df['document'] = train_df['document'].apply(lambda x: re.sub(r"\d+", " ", x))
test_df['document'] = test_df['document'].apply(lambda x: re.sub(r"\d+", " ", x))

# 중복되는 특수문자 제거 (ex: ~~ !! ... ??)
train_df['document'] = train_df['document'].apply(lambda x:re.sub(r"[\/?.,;~!^]{2,}", "", x))
test_df['document'] = test_df['document'].apply(lambda x: re.sub(r"[\/?.,;~!^]{2,}", "", x))

# 형태소 단어 토큰화
twitter = Twitter()
def tw_tokenizer(text):
    tokens_ko = twitter.morphs(text)
    return tokens_ko


if OVERWRITE[0] is False and os.path.exists('tfidf_vect.pkl'):
    tfidf_vect = joblib.load('tfidf_vect.pkl')
else:
    tfidf_vect = TfidfVectorizer(tokenizer=tw_tokenizer, ngram_range=(1, 2), min_df=3, max_df=0.9)
    tfidf_vect.fit(train_df['document'])
    joblib.dump(tfidf_vect, 'tfidf_vect.pkl')

print(sorted(tfidf_vect.vocabulary_.items()))

if OVERWRITE[1] is False and os.path.exists('tfidf_matrix_train.pkl'):
    tfidf_matrix_train = joblib.load('tfidf_matrix_train.pkl')
else:
    tfidf_matrix_train = tfidf_vect.transform(train_df['document'])
    joblib.dump(tfidf_matrix_train,'tfidf_matrix_train.pkl')


# 로지스틱 회귀
if OVERWRITE[2] is False and os.path.exists('model.pkl'):
    grid_cv = joblib.load('grid_cv.pkl')
    model = joblib.load('model.pkl')
else:
    model = LogisticRegression(random_state=777, solver='liblinear')

    params = {'C': [1, 3.5, 4.5, 5.5, 10]}
    grid_cv = GridSearchCV(model, param_grid=params, cv=3, scoring='accuracy', verbose=1)
    grid_cv.fit(tfidf_matrix_train, train_df['label'])

    joblib.dump(grid_cv, 'grid_cv.pkl')
    joblib.dump(model, 'model.pkl')
print(grid_cv.best_params_, round(grid_cv.best_score_, 4))


