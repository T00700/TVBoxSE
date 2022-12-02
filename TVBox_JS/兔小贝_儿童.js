import './dayjs.min.js'
import './uri.min.js';
import './crypto-js.js'

let rule = {
    title:'兔小贝',
    host:'https://www.tuxiaobei.com',
    homeUrl:'',
    url:'/list/mip-data?typeId=fyclass&page=fypage&callback=',
    detailUrl:'/play/fyid',
    searchUrl:'/search/index?key=**',
    searchable:2,
    headers:{
        'User-Agent':'MOBILE_UA'
    },
    timeout:5000,
    class_url:'2&3&4&25',
    class_name:'儿歌&故事&国学&启蒙',
    //class_name:'#page-viewport&&ul&&li;.text&&Text;a&&href;/(.*)',
    cate_exclude:'应用',
    推荐:'.pic-list.list-box;.items;.text&&Text;mip-img&&src;.all&&Text;a&&href',
    double:true,
    limit:5,
    play_parse:true,
    lazy:'js:fetch_params.headers["user-agent"] = IOS_UA;let html=fetch(input,fetch_params);let src = jsp.pdfh(html,"body&&#videoWrap&&video-src");input=src;',
    // 一级:'json:data.items;name;image;collect_num;category_id+video_id',
    一级:'json:data.items;name;image;duration_string;video_id',
    二级:'*',
    搜索:'.list-con&&.items;.text&&Text;mip-img&&src;.time&&Text;a&&href',
}

function init(ext) {
}

function home(filter) {
    ... 算了
}

function homeVod(params) {
}

function category(tid, pg, filter, extend) {
}

function detail(id) {
}

function play(flag, id, flags) {
}

function search(wd, quick) {
}

export default {
    init: init,
    home: home,
    homeVod: homeVod,
    category: category,
    detail: detail,
    play: play,
    search: search
}