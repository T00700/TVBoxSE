#coding=utf-8
#!/usr/bin/python
import sys
sys.path.append('..') 
from base.spider import Spider
import requests

class Spider(Spider):
	def getDependence(self):
		return ['py_ali']
	def getName(self):
		return "py_mac"
	def init(self,extend):
		self.ali = extend[0]
		print("============py_mac============")
		pass
	def isVideoFormat(self,url):
		pass
	def manualVideoCheck(self):
		pass
	def homeContent(self,filter):
		result = {}
		return result
	def homeVideoContent(self):
		result = {}
		return result
	def categoryContent(self,tid,pg,filter,extend):
		result = {}
		return result
	header = {
		"User-Agent": "Mozilla/5.0 (Linux; Android 12; V2049A Build/SP1A.210812.003; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/103.0.5060.129 Mobile Safari/537.36",
		"Referer": "http://ali.546326.xyz"
	}
	def detailContent(self,array):
		tid = array[0]
		#print(tid)
		#print(self.getName())
		url="http://ali.546326.xyz/api.php/provide/vod/?ac=detail&ids={0}".format(tid)
		vods=requests.get(url=url, headers=self.header, verify=False).json()["list"][0]
		playurl = vods['vod_play_url']
		playArray = playurl.split("#")
		newArray =[]
		for vod in playArray:
			if vod:
				vds = vod.split("$")
				playurl = vds[1].replace(" ","")
				pattern = '(https://www.aliyundrive.com/s/[^\"]+)'
				url = self.regStr(playurl, pattern)
				if len(url) > 0:
					newArray.append(playurl)
		if len(newArray) == 0:
			return ""
		#print(newArray)
		return self.ali.detailContent(newArray)

	def searchContent(self,key,quick):
		url = "http://ali.546326.xyz/api.php/provide/vod/?wd={0}".format(key)
		vodList = requests.get(url=url, headers=self.header, verify=False).json()["list"]
		videos = []
		for vod in vodList:
			videos.append({
				"vod_id": vod["vod_id"],
                "vod_name": vod["vod_name"],
                "vod_pic": "https://img0.baidu.com/it/u=603086994,1727626977&fm=253&fmt=auto?w=500&h=667",#字段不存在
                "vod_remarks": vod["vod_remarks"]
			})
		result = {
			'list':videos
		}
		return result

	def playerContent(self,flag,id,vipFlags):
		return self.ali.playerContent(flag,id,vipFlags)

	config = {
		"player": {},
		"filter": {}
	}
	header = {}

	def localProxy(self,param):
		return [200, "video/MP2T", action, ""]