# -*- coding: utf-8 -*-

import pandas as pd
import np
import matplotlib.pyplot as plt
from sklearn.tree import DecisionTreeClassifier
from sklearn import metrics
from sklearn.ensemble import RandomForestRegressor
from sklearn.ensemble import RandomForestClassifier
reload(__import__('sys')).setdefaultencoding('utf-8')

names = ("age, workclass, fnlwgt, education, education-num, "
         "marital-status, occupation, relationship, race, sex, "
         "capital-gain, capital-loss, hours-per-week, "
         "native-country, income").split(', ')

data_train = pd.read_csv('data/adult.data.txt', names = names)
data_test = pd.read_csv('data/adult.test.txt', names = names)
# print data
# print data.head()
# print data.describe()

# 按职业分组、计数并排序
# data.groupby('occupation').size().sort_values(ascending=False)
# print data.head()

# 数据信息
data_train.info()

# 将<50K存入low_income, 将>50K存入high_income
low_income = data_train[data_train['income']==' <=50K']
high_income = data_train[data_train['income']==' >50K']

# 画图展示，分析哪些列数据有用
fig = plt.figure()
fig.set(alpha=0.2)

# # income 分布图
# data_train.income.value_counts().plot(kind='bar') # income柱状图
# plt.title('income')  # 收入>50K与<50K分布
# plt.ylabel('number')
# plt.show()
#

# 收入与年龄分布图
# bins = np.linspace(10,90,20)
# plt.hist(low_income['age'].values, bins=bins, alpha=0.5, label='<=50K')
# plt.hist(high_income['age'].values, bins=bins, alpha=0.5, label='>50K')
# plt.legend(loc='best')
# # data_train.age.value_counts().plot(kind='bar')
# # plt.title('age')  # 收入>50K与<50K分布
# # plt.ylabel('number')
# plt.show()

# 提取含有"?"的数据 workclass, native-country, occupation
data_matrix = data_train.as_matrix()
# unknown = data_train[data_train['native-country']==' ?' or data_train['workclass']==' ?']
unknown = []
for x in data_matrix:
    if x[1]==' ?' or x[6]==' ?' or x[13]==' ?':
        unknown.append(x)
print 'all ? data num : ' + str(len(unknown))

# 数值型数据、分类变量转换
def transfer_to_num(df):
    target = df['income']
    features_data = df.drop('income', axis=1)
    numeric_features = [c for c in features_data if features_data[c].dtype.kind in ('i', 'f')]
    numeric_data = features_data[numeric_features]
    categorical_data = features_data.drop(numeric_features, 1)
    categorical_data_encoded = categorical_data.apply(lambda x: pd.factorize(x)[0])
    features = pd.concat([numeric_data, categorical_data_encoded], axis=1)
    X = features.values.astype(np.float32)
    return X

# 使用RandomForestClassifier填补缺失属性性
def deal_unknow_item(df):
    # 把已有的数值型特征丢进Random Forest Regressor
    clear_df = df[df.workclass!=' ?']
    clear_df = clear_df[clear_df['native-country'] != ' ?']
    clear_df = clear_df[clear_df['occupation'] != ' ?']
    # print len(clear_df) # 30162
    target_cur = clear_df['income']
    features_data_cur = clear_df.drop('income', axis=1)
    numeric_features_cur = [c for c in features_data_cur if features_data_cur[c].dtype.kind in ('i', 'f')]
    numeric_data_cur = features_data_cur[numeric_features_cur]
    categorical_data_cur = features_data_cur.drop(numeric_features_cur, 1)
    categorical_data_encoded = categorical_data_cur.apply(lambda x: pd.factorize(x)[0])
    features_cur = pd.concat([numeric_data_cur, categorical_data_encoded], axis=1)
    X = features_cur.values.astype(np.float32)
    y = (target_cur.values == ' >50K').astype(np.int32)
    rfr = RandomForestRegressor(random_state=0, n_estimators=10, n_jobs=-1)
    rfr.fit(X, y)
    # 用得到的模型去对未知三项做预测
    known_workclass = df[df.workclass!=' ?']
    unknown_workclass = df[df.workclass==' ?']
    X = transfer_to_num(unknown_workclass)
    predictedWorkclass = rfr.predict(X)
    print predictedWorkclass

deal_unknow_item(data_train)


# 模型构建
# 设置因变量target, 自变量features_data(去除income的所有变量)
# 将数值型保存在numeric_features
target = data_train['income']
features_data = data_train.drop('income', axis=1)
numeric_features = [c for c in features_data if features_data[c].dtype.kind in ('i', 'f')] # 提取数值类型为整数或浮点数的变量
print numeric_features
numeric_data = features_data[numeric_features]
#print numeric_data.head(5)
categorical_data = features_data.drop(numeric_features, 1)
# print categorical_data.head(5)
# 分类变量
categorical_data_encoded = categorical_data.apply(lambda x: pd.factorize(x)[0]) # pd.factorize即可将分类变量转换为数值表示                                                                                # apply运算将转换函数应用到每一个变量维度
categorical_data_encoded.head(5)
# 所有数据
features = pd.concat([numeric_data, categorical_data_encoded], axis=1)
# features.head()

X = features.values.astype(np.float32) # 转换数据类型
y = (target.values == ' >50K').astype(np.int32) # 收入水平 ">50K" 记为1，“<=50K” 记为0

clf = DecisionTreeClassifier(max_depth=8) # 参数max_depth设置树最大深度
# clf = RandomForestClassifier(n_estimators=10, max_depth=None, min_samples_split=2, random_state=0)  # 0.83
# from sklearn.svm import SVC
# clf = SVC()
clf.fit(X, y)
print clf

### test data
# 模型构建
# 设置因变量target, 自变量features_data(去除income的所有变量)
# 将数值型保存在numeric_features
target = data_test['income']
features_data = data_test.drop('income', axis=1)
numeric_features = [c for c in features_data if features_data[c].dtype.kind in ('i', 'f')] # 提取数值类型为整数或浮点数的变量
numeric_data = features_data[numeric_features]
#print numeric_data.head(5)
categorical_data = features_data.drop(numeric_features, 1)
# print categorical_data.head(5)
# 分类变量
categorical_data_encoded = categorical_data.apply(lambda x: pd.factorize(x)[0]) # pd.factorize即可将分类变量转换为数值表示                                                                                # apply运算将转换函数应用到每一个变量维度
categorical_data_encoded.head(5)
# 所有数据
features = pd.concat([numeric_data, categorical_data_encoded], axis=1)
X = features.values.astype(np.float32) # 转换数据类型
pre = clf.predict(X)
y = (target.values == ' >50K.').astype(np.int32) # 收入水平 ">50K" 记为1，“<=50K” 记为0
y_list = y.tolist()
pre_list = pre.tolist()

# for i in range(0, 6):
#     print pre_list[i]

# 比较准确率
ans = 0
for i in range(0, len(y_list)):
    if y_list[i]==pre_list[i]:
        ans += 1
print ans, len(y_list), float(float(ans) / float(len(y_list)))

predictions = clf.predict(X)
result = pd.DataFrame({'age':data_test['age'].as_matrix(),'Survived':predictions.astype(np.int32)})
result.to_csv("data/result.csv", index=False)
