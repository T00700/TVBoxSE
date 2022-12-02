from spider import SpiderVideo
from spider_aliyundrive import SpiderAliyunDrive
from bs4 import BeautifulSoup
import requests
import re


class SpiderZhaoZiYuan(SpiderAliyunDrive):

    regex_url = re.compile(r'https://www.aliyundrive.com/s/[^"]+')

    def name(self):
        return '找资源'

    def is_searchable(self):
        return True

    def hide(self):
        return False

    def list_categories(self):
        return []

    def list_videos(self, category_id, page):
        return [], False

    def list_episodes(self, video_id):
        r = requests.get('https://zhaoziyuan.la/' + video_id)
        print(r.text)
        m = self.regex_url.search(r.text)
        url = m.group().replace('\\', '')
        return super().list_episodes(url)

    def search_videos(self, keyword):
        r = requests.get('https://zhaoziyuan.la/so',
                         params={
                             'filename': keyword,
                         })
        soup = BeautifulSoup(r.text, 'html.parser')

        items = soup.select('div.news_text > a')
        videos = []
        for item in items:
            name = item.find('h3').text
            #if keyword not in name:
            #    continue
            videos.append(SpiderVideo(
                id=item.get('href'),
                name=name,
            ))

        return videos
