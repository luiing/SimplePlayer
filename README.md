# SimplePlayer
    AD Player simulate Tmall(广告播放器，模仿天猫，支持全屏，支持在线和离线播放，支持RecyclerView)

### 手机自带的MediaPlayer，支持http,https,rtsp

### 播放器原理
    1.只有唯一的MediaPlayer和Surface在工作,切换视频会复用，内存消耗非常小
    3.Surface创建使用的是Application.Context,Actiivty关闭不会对其持有引用
    3.全屏切换会复用Surface防止卡顿，MediaPlayer随url变化，只保持一个
    4.全屏切换通过Windows.ID_ANDROID_CONTENT 加上一个viewgroup
        a.全屏横屏
        ViewGroup.setSystemUiVisibility(SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        Activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        b.退出横屏
        ViewGroup.setSystemUiVisibility(SYSTEM_UI_FLAG_VISIBLE);
        Activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

# Use
    compile 'com.uis:lib-simpleplayer:0.2.0'
# Version
    0.0.2 init library
    0.0.5 fixed Databinding Confilect,make demo like Tmall
    0.0.6 fixed fullscreen hide the thumb picture
    0.0.7 fixed screen change,make video resize
    0.1.4 fixed fullscreen bug, add network dnymic changed,add thumb scaleType and holder
    0.1.6 fixed全屏小画面，支持销毁PlayerView
    0.1.8 fixed全屏小画面不能自适应，自动销毁资源
    0.2.0 stable
# 效果图
![image](/snapshot/2017-1.png)

![image](/snapshot/2017-2.png)

![image](/snapshot/2017-3.png)

![image](/snapshot/2017-11.png)

![image](/snapshot/2017-12.png)

![image](/snapshot/device-2017-002.png)

![image](/snapshot/device-2017-003.png)