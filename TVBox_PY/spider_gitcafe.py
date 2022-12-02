from spider import SpiderCategory, SpiderVideo
from spider_aliyundrive import SpiderAliyunDrive
import requests


class SpiderGitCafe(SpiderAliyunDrive):

    def name(self):
        return '小纸条'

    def is_searchable(self):
        return True

    def hide(self):
        return False

    def list_categories(self):
        categories = []
        categories.append(SpiderCategory('hydm', '华语动漫'))
        categories.append(SpiderCategory("hyds", '华语电视'))
        categories.append(SpiderCategory("hydy", '华语电影'))
        categories.append(SpiderCategory("omdm", '欧美动漫'))
        categories.append(SpiderCategory("omds", '欧美电视'))
        categories.append(SpiderCategory("omdy", '欧美电影'))
        categories.append(SpiderCategory("rhdm", '日韩动漫'))
        categories.append(SpiderCategory("rhds", '日韩电视'))
        categories.append(SpiderCategory("rhdy", '日韩电影'))
        categories.append(SpiderCategory("qtds", '其他电视'))
        categories.append(SpiderCategory("qtdy", '其他电影'))
        categories.append(SpiderCategory("qtsp", '其他视频'))
        categories.append(SpiderCategory("jlp", '纪录片'))
        categories.append(SpiderCategory("zyp", '综艺片'))
        return categories

    def list_videos(self, category_id, page):
        r = requests.post('https://gitcafe.net/tool/alipaper/',
                          data={
                              'action': 'viewcat',
                              'cat': category_id,
                              'num': page,
                          })
        data = r.json()

        videos = []
        for video in data:
            videos.append(
                SpiderVideo(
                    id='https://www.aliyundrive.com/s/' + video['key'],
                    name=video['title'],
                ))

        return videos, len(videos) >= 50

    def search_videos(self, keyword):
        r = requests.post('https://gitcafe.net/tool/alipaper/',
                          data={
                              'action': 'search',
                              'keyword': keyword,
                          })
        data = r.json()

        videos = []
        for video in data:
            videos.append(
                SpiderVideo(
                    id='https://www.aliyundrive.com/s/' + video['key'],
                    name=video['title'],
                ))

        return videos
