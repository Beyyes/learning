# -*- coding: utf-8 -*-
import requests
from bs4 import BeautifulSoup

reload(__import__('sys')).setdefaultencoding('utf-8')

person_url = 'http://history.xikao.com/person/%E9%98%BF%E7%94%B2'
content = BeautifulSoup(requests.get(person_url).text, 'html.parser').find('div', {'id': 'article'})

for x in content:
    # print x.prettify()
    print x.div
    y = x.find_next('hr')
    print '-----------------'
    print y

    # person_introduction = x.get_text(strip=True)
    # cnt = 1
    # if cnt == 1:
    #     print person_introduction[3:]
    # if cnt == 2:
    #     pos = person_introduction.find('活动年表')
    #     if pos != -1:
    #         print person_introduction[:pos]
    # cnt += 1
print
