# -*- coding: utf-8 -*-
import requests
from bs4 import BeautifulSoup
reload(__import__('sys')).setdefaultencoding('utf-8')

index_url = 'http://history.xikao.com/people'
xikao_url = 'http://history.xikao.com'


def output_content_of_url(url):
    # with open(indexUrl, 'wb') as f:
    #     f.write()
    url_content = requests.get(url)
    # print urlContent.content
    # url_soup = BeautifulSoup(urlContent.text, "html.parser")
    # print url_soup
    url_soup = BeautifulSoup(url_content.text, 'html.parser')
    name_list = url_soup.find_all("li", {'class': 'bullet'})
    last_name_list = []

    # get all last_name_url_list
    for content in name_list:
        name_href_list = content.find_all('a')
        for href in name_href_list:
            last_name_list.append(href['href'])
            # print href['href']
    print 'all last name size:',
    print len(last_name_list)

    # get all person_introductions
    person_urls = []
    num = 0
    flag = False
    for last_name_url in last_name_list:
        if flag is True:
            break
        cur_url = str(xikao_url + str(last_name_url))
        web_content = requests.get(cur_url)
        full_name_list = BeautifulSoup(web_content.text, 'html.parser').find_all("li", {'class': 'bullet'})
        for full_name_urlcon in full_name_list:
            if flag is True:
                break
            tmp_list = full_name_urlcon.find_all('a')
            for tmp in tmp_list:
                person_urls.append(xikao_url + tmp['href'])
                num += 1
                if num > 1:
                    flag = True
                    break
                # print xikao_url + tmp['href']
        # print full_name_list

    wfile = open('name_url_content.xml', 'w')
    for person_url in person_urls:
        # print requests.get(person_url).text
        content = BeautifulSoup(requests.get(person_url).text, 'html.parser').find('div', {'id': 'article'})
        cnt = 1
        wfile.write(person_url + ' ')
        for x in content:
            person_introduction = x.get_text(strip=True)
            if cnt == 1:
                wfile.write(person_introduction[3:]+' ')
                # print person_introduction[3:]
            if cnt == 2:
                pos = person_introduction.find('活动年表')
                if pos != -1:
                    wfile.write(person_introduction[:pos])
                    # print person_introduction[:pos]
            cnt += 1
        print
    wfile.close()
if __name__ == '__main__':
    output_content_of_url(index_url)

