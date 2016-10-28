# -*- coding: utf-8 -*-
import requests
from bs4 import BeautifulSoup
import jieba
import jieba.posseg as pseg # 词性标注

reload(__import__('sys')).setdefaultencoding('utf-8')

def readfile(filename):
    jieba.load_userdict('../dict.txt') # 导入自定义词典  tips:更改词频
    file = open(filename)
    content = file.readline()
    parts = content.split(' ')
    res = pseg.lcut(parts[2]) # 返回列表形式
    # for i in range(1, len(res)):
    #     print (res[i]).word
    for word, flag in res:
        if flag == 'nr':
            print word



if __name__ == '__main__':
    readfile('../name_url_content.xml')