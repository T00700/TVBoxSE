# TVBoxOSC
不懂java代码，在开源项目中做一些常见的小修改。
改APP名字，版本号，替换app图标，替换背景图片，内置源。共存（com.github.tvbox.osc）。

![9c47ad0a6e7445b825be058e22888b4](https://user-images.githubusercontent.com/102397160/177658190-7863e8bb-619f-4ef3-88b6-2cb5a0c9084b.png)

改APP版本号:TVBoxOS-junlao/app/build.gradle 　　　　　　versionName '2.0.0'

![7866862a3f3636c9d57527ce175057c](https://user-images.githubusercontent.com/102397160/177658195-ca687aed-427a-4443-af35-df92240729bb.png)
![fe07333d50600375c49612855018fdd](https://user-images.githubusercontent.com/102397160/177658197-ae104e2c-66d9-4e5f-9ea9-60675323574b.png)
![微信图片_20220728040003](https://user-images.githubusercontent.com/102397160/181361340-3d6f361c-8199-4a2b-a445-786ec9dad028.jpg)
调整设置画面选项的位置方法：打开，两块复制调换一下就行。

![微信图片_20220728040329](https://user-images.githubusercontent.com/102397160/181361920-cdcdffa7-5cd0-4b10-af2d-e850d59ae219.png)

修改内置源

俊老仓库打开下面,第83行
https://github.com/dabbing2019/TVBoxOS/blob/main/app/src/main/java/com/github/tvbox/osc/api/ApiConfig.java

takagen99大佬仓库 改这里;app/src/main/res/values-zh/strings.xml

修改默认缩略图、硬解、dns
地址：

https://github.com/dabbing2019/TVBoxOS/blob/main/app/src/main/java/com/github/tvbox/osc/base/App.java

代码

@@ -53,9 +53,19 @@ private void initParams() 
{
       
       if (!Hawk.contains(HawkConfig.PLAY_TYPE)) 
        
        {
            Hawk.put(HawkConfig.PLAY_TYPE, 1);
            
        }
        
        //自定义默认配置-硬解-安全dns-缩略图
        
        if (!Hawk.contains(HawkConfig.IJK_CODEC)) 
        
        {
            Hawk.put(HawkConfig.IJK_CODEC, "硬解码");
        }
        
        if (!Hawk.contains(HawkConfig.DOH_URL)) 
        
        {
            Hawk.put(HawkConfig.DOH_URL, 2);
        }
        
        if (!Hawk.contains(HawkConfig.SEARCH_VIEW))
        
        {
            Hawk.put(HawkConfig.SEARCH_VIEW, 2);
        }
    }

    public static App getInstance() 
    
    {
        return instance;
    }
