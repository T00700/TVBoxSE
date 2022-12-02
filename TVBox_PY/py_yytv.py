#coding=utf-8
#!/usr/bin/python
import sys
sys.path.append('..') 
from base.spider import Spider
import json

class Spider(Spider):
	def getName(self):
		return "体育直播"
	def init(self,extend=""):
		pass
	def isVideoFormat(self,url):
		pass
	def manualVideoCheck(self):
		pass
	def homeContent(self,filter):
		result = {}
		cateManual = {
			"全部": "0",
			"足球": "1",
			"篮球": "2",
			"其他": "5"
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
		result = {}
		return result

	def categoryContent(self,tid,pg,filter,extend):
		result = {}
		url = 'https://json.cranemarsh.com/all_live_rooms.json'
		rsp = self.fetch(url)
		pat = 'all_live_rooms\\((.*)\\)'
		Root = self.regStr(rsp.text, pat)
		jRoot = json.loads(Root)
		videos = []
		vodList = jRoot['data'][tid]
		for vod in vodList:
			aid = vod['roomNum']
			title = vod['title'].strip()
			img =  vod['cover'].strip()
			remark = vod['anchor']['nickName'].strip()
			videos.append({
				"vod_id":aid,
				"vod_name":title,
				"vod_pic":img,
				"vod_remarks":remark
			})
		result['list'] = videos
		result['page'] = pg
		result['pagecount'] = 9999
		result['limit'] = 90
		result['total'] = 999999
		return result

	def detailContent(self,array):
		aid = array[0]
		url = "https://json.cranemarsh.com/room/{0}/detail.json".format(aid)
		rsp = self.fetch(url,headers=self.header)
		pat = 'detail\\((.*)\\)'
		Root = self.regStr(rsp.text, pat)
		jRoot = json.loads(Root)
		jo = jRoot['data']['room']
		id = jo['roomNum']
		title = jo['title']
		pic = jo['cover']
		vod = {
			"vod_id":id,
			"vod_name":title,
			"vod_pic":pic,
			"type_name":'',
			"vod_year":"",
			"vod_area":'',
			"vod_remarks":'',
			"vod_actor":"",
			"vod_director":"",
			"vod_content":''
		}
		ja = jRoot['data']['stream']
		flv = ja['flv']
		hdFlv = ja['hdFlv']
		m3u8 = ja['m3u8']
		hdM3u8 = ja['hdM3u8']
		playUrl = 'FLV' + '$' + flv + '#' + '高清FLV' + '$' + hdFlv + '#' + 'M3U8' + '$' + m3u8 + '#' + '高清M3U8' + '$' + hdM3u8 + '#'
		vod['vod_play_from'] = '体育直播'
		vod['vod_play_url'] = playUrl
		result = {
			'list':[
				vod
			]
		}
		return result
	def searchContent(self,key,quick):
		result = {}
		return result
	def playerContent(self,flag,id,vipFlags):
		result = {}
		url = id
		result["parse"] = 0
		result["playUrl"] = ''
		result["url"] = url
		result["header"] = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36"}

		return result

	config = {
		"player": {},
		"filter": {}
	}
	header = {}

	config = {
		"player": {},
		"filter": {}
	}
	header = {}
	def localProxy(self,param):
		action = {
			'url':'',
			'header':'',
			'param':'',
			'type':'string',
			'after':''
		}
		return [200, "video/MP2T", action, ""]