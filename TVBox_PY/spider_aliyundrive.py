from spider import Spider, SpiderEpisode
from cache import get_cache, set_cache
from proxy import get_proxy_func_url, ProxyFuncResult
from downloader import get_download_url
import requests
import re
import json
import time
import xbmcaddon

_ADDON = xbmcaddon.Addon()

base_headers = {
    'User-Agent':
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.54 Safari/537.36',
    'Referer': 'https://www.aliyundrive.com/',
}


class SpiderAliyunDrive(Spider):
    setting_key_refresh_token = 'aliyundrive_refresh_token'
    setting_key_display_file_size_switch = 'aliyundrive_display_file_size_switch'
    setting_key_downloder_switch = 'downloader_switch'
    regex_share_id = re.compile(
        r'www.aliyundrive.com\/s\/([^\/]+)(\/folder\/([^\/]+))?')
    cache = {}

    def name(self):
        return '阿里云盘'

    def is_searchable(self):
        return False

    def hide(self):
        return True

    def list_categories(self):
        return []

    def list_videos(self, category_id, page):
        return [], False

    def list_episodes(self, video_id):
        m = self.regex_share_id.search(video_id)
        share_id = m.group(1)
        file_id = m.group(3)

        r = requests.post(
            'https://api.aliyundrive.com/adrive/v3/share_link/get_share_by_anonymous',
            json={'share_id': share_id})
        share_info = r.json()

        if len(share_info['file_infos']) == 0:
            return []

        file_info = None
        if file_id:
            for fi in share_info['file_infos']:
                if fi['file_id'] == file_id:
                    file_info = fi
                    break
            if file_info is None:
                return []
        else:
            file_info = share_info['file_infos'][0]
            file_id = file_info['file_id']

        parent_file_id = None
        if file_info['type'] == 'folder':
            parent_file_id = file_id
        elif file_info['type'] == 'file' and file_info['category'] == 'video':
            parent_file_id = 'root'
        else:
            return []

        share_token = self._get_share_token(share_id)

        video_file_infos = []
        subtitle_file_infos = []
        self._list_files(video_file_infos, subtitle_file_infos, share_id,
                         share_token, parent_file_id)
        video_file_infos.sort(key=lambda x: x['name'])

        subtitles = []
        for file_info in subtitle_file_infos:
            subtitles.append({
                'name':
                file_info['name'],
                'url':
                get_proxy_func_url(
                    SpiderAliyunDrive.__name__,
                    self.proxy_download_url.__name__,
                    {
                        'share_id': file_info['share_id'],
                        'file_id': file_info['file_id'],
                        'drive_id': file_info['drive_id'],
                    },
                )
            })

        episodes = []
        display_file_size = _ADDON.getSettingBool(
            self.setting_key_display_file_size_switch)
        for file_info in video_file_infos:
            if display_file_size:
                name = '[{}] {}'.format(
                    self._sizeof_fmt(file_info['size']),
                    file_info['name'],
                )
            else:
                name = file_info['name']

            sources = [{
                'name': '原画',
                'params': {
                    'template_id': '',
                    'share_id': file_info['share_id'],
                    'file_id': file_info['file_id'],
                    'drive_id': file_info['drive_id'],
                },
            }, {
                'name': '超高清',
                'params': {
                    'template_id': 'FHD',
                    'share_id': file_info['share_id'],
                    'file_id': file_info['file_id'],
                    'drive_id': file_info['drive_id'],
                },
            }, {
                'name': '高清',
                'params': {
                    'template_id': 'HD',
                    'share_id': file_info['share_id'],
                    'file_id': file_info['file_id'],
                    'drive_id': file_info['drive_id'],
                },
            }, {
                'name': '标清',
                'params': {
                    'template_id': 'SD',
                    'share_id': file_info['share_id'],
                    'file_id': file_info['file_id'],
                    'drive_id': file_info['drive_id'],
                },
            }]

            episodes.append(
                SpiderEpisode(
                    name=name,
                    sources=sources,
                    cover=share_info['avatar'],
                    description=video_id,
                    subtitles=subtitles,
                ))

        return episodes

    def resolve_play_url(self, episode_params):
        if len(episode_params['template_id']) == 0:
            downloader_switch = _ADDON.getSettingBool(
                self.setting_key_downloder_switch)
            if downloader_switch:
                return get_download_url(
                    spider_class=SpiderAliyunDrive.__name__,
                    func_name=self.proxy_download_url.__name__,
                    params=episode_params)
            else:
                return get_proxy_func_url(SpiderAliyunDrive.__name__,
                                          self.proxy_download_url.__name__,
                                          episode_params)
        else:
            return get_proxy_func_url(SpiderAliyunDrive.__name__,
                                      self.proxy_preview_m3u8.__name__,
                                      episode_params)

    def search_videos(self, keyword):
        return []

    def _get_refresh_token(self):
        token_or_url = _ADDON.getSettingString(self.setting_key_refresh_token)
        if token_or_url.startswith('http://') or token_or_url.startswith(
                'https://'):
            return requests.get(token_or_url).text.strip()
        else:
            return token_or_url

    def _get_access_token(self):
        key = 'aliyundrive:access_token'
        data = self._get_cache(key)
        if data:
            return data['access_token']

        r = requests.post('https://api.aliyundrive.com/token/refresh',
                          json={
                              'refresh_token': self._get_refresh_token(),
                          })
        data = r.json()

        access_token = '{} {}'.format(data['token_type'], data['access_token'])
        expires_at = int(time.time()) + int(data['expires_in'] / 2)
        self._set_cache(key, {
            'access_token': access_token,
            'expires_at': expires_at
        })
        return access_token

    def _get_share_token(self, share_id, share_pwd=''):
        key = 'aliyundrive:share_token'
        data = self._get_cache(key)
        if data:
            if data['share_id'] == share_id and data['share_pwd'] == share_pwd:
                return data['share_token']

        r = requests.post(
            'https://api.aliyundrive.com/v2/share_link/get_share_token',
            json={
                'share_id': share_id,
                'share_pwd': share_pwd
            })
        data = r.json()

        share_token = data['share_token']
        expires_at = int(time.time()) + int(data['expires_in'] / 2)
        self._set_cache(
            key, {
                'share_token': share_token,
                'expires_at': expires_at,
                'share_id': share_id,
                'share_pwd': share_pwd
            })
        return share_token

    def _list_files(self, video_file_infos, subtitle_file_infos, share_id,
                    share_token, parent_file_id):
        marker = ''
        headers = base_headers.copy()
        headers['x-share-token'] = share_token
        for page in range(1, 51):
            if page >= 2 and len(marker) == 0:
                break

            r = requests.post(
                'https://api.aliyundrive.com/adrive/v3/file/list',
                json={
                    "image_thumbnail_process":
                    "image/resize,w_160/format,jpeg",
                    "image_url_process": "image/resize,w_1920/format,jpeg",
                    "limit": 200,
                    "order_by": "updated_at",
                    "order_direction": "DESC",
                    "parent_file_id": parent_file_id,
                    "share_id": share_id,
                    "video_thumbnail_process":
                    "video/snapshot,t_1000,f_jpg,ar_auto,w_300",
                    'marker': marker,
                },
                headers=headers)
            data = r.json()

            for item in data['items']:
                if item['type'] == 'folder':
                    self._list_files(video_file_infos, subtitle_file_infos,
                                     share_id, share_token, item['file_id'])
                elif item['type'] == 'file' and item['category'] == 'video':
                    video_file_infos.append(item)
                elif item['type'] == 'file' and item['file_extension'] in [
                        'srt', 'ass', 'nfo', 'vtt'
                ]:
                    subtitle_file_infos.append(item)

            marker = data['next_marker']

    def _get_m3u8_cache(self, share_id, file_id, template_id):
        key = 'aliyundrive:m3u8'
        data = self._get_cache(key)
        if data:
            if data['share_id'] == share_id and data[
                    'file_id'] == file_id and data[
                        'template_id'] == template_id:
                return data['m3u8'], data['media_urls']

        access_token = self._get_access_token()
        share_token = self._get_share_token(share_id)

        headers = base_headers.copy()
        headers['x-share-token'] = share_token
        headers['Authorization'] = access_token
        r = requests.post(
            'https://api.aliyundrive.com/v2/file/get_share_link_video_preview_play_info',
            json={
                'share_id': share_id,
                'category': 'live_transcoding',
                'file_id': file_id,
                'template_id': '',
            },
            headers=headers,
        )

        preview_url = ''
        for t in r.json(
        )['video_preview_play_info']['live_transcoding_task_list']:
            if t['template_id'] == template_id:
                preview_url = t['url']
                break

        r = requests.get(preview_url,
                         headers=base_headers.copy(),
                         allow_redirects=False)
        preview_url = r.headers['Location']

        lines = []
        media_urls = []
        r = requests.get(preview_url, headers=base_headers.copy(), stream=True)
        media_id = 0
        for line in r.iter_lines():
            line = line.decode()
            if 'x-oss-expires' in line:
                media_url = preview_url[:preview_url.rindex('/') + 1] + line
                media_urls.append(media_url)
                line = get_proxy_func_url(
                    SpiderAliyunDrive.__name__,
                    self.proxy_preview_media.__name__, {
                        'share_id': share_id,
                        'file_id': file_id,
                        'template_id': template_id,
                        'media_id': media_id
                    })
                media_id += 1
            lines.append(line)
        m3u8 = '\n'.join(lines)

        self._set_cache(
            key, {
                'share_id': share_id,
                'file_id': file_id,
                'template_id': template_id,
                'm3u8': m3u8,
                'media_urls': media_urls,
                'expires_at': int(time.time()) + 300,
            })

        return m3u8, media_urls

    def proxy_preview_m3u8(self, params):
        share_id = params['share_id']
        file_id = params['file_id']
        template_id = params['template_id']
        m3u8, _ = self._get_m3u8_cache(share_id, file_id, template_id)
        return ProxyFuncResult(
            body=m3u8,
            headers={'Content-Type': 'application/vnd.apple.mpegurl'})

    def proxy_preview_media(self, params):
        share_id = params['share_id']
        file_id = params['file_id']
        template_id = params['template_id']
        media_id = params['media_id']

        _, media_urls = self._get_m3u8_cache(share_id, file_id, template_id)
        media_url = media_urls[media_id]
        return ProxyFuncResult(url=media_url, headers=base_headers.copy())

    def proxy_download_url(self, params):
        share_id = params['share_id']
        file_id = params['file_id']

        key = 'aliyundrive:download_url:{}:{}'.format(share_id, file_id)
        data = self._get_cache(key)
        if data:
            return ProxyFuncResult(url=data['download_url'],
                                   headers=base_headers.copy())

        access_token = self._get_access_token()
        share_token = self._get_share_token(share_id)

        headers = base_headers.copy()
        headers['x-share-token'] = share_token
        headers['Authorization'] = access_token
        r = requests.post(
            'https://api.aliyundrive.com/v2/file/get_share_link_download_url',
            json={
                'share_id': share_id,
                'file_id': file_id,
                'expires_sec': 7200,
            },
            headers=headers)
        data = r.json()

        r = requests.get(data['download_url'],
                         headers=base_headers.copy(),
                         allow_redirects=False)
        download_url = r.headers['Location']
        self._set_cache(
            key, {
                'download_url': download_url,
                'expires_at': int(time.time()) + 300,
                'share_id': share_id,
                'file_id': file_id,
            })

        return ProxyFuncResult(url=download_url, headers=base_headers.copy())

    def _sizeof_fmt(self, num, suffix="B"):
        for unit in ["", "K", "M", "G", "T", "P", "E", "Z"]:
            if num < 1024.0:
                return f"{num:3.1f} {unit}{suffix}"
            num /= 1024.0
        return f"{num:.1f}Yi{suffix}"

    def _get_cache(self, key):
        if key in self.cache:
            data = self.cache[key]
            if data['expires_at'] >= int(time.time()):
                return data

        data = get_cache(key)
        if data:
            data = json.loads(data)
            if data['expires_at'] >= int(time.time()):
                return data

        return None

    def _set_cache(self, key, value):
        set_cache(key, json.dumps(value))
        self.cache[key] = value
