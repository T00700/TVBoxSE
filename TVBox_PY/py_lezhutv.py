# coding=utf-8
# !/usr/bin/python
import sys
sys.path.append('..')
from base.spider import Spider
import json
import base64
import hashlib


class Spider(Spider):  # 元类 默认的元类 type
    def getName(self):
        return "LeZhuTV"

    def init(self, extend=""):
        print("============{0}============".format(extend))
        pass

    def homeContent(self, filter):
        result = {}
        cateManual = {
            "电影": "1",
            "连续剧": "2",
            "动漫": "4",
            "综艺": "3",
            "韩剧": "14",
            "美剧": "15"
        }
        classes = []
        for k in cateManual:
            classes.append({
                'type_name': k,
                'type_id': cateManual[k]
            })

        result['class'] = classes
        if (filter):
            result['filters'] = self.config['filter']
        return result

    def homeVideoContent(self):
        rsp = self.fetch("http://www.lezhutv.com")
        root = self.html(rsp.text)
        aList = root.xpath("//ul[@class='tbox_m2']/li")
        videos = []
        for a in aList:
            name = a.xpath('.//@title')[0]
            pic = a.xpath('.//@data-original')[0]
            mark = a.xpath(".//span/text()")[0]
            sid = a.xpath(".//@href")[0]
            sid = self.regStr(sid, "/detail/(\\d+).html")
            videos.append({
                "vod_id": sid,
                "vod_name": name,
                "vod_pic": pic,
                "vod_remarks": mark
            })
        result = {
            'list': videos
        }
        return result

    def categoryContent(self, tid, pg, filter, extend):
        result = {}

        ext = extend.get("by","")
        url = 'http://www.lezhutv.com/list/{0}_{1}_desc_{2}_0_0___.html'.format(tid,pg,ext)
        rsp = self.fetch(url)
        root = self.html(rsp.text)
        aList = root.xpath("//ul[@class='tbox_m2']/li")
        videos = []
        for a in aList:
            name = a.xpath('.//@title')[0]
            pic = a.xpath('.//@data-original')[0]
            mark = a.xpath(".//span/text()")[0]
            sid = a.xpath(".//@href")[0]
            sid = self.regStr(sid, "/detail/(\\d+).html")
            videos.append({
                "vod_id": sid,
                "vod_name": name,
                "vod_pic": pic,
                "vod_remarks": mark
            })
        result['list'] = videos
        result['page'] = pg
        result['pagecount'] = 9999
        result['limit'] = 90
        result['total'] = 999999
        return result

    def detailContent(self, array):
        tid = array[0]
        url = 'http://www.lezhutv.com/detail/{0}.html'.format(tid)
        rsp = self.fetch(url)
        root = self.html(rsp.text)
        node = root.xpath(".//div[@class='dbox']")[0]
        nodes = root.xpath(".//div[@class='tbox2']")[0]
        pic = node.xpath(".//div/@data-original")[0]
        title = node.xpath('.//h4/text()')[0]
        detail = nodes.xpath(".//div[@class='tbox_js']/text()")[0]
        yac = node.xpath(".//p[@class='yac']/text()")[0]
        yac = yac.split('/')
        yacs = yac[0].strip()
        type_name = yac[1].strip()
        actor = node.xpath(".//p[@class='act']/text()")[0]
        director = node.xpath(".//p[@class='dir']/text()")[0]

        vod = {
            "vod_id": tid,
            "vod_name": title,
            "vod_pic": pic,
            "type_name": type_name,
            "vod_year": yacs,
            "vod_area": "",
            "vod_remarks": "",
            "vod_actor": actor,
            "vod_director": director,
            "vod_content": detail
        }

        vod_play_from = '$$$'
        playFrom = []
        vodHeader = root.xpath(".//div[@class='tbox2 tabs']/div/h3/text()")
        i=1
        for v in vodHeader:
            playFrom.append("线路" + str(i))
            i = i+1
        vod_play_from = vod_play_from.join(playFrom)
        vod_play_url = '$$$'
        playList = []
        vodList = root.xpath("//div[@class='tbox2 tabs']")

        for vl in vodList:
            vodItems = []
            aList = vl.xpath(".//ul/li/a")
            for tA in aList:
                href = tA.xpath('./@href')[0]
                name = tA.xpath('./text()')[0]
                tId = self.regStr(href, '/play/(\\S+).html')
                vodItems.append(name + "$" + tId)
            joinStr = '#'
            joinStr = joinStr.join(vodItems)
            playList.append(joinStr)
        vod_play_url = vod_play_url.join(playList)
        vod['vod_play_from'] = vod_play_from
        vod['vod_play_url'] = vod_play_url

        result = {
            'list': [
                vod
            ]
        }
        return result

    def searchContent(self, key, quick):
        url = 'http://www.lezhutv.com/search-pg-1-wd-{0}.html'.format(key)
        rsp = self.fetch(url)
        root = self.html(rsp.text)
        seaArray = root.xpath("//ul[@class='tbox_m']/li")
        seaList = []
        for vod in seaArray:
            name = vod.xpath('.//@title')[0]
            pic = vod.xpath('.//@data-original')[0]
            mark = vod.xpath(".//span/text()")[0]
            sid = vod.xpath(".//@href")[0]
            sid = self.regStr(sid, "/detail/(\\d+).html")
            seaList.append({
                "vod_id": sid,
                "vod_name": name,
                "vod_pic": pic,
                "vod_remarks": mark
            })
        result = {
            'list': seaList
        }
        return result

    config = {
        "player":{},
        "filter":{"1":[{"key":"by","name":"排序","value":[{"n":"时间","v":"time"},{"n":"人气","v":"score"},{"n":"评分","v":"hits"}]}],"2":[{"key":"by","name":"排序","value":[{"n":"时间","v":"time"},{"n":"人气","v":"score"},{"n":"评分","v":"hits"}]}],"3":[{"key":"by","name":"排序","value":[{"n":"时间","v":"time"},{"n":"人气","v":"score"},{"n":"评分","v":"hits"}]}],"4":[{"key":"by","name":"排序","value":[{"n":"时间","v":"time"},{"n":"人气","v":"score"},{"n":"评分","v":"hits"}]}],"14":[{"key":"by","name":"排序","value":[{"n":"时间","v":"time"},{"n":"人气","v":"score"},{"n":"评分","v":"hits"}]}],"15":[{"key":"by","name":"排序","value":[{"n":"时间","v":"time"},{"n":"人气","v":"score"},{"n":"评分","v":"hits"}]}]}
    }
    header = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36"
    }

    def get_md5(self,value):
        b64 = base64.b64encode((base64.b64encode(value.encode()).decode() + "NTY2").encode()).decode()
        md5 = hashlib.md5(b64.encode()).hexdigest()
        return "".join(char if char.isdigit() else "zyxwvutsrqponmlkjihgfedcba"["abcdefghijklmnopqrstuvwxyz".find(char)] for char in md5)

    def playerContent(self, flag, id, vipFlags):
        result = {}
        url = 'http://www.lezhutv.com/play/{0}.html'.format(id)
        rsp = self.fetch(url)
        root = self.html(rsp.text)
        scripts = root.xpath("//script/text()")
        scripts = scripts[1].replace('\n', '')
        nid = self.regStr(scripts, 'view_path = \'(.*?)\';')

        md5url = 'http://www.lezhutv.com/hls2/index.php?url={0}'.format(nid)
        rsp = self.fetch(md5url)
        root = self.html(rsp.text)
        value = root.xpath(".//input[@id='hdMd5']/@value")
        value = ''.join(value)
        md5s = self.get_md5(str(value))
        data = {
            "id": nid,
            "type": "vid",
            "siteuser": "",
            "md5": md5s,
            "referer": url,
            "hd": "",
            "lg": ""
        }
        payUrl = 'http://www.lezhutv.com/hls2/url.php'
        parseRsp = self.post(payUrl,data,headers=self.header)
        parseRsps = json.loads(parseRsp.text)
        realUrl = parseRsps['media']['url']
        if len(realUrl) > 0:
            result["parse"] = 0
            result["playUrl"] = ""
            result["url"] = realUrl
            result["header"] = ""
        else:
            result["parse"] = 1
            result["playUrl"] = ""
            result["url"] = url
            result["header"] = json.dumps(self.header)
        return result

    def isVideoFormat(self, url):
        pass

    def manualVideoCheck(self):
        pass

    def localProxy(self, param):
        return [200, "video/MP2T", action, ""]