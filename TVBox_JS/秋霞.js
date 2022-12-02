var rule = {
title:'秋霞',
host:'https://www.7xiady.cc',
url:'/index.php/type/fyclass-fypage/',
//class_parse:'.stui-header__menu&&ul&&li;a&&Text;a&&href;/type/(.*?)/',
searchUrl:'/search/**----------fypage---.html',
    searchable:2,//是否启用全局搜索,
    quickSearch:0,//是否启用快速搜索,
    filterable:0,//是否启用分类筛选,
    headers:{
        'User-Agent':'UC_UA',
        // "Cookie": ""
    },
   class_name:'电影&连续剧&综艺&动漫',
     class_url:'dianying&lianxuju&zongyi&dongman',
   // class_parse:'.stui-header__menu li:gt(0):lt(7);a&&Text;a&&href;.*/(.*?).html',
    play_parse:true,
    lazy:'',
    limit:6,
    推荐:'ul.stui-vodlist.clearfix;li;a&&title;.lazyload&&data-original;.pic-text&&Text;a&&href',
    double:true, // 推荐内容是否双层定位
    一级:'.stui-vodlist li;a&&title;a&&data-original;.pic-text&&Text;a&&href',
    二级:{"title":".stui-content__detail .title&&Text;.stui-content__detail p:eq(-2)&&Text","img":".stui-content__thumb .lazyload&&data-original","desc":".stui-content__detail p:eq(0)&&Text;.stui-content__detail p:eq(1)&&Text;.stui-content__detail p:eq(2)&&Text","content":".stui-content__desc&&Text","tabs":".stui-pannel__head.bottom-line.active.clearfix h3","lists":".stui-content__playlist:eq(#id) li"},
  搜索:'ul.stui-vodlist&&li;a&&title;.lazyload&&data-original;.pic-text&&Text;a&&href'}