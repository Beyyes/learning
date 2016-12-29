# -*- coding: utf-8 -*-
reload(__import__('sys')).setdefaultencoding('utf-8')

item_cost = {}
for line in open('data/item.txt'):
    splits = line.split(' ')
    item_cost[int(splits[0])] = int(splits[1])

deals = {}
for line in open('data/deal.txt'):
    splits = line.split(' ')
    user = int(splits[0])
    num = int(splits[1])
    cost = 0
    i = 2
    while i < len(splits):
        n = int(splits[i + 1])
        cost += item_cost[int(splits[i])] * n
        i += 2
    if deals.get(user) is None:
        deals[user] = 0
    deals[user] += cost
#print deals

users = {}
for line in open('data/user.txt'):
    splits = line.replace("\n", "").split(' ')
    s = set()
    for i in range(2, len(splits)):
        s.add(int(splits[i]))
    users[int(splits[0])] = s
print len(users)

error = open('error.txt', 'w')
N = 100 # 输出前100大的土豪度
get_num = 0 # 已经计算出的圈子数目
relations = {}
hashret = set()  # store all users' tuhao degree
maxans = []
for k in users:
    s = set()
    s.add(k) # 考虑圈子只有本身的情况
    s = s.union(users[k])
    for i in users[k]:
        s = s.union(users[i])
    # relations[k] = s
    l = list(s)
    l.sort()
    tuplehash = tuple(l)
    # 判断时为快速就使用hash，将dict的list转化为tuple的tuple并hash；或直接用MD5搞，Hash相同则需要与hash相同的集合逐一对比元素差异
    # 为求出正确圈子数目采用直接判断的方式

    if tuplehash in hashret:
        # error.write(str(tuplehash))
        continue
    hashret.add(tuplehash)
    sum = 0
    for x in l:
        if deals.get(x) is None:
            continue
        sum += deals[x]
    d = {sum : l} # 存储土豪度及圈子集合
    rmid = -1
    min = 4000000000
    if len(maxans) < N:
        maxans.append(d)
        continue
    else:
        for i in range(0, len(maxans)):
            tmp = maxans[i]
            if tmp.keys()[0] < min:
                min = tmp.keys()[0]
                rmid = i

    if min < sum:
        maxans.append(d)
        if len(maxans) > N:
            maxans.pop(rmid)
        print len(maxans)

maxans.sort()
long_ans = 0
# maxans.sort(lambda a, b : )
file = open('output.txt', 'w')

print len(hashret)
# print hashret
for x in reversed(maxans):
    long_ans += x.keys()[0]
    file.write(str(x)+'\n')
file.close()

# print maxans[::-1]
print long_ans

# print relations

# lk 98695 -> 99864 1169
# me 98682-> 99847 1165
