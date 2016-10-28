# -*- coding: utf-8 -*-
import requests
import jieba
import jieba.posseg as pseg # 词性标注

reload(__import__('sys')).setdefaultencoding('utf-8')

def write_name(filename):
    dict = {}
    list = []
    for line in open(filename):
        if line.find('（') != -1:
            two = line.split('（')
            list.append(two[0])
            name_splits = two[1][0:two[1].find('）')].split('、')
            for t in name_splits:
                list.append(t)
                # print t
        else:
            list.append(line.strip('\n'))
    file = open('../data/name_result.txt', 'w')
    for l in list:
        file.write(l + ' ' + '3' + ' nr\n')
    file.close()
if __name__ == '__main__':
    write_name('../data/names2.xml')