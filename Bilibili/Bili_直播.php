<?php
    $id = $_GET['id'];
    //$id = '5854051'; // for test
    $bstrURL = 'https://live.bilibili.com/'.$id;
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $bstrURL);                  
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE);
    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, FALSE);
    curl_setopt($ch, CURLOPT_USERAGENT, 'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36');
    $data = curl_exec($ch);
    curl_close($ch);
    preg_match_all('/base_url":"(.*?)",/i',$data,$base_url);
    preg_match_all('/host":"(.*?)",/i',$data,$host);
    preg_match_all('/extra":"(.*?)",/i',$data,$extra);
    $base_url = decodeUnicode($base_url[1][0]);
    $host = decodeUnicode($host[1][0]);
    $extra = $extra[1][0];
    $url = $host.''.$base_url.''.$extra;
    header('location:'.$url);        
        
    function decodeUnicode($str)
    {
           return preg_replace_callback('/\\\\u([0-9a-f]{4})/i', function ($matches) {
           return mb_convert_encoding(pack("H*", $matches[1]), "UTF-8", "UCS-2BE");
                }, $str);
     }
?>