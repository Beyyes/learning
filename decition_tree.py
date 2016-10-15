#!/usr/bin/env python
#coding:utf-8
'''
Created on 2014年11月25日
@author: zhaohf
'''
import pandas as pd
import numpy as np
from sklearn import tree
from sklearn import cross_validation
import csv
df = pd.read_csv('../data/train.csv',header=0)
df = df.drop(['Ticket','Name','Cabin','Embarked'],axis=1)
m = np.ma.masked_array(df['Age'], np.isnan(df['Age']))
mean = np.mean(m).astype(int)
df['Age'] = df['Age'].map(lambda x : mean if np.isnan(x) else x)
df['Sex'] = df['Sex'].map( {'female': 1, 'male': 0} ).astype(int)
X = df.values
y = df['Survived'].values
X = np.delete(X,1,axis=1)
X_train, X_test, y_train, y_test = cross_validation.train_test_split(X,y,test_size=0.3,random_state=0)
dt = tree.DecisionTreeClassifier(max_depth=5)
dt.fit(X_train, y_train)
print dt.score(X_test,y_test)

test = pd.read_csv('../Data/test.csv',header=0)
tf = test.drop(['Ticket','Name','Cabin','Embarked'],axis=1)
m = np.ma.masked_array(tf['Age'], np.isnan(tf['Age']))
mean = np.mean(m).astype(int)
tf['Age'] = tf['Age'].map(lambda x : mean if np.isnan(x) else int(x))
tf['Sex'] = tf['Sex'].map( {'female': 1, 'male': 0} ).astype(int)
tf['Fare'] = tf['Fare'].map(lambda x : 0 if np.isnan(x) else int(x)).astype(int)
predicts = dt.predict(tf)
ids = tf['PassengerId'].values
predictions_file = open("../Submissions/dt_submission.csv", "wb")
open_file_object = csv.writer(predictions_file)
open_file_object.writerow(["PassengerId","Survived"])
open_file_object.writerows(zip(ids, predicts))
predictions_file.close()
