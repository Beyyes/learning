# -*- coding: utf-8 -*-
import requests
from bs4 import BeautifulSoup
reload(__import__('sys')).setdefaultencoding('utf-8')
from xml.dom.minidom import Document

def transfer(filename):
    doc = Document()  # 创建DOM文档对象
    bookstore = doc.createElement('bookstore')  # 创建根元素
    doc.appendChild(bookstore)

    book = doc.createElement('book')
    book.setAttribute('genre', 'XML')
    bookstore.appendChild(book)

    f = open('../data/bookstore.xml', 'w')
    f.write(doc.toprettyxml(indent=''))
    f.close()

if __name__ == '__main__':
    transfer('../data/name_url_content.xml')