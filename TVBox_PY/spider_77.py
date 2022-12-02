from spider import Spider, SpiderCategory, SpiderVideo, SpiderEpisode
from proxy import get_proxy_main_url
from urllib.parse import urlparse
from danmaku import get_danmaku_url
import requests
import hashlib
import time

base_params = {
    'pcode': '010110005',
    'version': '2.0.5',
    'devid': hashlib.md5(str(time.time()).encode()).hexdigest(),
    'sys': 'android',
    'sysver': 11,
    'brand': 'google',
    'model': 'Pixel_3_XL',
    'package': 'com.sevenVideo.app.android'
}

base_headers = {
    'User-Agent': 'okhttp/3.12.0',
}


class Spider77(Spider):

    def name(self):
        return '七七'

    def is_searchable(self):
        return True

    def list_categories(self):
        r = requests.get('http://api.kunyu77.com/api.php/provide/filter',
                         headers=base_headers.copy())
        data = r.json()
        categories = []
        for category_id in data['data']:
            category_name = data['data'][category_id][0]['cat']
            categories.append(SpiderCategory(category_id, category_name))
        return categories

    def list_videos(self, category_id, page):
        r = requests.get('http://api.kunyu77.com/api.php/provide/searchFilter',
                         params={
                             'type_id': category_id,
                             'pagenum': page,
                             'pagesize': 50
                         },
                         headers=base_headers.copy())
        data = r.json()
        videos = []
        for video in data['data']['result']:
            videos.append(
                SpiderVideo(id=video['id'],
                            name=video['title'],
                            cover=video['videoCover']))

        has_next_page = page < data['data']['pagesize']
        return videos, has_next_page

    def list_episodes(self, video_id):
        ts = int(time.time())
        params = base_params.copy()
        params['ids'] = video_id
        params['sj'] = ts

        headers = base_headers.copy()
        headers['t'] = str(ts)

        url = 'http://api.kunyu77.com/api.php/provide/videoDetail'
        headers['TK'] = self._get_tk(url, params, ts)
        r = requests.get(url, params=params, headers=headers)
        detail = r.json()['data']

        url = 'http://api.kunyu77.com/api.php/provide/videoPlaylist'
        headers['TK'] = self._get_tk(url, params, ts)
        r = requests.get(url, params=params, headers=headers)
        episodes = r.json()['data']['episodes']

        mepisodes = []
        for episode in episodes:

            sources = []
            danmakus = []
            for playurl in episode['playurls']:
                if playurl['playfrom'] in ['ppayun']:
                    sources.append({
                        'name': playurl['playfrom'],
                        'params': {
                            'url': playurl['playurl'],
                        }
                    })

                if playurl['playfrom'] in [
                        'qq', 'mgtv', 'qiyi', 'youku', 'bilibili'
                ]:
                    danmakus.append({
                        'name': playurl['playfrom'],
                        'url': get_danmaku_url(playurl['playurl']),
                    })

            mepisodes.append(
                SpiderEpisode(
                    name=episode['title'],
                    sources=sources,
                    cover=detail['videoCover'],
                    description=detail['brief'],
                    cast=detail['actor'].split(' '),
                    director=detail['director'],
                    area=detail['area'],
                    year=int(detail['year']),
                    danmakus=danmakus,
                ))

        return mepisodes

    def resolve_play_url(self, episode_params):
        headers = base_headers.copy()

        r = requests.get('http://api.kunyu77.com/api.php/provide/parserUrl',
                         params={'url': episode_params['url']},
                         headers=base_headers.copy())
        data = r.json()['data']
        if 'playHeader' in data:
            for key in data['playHeader']:
                headers[key] = data['playHeader'][key]

        r = requests.get(data['url'])
        return get_proxy_main_url(r.json()['url'], headers)

    def search_videos(self, keyword):
        url = 'http://api.kunyu77.com/api.php/provide/searchVideo'

        ts = int(time.time())
        params = base_params.copy()
        params['sj'] = ts
        params['searchName'] = keyword
        params['pg'] = 1

        headers = base_headers.copy()
        headers['t'] = str(ts)
        headers['TK'] = self._get_tk(url, params, ts)

        r = requests.get(url, params=params, headers=headers)
        data = r.json()
        videos = []
        for video in data['data']:
            videos.append(
                SpiderVideo(
                    id=video['id'],
                    name=video['videoName'],
                    cover=video['videoCover'],
                    description=video['brief'],
                    cast=video['starName'].split(','),
                    year=int(video['year']),
                ))
        return videos

    def _get_tk(self, url, params, ts):
        keys = []
        for key in params:
            keys.append(key)
        keys.sort()

        src = urlparse(url).path
        for key in keys:
            src += str(params[key])
        src += str(ts)
        src += 'XSpeUFjJ'

        return hashlib.md5(src.encode()).hexdigest()
