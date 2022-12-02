from spider import SpiderVideo
from spider_aliyundrive import SpiderAliyunDrive
from bs4 import BeautifulSoup
import requests
import re


class SpiderAliPanSou(SpiderAliyunDrive):

    regex_url = re.compile(r'https://www.aliyundrive.com/s/[^"]+')

    def name(self):
        return '猫狸盘搜'

    def is_searchable(self):
        return True

    def hide(self):
        return False

    def list_categories(self):
        return []

    def list_videos(self, category_id, page):
        return [], False

    def list_episodes(self, video_id):
        r = requests.get('https://www.alipansou.com' + video_id)
        m = self.regex_url.search(r.text)
        url = m.group().replace('\\', '')
        return super().list_episodes(url)

    def search_videos(self, keyword):
        r = requests.get('https://www.alipansou.com/search',
                         params={
                             'k': keyword,
                             't': 7,
                         })
        soup = BeautifulSoup(r.text, 'html.parser')

        items = soup.select('van-row > a')
        videos = []
        for item in items:
            name = self._remove_html_tags(item.find('template').__str__())
            #if keyword not in name:
            #    continue
            videos.append(SpiderVideo(
                id=item.get('href'),
                name=name,
            ))

        return videos

    def _remove_html_tags(self, text):
        """Remove html tags from a string"""
        import re
        clean = re.compile('<.*?>')
        return re.sub(clean, '', text)
