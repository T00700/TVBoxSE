var rule={
    title:'厂长资源',
    host:'https://www.czspp.com',
    url:'/fyclass/page/fypage',
    searchUrl:'/xssearch?q=**&f=_all&p=fypage',
    searchable:2,//是否启用全局搜索,
    quickSearch:0,//是否启用快速搜索,
    filterable:0,//是否启用分类筛选,
    headers:{
        'User-Agent':'UC_UA',
    },
    // class_parse:'.fed-pops-navbar&&ul.fed-part-rows&&a.fed-part-eone:gt(0):lt(5);a&&Text;a&&href;.*/(.*?).html',
    //class_parse:'.top_nav&&ul li:gt(0):lt(25);a&&Text;a&&href;.*/(.*?).html',
    class_name:'dbtop250&最新电影&电视剧&国产剧&美剧&韩剧&番剧&动漫',
    class_url:'dbtop250&zuixindianying&dsj&gcj&meijutt&hanjutv&fanju&dm',
    play_parse:true,
    lazy:'',
    limit:6,
    推荐:'.bt_img&&ul&&li;h3&&Text;img&&data-original;.jidi&&Text;a&&href',
    double:true, // 推荐内容是否双层定位
    一级:'.bt_img&&ul&&li;h3&&Text;img&&data-original;.jidi&&Text;a&&href',
    二级:{"title":"h1&&Text;.moviedteail_list li:eq(0)&&Text","img":".dyimg.fl .vodlist_thumb&&data-original","desc":".content_detail.content_min.fl li:eq(0)&&Text;.content_detail.content_min.fl li:eq(2)&&Text;.content_detail.content_min.fl li:eq(3)&&Text","content":".yp_context","tabs":".ypxingq_t;span","lists":".paly_list_btn:eq(#id) a"},
    搜索:'.bt_img&&ul&&li;h3&&Text;img&&data-original;.jidi&&Text;a&&href',
}
