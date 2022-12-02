<?php
header("Content-Type: text/html; charset=UTF-8");
libxml_use_internal_errors(true);

//建议php版本7 开启curl扩展
$typeid =$_GET["t"];
$page = $_GET["pg"];
$ids = $_GET["ids"];
$burl = $_GET["url"];
$wd = $_GET["wd"];


//通用模板要修改的东西只有：$web=网站链接          $movietype=影视分类      $url1=影视详情链接文本左  $url2=影视详情链接文本右
//通用模板要修改的东西只有：$liebiao=影视列表链接  $num=影视列表单页多少个  $detail=影视详情链接


//===============================================基础配置开始===========================================
$web='https://cokemv.me';

//1=开启搜索  0=关闭搜索 默认关闭搜索（极品有验证码 开启无效，搜索框架仅供参考）
$searchable=1;


//====================以下内容可忽略不修改===================
//1=curl访问 2=file_get_contents 真不卡用curl被屏蔽了，所以改为2.  //$gettype仅在此模板有效(其他模板未添加这个设置) //如遇其他网站出现类似的【电脑可以访问，但是php打不开】也可以尝试用此模板
$gettype=1;

//$gettype=1时设置cookie有效，如不懂可以不填写(针对奈菲这样子的，就需要写入cookie才能访问)
$cookie='';

//当影视详情没有影视图片或取图片失败时，返回该指定的图片链接(不设置的话，缺图时历史记录的主图会空白)
$historyimg='';

//模拟ua 如非不要默认即可
$UserAgent='Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36';

//1=开启直链分析  0=关闭直链分析 (直链也是通过本php页面解析) 测试极品关闭直链 大部分能通过webview解析
//该模板的直链代码是针对极品影视的，每个站的直链代码都不同。其他网站请设置为0关闭
$zhilian=0;

//QQ iqiyi  youku的VIP解析 没用直链的话，$vip设置无效
$vip='https://jxapp.jpysvip.net/m3u8.php?url=';
//====================以上内容可忽略不修改===================

//===============================================基础配置结束===========================================




//===============================================广告图片配置开始 可以不用修改=======================================
//$adable=1开启广告  $adable=0关闭广告图片  可插入指定图片到每次读取第一页影视列表的开头，默认关闭
$adable=0;
$adpicurl='https://alifei05.cfp.cn/creative/vcg/800/version23/VCG41184086603.jpg';
$adtitle1='我是片名';
$adtitle2='我是更新内容';
//===============================================广告图片配置结束 可以不用修改============================================





//===============================================影视分类相关配置开始===========================
//例如 国产剧链接：https://www.jpysvip.net/vodshow/13-----------.html 那国产剧ID就等于13
//例如 电影链接：https://www.jpysvip.net/vodtype/1.html 那电影ID就等于1
//电影 连续剧 关键词中间要用1个空格隔开，否则无法显示
$movietype = '{"class":[{"type_id":1,"type_name":"电影"},{"type_id":3,"type_name":"综艺"},{"type_id":2,"type_name":"电视剧"},{"type_id":4,"type_name":"动漫"}]}';

//支持类目ID非数字，type_id按照正常顺序排列 把catname写进去.注意$liebiao也要填写正确
//$movietype ='{"class":[{"type_id":1,"type_name":"全部动漫","catname":"all-all-all-all-all-time-{pageid}"},{"type_id":2,"type_name":"日漫","catname":"all-all-all-all-all-time-{pageid}-日本-all-all"},{"type_id":3,"type_name":"国漫","catname":"all-all-all-all-all-time-{pageid}-中国-all-all"},{"type_id":4,"type_name":"欧美动漫","catname":"all-all-all-all-all-time-{pageid}-欧美-all-all"}]}';
//===============================================影视分类相关配置结束===========================






//===============================================影视列表相关配置开始===========================


//=====================苹果CMS通用模板修改以下内容即可=============================
//取出影片ID的文本左边
$url1='/voddetail/';

//取出影片ID的文本右边
$url2='.html';

//影视列表链接 {pageid}=页码  {typeid}=类目ID    如果$movietype的catname不为空的话，{typeid}会被自动替换为相应的catname内容
$liebiao='https://cokemv.me/vodshow/{typeid}--------{pageid}---.html';
//每页多少个影片
$num=72;
//=====================苹果CMS通用模板修改以上内容即可=============================





//xpath列表
$query="//div[contains(@class,'module-items')]/a";

//取出影片的图片
$picAttr="//div[contains(@class,'module-items')]/a//img/@data-original";

//取出影片的图片 优先取$picAttr  $picAttr无结果的话，从$picAttr2取
//如果会xpath的话，只需设置$picAttr的即可
$picAttr2="";

//取出影片的标题
$titleAttr="//div[contains(@class,'module-items')]/a/@title";

//取出影片的链接
$linkAttr="//div[contains(@class,'module-items')]/a/@href";

//影视更新情况 例如：更新至*集
$query2 = "//div[contains(@class,'module-items')]/a/div[@class='module-item-cover']/div[@class='module-item-note']/text()";

//影视更新情况 例如：更新至*集 优先取$query2  $query2无结果的话，从$query3取
//如果会xpath的话，只需设置$query2的即可
$query3 = "//li/div[contains(@class,'-vodlist__box')]/a/span[contains(@class,'tag')]";
//===============================================影视列表相关配置结束===========================







//===============================================影视详情相关配置开始===========================


//=====================苹果CMS通用模板修改以下内容即可=============================
//影片链接 {vodid}=影片ID 
$detail='https://cokemv.me/voddetail/{vodid}.html';
//=====================苹果CMS通用模板修改以上内容即可=============================




//影片名称
$vodtitle="//div[contains(@class,'module-info-heading')]/h1";

//影片类型
$vodtype="//div[@class='module-info-tag']/div[3]/a";

//取出影片图片 猫的详情图片显示在历史记录里（历史记录图片没有的话，就是这个没取对）
$vodimg="//img[contains(@class,'ls-is-cached')]/@data-original";

//取出影片简介
$vodtext="//div[@class='module-info-introduction-content']/p/text()";

//取出影片年份
$vodyear="//div[contains(@class,'tag-link')][1]/a/text()";

//取出影片主演
$vodactor="//span[contains(text(),'主演')]/following-sibling::div/a/text()";

//取出影片导演
$voddirector="//span[contains(text(),'导演')]/following-sibling::div/a/text()";

//取出影片地区
$vodarea="//div[contains(@class,'tag-link')][2]/a/text()";

//播放地址名称 //div[contains(@class,'-panel__head') and contains(@class, 'clearfix')]/ul/li/a
//为了通用性，没有取出播放源名称，会xpath的可以自己填写进去 例子如上
$playname="//div[@id='y-playList']/div/span/text()";

//播放地址 自动往下级尝试查找5次并取链接 如第二次就找到链接，就会从第二个下级中获取
$playurl="//div[@id='panel1']";

//取出影片的全部播放链接  链接标识一般为href，不用修改
$linkAttr5='href';
//===============================================影视详情相关配置结束===========================







//===============================================影视搜索相关配置开始===========================
//=========下面把xpath规则的搜索屏蔽了，极品采用json的搜索结果========


//影片搜索返回结果 1=htm代码套用xpath规则   2=json结果
//$searchtype=1;
$searchtype=2;

//影片搜索 {wd}=搜索文字
//$searchtype=1的网址
//$search='https://www.jpysvip.net/vodsearch/-------------.html?wd={wd}&submit=';
//$searchtype=2的网址
//$search='https://www.jpysvip.net/index.php/ajax/suggest?mid=1&wd={wd}&limit=10';
//通用模板 $web=前面设置的网址
$search=$web.'/index.php/ajax/suggest?mid=1&wd={wd}&limit=10';

//htm代码分析用xpath取影片，取出影片ID的文本左边
//$searchurl1='/voddetail/';
$searchurl1='';

//htm代码分析用xpath取影片，取出影片ID的文本右边
//$searchurl2='.html';
//json直接返回影片ID，不用取值
$searchurl2='';

//xpath列表
//$searchquery='//ul[@class="myui-vodlist__media clearfix"]/li/div/a';
//json路径
$searchquery='list';

//xpath规则取出影片的标题
//$searchtitleAttr='//ul[@class="myui-vodlist__media clearfix"]/li/div/a/@title';
//json取影片标题
$searchtitleAttr='name';

//xpath规则取出影片的链接
//$searchlinkAttr='//ul[@class="myui-vodlist__media clearfix"]/li/div/a/@href';
//json取出影片的ID
$searchlinkAttr='id';

//xpath规则取影视更新情况 例如：更新至*集
//$searchquery2 = '//ul[@class="myui-vodlist__media clearfix"]/li/div/a/span[@class="pic-text text-right"]';
//json取影视更新情况 极品没有更新情况，所以留空
$searchquery2 ='';
//===============================================影视搜索相关配置结束===========================
//==============================================仅需修改以上代码↑=======================================








//==============================================以下内容的代码无需修改↓=======================================
$weburl='http://'.$_SERVER['HTTP_HOST'].$_SERVER['PHP_SELF'];
if ($typeid<> null && $page<>null){
//==============================================读取影视列表开始=======================================
$catname ='';
$arr=json_decode($movietype,true);
$arr_q1a=$arr['class'];
$m=count($arr_q1a);
 for($i=0;$i<$m;$i++){
 $type_id = $arr_q1a[$i]["type_id"];
 if($typeid==$type_id){
  $catname =  $arr_q1a[$i]["catname"];
  break;
 }
 }
if($catname==null){
$liebiao=str_replace("{typeid}",$typeid,$liebiao);
}else{
$liebiao=str_replace("{typeid}",$catname,$liebiao);
}
$liebiao=str_replace("{pageid}",$page,$liebiao);
$html = curl_get($liebiao,$gettype,$cookie);
$dom = new DOMDocument();
$html= mb_convert_encoding($html ,'HTML-ENTITIES',"UTF-8");
$dom->loadHTML($html);
$dom->normalize();
$xpath = new DOMXPath($dom);
$texts = $xpath->query($query2);
if($texts->length==0 && $query3<>null){
$texts = $xpath->query($query3);
}
$events = $xpath->query($query);
$picevents = $xpath->query($picAttr);
if ($picevents->length==0 && $picAttr2<>null){
$picevents = $xpath->query($picAttr2);
}
$titleevents= $xpath->query($titleAttr);
$linkevents= $xpath->query($linkAttr);
$length=$events->length;
$guolv='';
if ($adable==1 && $page==1){
$length=$length+1;
}
if ($length<$num)
{
$page2=$page;
}else{
$length=$length+1;
$page2=$page + 1;
}
$result='{"code":1,"page":'.$page.',"pagecount":'. $page2 .',"total":'. $length.',"list":[';
if ($adable==1 && $page==1){
    $result=$result.'{"vod_id":1,"vod_name":"'.$adtitle1.'","vod_pic":"'.$adpicurl.'","vod_remarks":"'.$adtitle2.'"},';
}
for ($i = 0; $i < $events->length; $i++) {
    $event = $events->item($i);
    $text = $texts->item($i)->nodeValue;
    $text = replacestr($text);
    $link = $linkevents->item($i)->nodeValue;
    $title = $titleevents->item($i)->nodeValue;
    $title = replacestr($title);
    $pic = $picevents->item($i)->nodeValue;
      if($url1<>null){
       $link2 =getSubstr($link,$url1,$url2);
    }else{
    $link2 =$link;
    }
    
    	if (substr($pic,0,2)=='//'){
	$pic = 'http:'.$pic;
	}else if (substr($pic,0,4)<>'http' && $pic<>null){
	$pic = $web.$pic;
	}

if($guolv==null){
	    $result=$result.'{"vod_id":"'.$link2.'","vod_name":"'.$title.'","vod_pic":"'.$pic.'","vod_remarks":"'.$text.'"},';
    	$guolv=$guolv."{".$link2."}";
}else if(strpos($guolv, "{".$link2."}")==0){
	    $result=$result.'{"vod_id":"'.$link2.'","vod_name":"'.$title.'","vod_pic":"'.$pic.'","vod_remarks":"'.$text.'"},';
    	$guolv=$guolv."{".$link2."}";
	}
 
}

$result=substr($result, 0, strlen($result)-1).']}';
echo $result;
//==============================================读取影视列表结束=======================================
}else if ($ids<> null){

//==============================================读取影视信息开始=======================================
$detail=str_replace("{vodid}",$ids,$detail);
$html = curl_get($detail,$gettype,$cookie);
$dom = new DOMDocument();
$html= mb_convert_encoding($html ,'HTML-ENTITIES',"UTF-8");
$dom->loadHTML($html);
$dom->normalize();
$xpath = new DOMXPath($dom);
if($vodtitle<>null){
$texts = $xpath->query($vodtitle);
$text = $texts->item(0)->nodeValue;
$text = replacestr($text);
}
if($vodtype<>null){
$texts = $xpath->query($vodtype);
$type = $texts->item(0)->nodeValue;
$type = replacestr($type);
}
if($vodtext<>null){
$texts = $xpath->query($vodtext);
$vodtext2 = $texts->item(0)->nodeValue;
$vodtext2 = replacestr($vodtext2);
}
if($vodyear<>null){
$texts = $xpath->query($vodyear);
$year = $texts->item(0)->nodeValue;
$year = replacestr($year);
}
if($vodimg<>null){
$texts = $xpath->query($vodimg);
$img = $texts->item(0)->nodeValue;

	if (substr($img,0,2)=='//'){
	$img = 'http:'.$img;
	}else if (substr($img,0,4)<>'http' && $img<>null){
	$img = $web.$img;
	}
}
if($img==null){
$img =$historyimg;
}
if($vodarea<>null){
$texts = $xpath->query($vodarea);
$area = $texts->item(0)->nodeValue;
$area = replacestr($area);
}
if($vodactor<>null){
$texts = $xpath->query($vodactor);
$actor ='';
for ($i = 0; $i < $texts->length; $i++) {
    $event1 = $texts->item($i);
    $actor = $actor.$event1->nodeValue.' ';
}
}
if($voddirector<>null){
$texts = $xpath->query($voddirector);
$director ='';
for ($i = 0; $i < $texts->length; $i++) {
    $event1 = $texts->item($i);
    $director = $director.$event1->nodeValue.' ';
}
}

$result='{"list":[{"vod_id":"'.$ids.'",';
if($text<>null){
$result=$result.'"vod_name":"'.$text.'",';
}
if($text==null){
$result=$result.'"vod_name":"'."片名获取失败".'",';
}
if($img<>null){
$result=$result.'"vod_pic":"'.$img.'",';
}
if($type<>null){
$result=$result.'"type_name":"'.$type.'",';
}

if($year<>null){
$result=$result.'"vod_year":"'.$year.'",';
}

if($actor<>null){
$result=$result.'"vod_actor":"'.$actor.'",';
}

if($director<>null){
$result=$result.'"vod_director":"'.$director.'",';
}
if($area<>null){
$result=$result.'"vod_area":"'.$area.'",';
}
if($vodtext2<>null){
$vodtext2=str_replace('"','\"',$vodtext2);
$result=$result.'"vod_content":"'.$vodtext2.'",';
}

$yuan = '';
$dizhi = '';

$text1 = $xpath->query($playname);
$text2 = $xpath->query($playurl);

if($text2->length==0){
$result= $result.'"vod_play_from":"'."原页面".'",';
$result= $result.'"vod_play_url":"'.$detail.'"}]}';
}else{
if($playname<>null){
for ($i = 0; $i < $text2->length; $i++) {
    $event1 = $text1->item($i);
    $bfyuan = $event1->nodeValue;
    $bfyuan = replacestr($bfyuan);
    $yuan = $yuan.$bfyuan.'$$$';
}
}

if($yuan==''){
for ($i = 0; $i < $text2->length; $i++) {
    $bfyuan =$i+1;
    $yuan = $yuan."播放源".$bfyuan.'$$$';
}
}
foreach ($text2 as $oObject) {
$dizhi2 = '';
        foreach($oObject->childNodes as $col){
        if ($col->hasChildNodes()){
            $link4 = $col->getAttribute($linkAttr5);
            if($link4<>null){
            $text4 = $col->nodeValue;
            $text4 = replacestr($text4);
	         if (substr($link4,0,4)<>'http' && $link4<>null){
	        $link4 = $web.$link4;
	        }
	        if($zhilian==1){
        $dizhi2 = $dizhi2.$text4.'$'.$weburl.'?url='.$link4.'#';
        }else{
        $dizhi2 = $dizhi2.$text4.'$'.$link4.'#';
        }
        }else{          
            foreach($col->childNodes as $col2){
            if ($col2->hasChildNodes()){
             $link4 = $col2->getAttribute($linkAttr5);
            if($link4<>null){
            $text4 = $col2->nodeValue;
            $text4 = replacestr($text4);
	         if (substr($link4,0,4)<>'http' && $link4<>null){
	        $link4 = $web.$link4;
	        }
          if($zhilian==1){
        $dizhi2 = $dizhi2.$text4.'$'.$weburl.'?url='.$link4.'#';
        }else{
        $dizhi2 = $dizhi2.$text4.'$'.$link4.'#';
        }
        }else{
           foreach($col2->childNodes as $col3){
            if ($col3->hasChildNodes()){
             $link4 = $col3->getAttribute($linkAttr5);
            if($link4<>null){
            $text4 = $col3->nodeValue;
            $text4 = replacestr($text4);
	         if (substr($link4,0,4)<>'http' && $link4<>null){
	        $link4 = $web.$link4;
	        }
          if($zhilian==1){
        $dizhi2 = $dizhi2.$text4.'$'.$weburl.'?url='.$link4.'#';
        }else{
        $dizhi2 = $dizhi2.$text4.'$'.$link4.'#';
        }
        }else{
           foreach($col3->childNodes as $col4){
            if ($col4->hasChildNodes()){
             $link4 = $col4->getAttribute($linkAttr5);
            if($link4<>null){
            $text4 = $col4->nodeValue;
            $text4 = replacestr($text4);
	         if (substr($link4,0,4)<>'http' && $link4<>null){
	        $link4 = $web.$link4;
	        }
          if($zhilian==1){
        $dizhi2 = $dizhi2.$text4.'$'.$weburl.'?url='.$link4.'#';
        }else{
        $dizhi2 = $dizhi2.$text4.'$'.$link4.'#';
        }
        }else{
        foreach($col4->childNodes as $col5){
            if ($col5->hasChildNodes()){
             $link4 = $col5->getAttribute($linkAttr5);
            if($link4<>null){
            $text4 = $col5->nodeValue;
            $text4 = replacestr($text4);
	         if (substr($link4,0,4)<>'http' && $link4<>null){
	        $link4 = $web.$link4;
	        }
          if($zhilian==1){
        $dizhi2 = $dizhi2.$text4.'$'.$weburl.'?url='.$link4.'#';
        }else{
        $dizhi2 = $dizhi2.$text4.'$'.$link4.'#';
        }}}}}}}}}}}}}}}}
        if($dizhi2==null){
                $dizhi=$dizhi.'无播放地址 请更换其他源$http'.'$$$';
        }else{
                $dizhi=$dizhi.substr($dizhi2, 0, strlen($dizhi2)-1).'$$$';
        }
    }
$result= $result.'"vod_play_from":"'.substr($yuan, 0, strlen($yuan)-3).'",';
$result= $result.'"vod_play_url":"'.substr($dizhi, 0, strlen($dizhi)-3).'"}]}';
}
echo $result;
//==============================================读取影视信息结束=======================================



}else  if ($burl<> null){

//=============================以下是直链分析代码=======================================================
$html = curl_get($burl,$gettype,$cookie);
$content=getSubstr($html,'var player','</script>');
$content=getSubstr($content,'"url":"','",');
$content=str_replace("\/","/",$content);

if(strpos($content,"qq.com")>0 or strpos($content,"youku.com")>0 or strpos($content,"iqiyi.com")>0 or strpos($content,"mgtv.com")>0){
$content=$vip.$content;
}
echo  '<iframe src="'.$content.'" class="iframeStyle" id="myiframe" ></iframe>';
//==============================以上是直链分析代码=======================================================



}else  if ($wd<> null){
//=============================以下是搜索代码=======================================================
if($searchable==0){
echo 'php中未开启搜索';
exit;
}
if($page==null){
$page=1;
}
$geturl =str_replace("{wd}",urlencode($wd),$search);
$geturl =str_replace("{page}",$page,$geturl);
$html = curl_get($geturl,$gettype,$cookie);
if($searchtype==1){
$dom = new DOMDocument();
$html= mb_convert_encoding($html ,'HTML-ENTITIES',"UTF-8");
$dom->loadHTML($html);
$dom->normalize();
$xpath = new DOMXPath($dom);
$texts = $xpath->query($searchquery2);
$events = $xpath->query($searchquery);
$titleevents= $xpath->query($searchtitleAttr);
$linkevents= $xpath->query($searchlinkAttr);
$length=$events->length;
$result='{"code":1,"page":'.$page.',"pagecount":'. $page.',"total":'. $length.',"list":[';
for ($i = 0; $i < $events->length; $i++) {
    $event = $events->item($i);
    $text = $texts->item($i)->nodeValue;
    $link = $linkevents->item($i)->nodeValue;
    $title = $titleevents->item($i)->nodeValue;
    if($searchurl1<>null){
        $link2 =getSubstr($link,$searchurl1,$searchurl2);
    }else{
    $link2 =$link;
    }
    $result=$result.'{"vod_id":'.$link2.',"vod_name":"'.$title.'","vod_remarks":"'.$text.'"},';
}
$result=substr($result, 0, strlen($result)-1).']}';
echo $result;
}else{
$arr=json_decode($html,true);
$arr_q1a=$arr[$searchquery];
$m=count($arr_q1a);
$result='{"code":1,"page":'.$page.',"pagecount":'. $page.',"total":'. $m.',"list":[';
 for($i=0;$i<$m;$i++){
 $title = $arr_q1a[$i][$searchtitleAttr];
$link =  $arr_q1a[$i][$searchlinkAttr];
if($searchquery2<>null){
$text = $arr_q1a[$i][$searchquery2];
$result=$result.'{"vod_id":'.$link.',"vod_name":"'.$title.'","vod_remarks":"'.$text.'"},';
}else{
$result=$result.'{"vod_id":'.$link.',"vod_name":"'.$title.'"},';
}
 }
 $result=substr($result, 0, strlen($result)-1).']}';
echo $result;
}
//==============================以上是搜索代码=======================================================
}else{
echo $movietype;
}

function curl_get($url,$gettype2,$cookie2){
  // $header = array(
    //   'User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36',
    //);
   if($gettype2==2){
    $data = file_get_contents($url);
    	return $data;
    }else{
        $curl = curl_init();
    curl_setopt($curl, CURLOPT_URL, $url);
    curl_setopt($curl, CURLOPT_HEADER, 0);
    curl_setopt($curl, CURLOPT_TIMEOUT, 20);
    curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, false);
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST, false);
    curl_setopt($curl, CURLOPT_USERAGENT, $UserAgent);
    if($cookie2<>null){
    curl_setopt($curl, CURLOPT_COOKIE, $cookie2);
    }
    $ip=Rand_IP();
    curl_setopt($curl, CURLOPT_HTTPHEADER, array('X-FORWARDED-FOR:'.$ip, 'CLIENT-IP:'.$ip));
    $data = curl_exec($curl);
    if (curl_error($curl)) {
        return "Error: ".curl_error($curl);
    } else {
	curl_close($curl);
	return $data;
    }
    }
}

function Rand_IP(){
    $ip2id= round(rand(600000, 2550000) / 10000); 
    $ip3id= round(rand(600000, 2550000) / 10000);
    $ip4id= round(rand(600000, 2550000) / 10000);
    $arr_1 = array("218","218","66","66","218","218","60","60","202","204","66","66","66","59","61","60","222","221","66","59","60","60","66","218","218","62","63","64","66","66","122","211");
    $randarr= mt_rand(0,count($arr_1)-1);
    $ip1id = $arr_1[$randarr];
    return $ip1id.".".$ip2id.".".$ip3id.".".$ip4id;
}


function getSubstr($str, $leftStr, $rightStr) 
{
if($leftStr<>null && $rightStr<>null){
$left = strpos($str, $leftStr);
$right = strpos($str, $rightStr,$left+strlen($left));
if($left < 0 or $right < $left){
return '';
}
return substr($str, $left + strlen($leftStr),$right-$left-strlen($leftStr));
}else{
$str2=$str;
if($leftStr<>null){
$str2=str_replace($leftStr,'',$str2);
}
if($rightStr<>null){
$str2=str_replace($rightStr,'',$str2);
}
return $str2;
}
}

function replacestr($str2){
$test2=$str2;
$test2=str_replace("	","",$test2);
$test2=str_replace(" ","",$test2);
$test2 = preg_replace('/\s*/', '', $test2);
return $test2;
}
?>