# LibBaiduTextToSpeech
百度语音合成 组件库

# 如何使用
一、在app项目build.gradle文件中加入组件库依赖
```gradle
implementation 'com.deity.texttospeech:library:1.0.1'
```

二、推荐在application中初始化组件库
其中appId/appKey/secretKey 请自行上[百度开发者平台](https://ai.baidu.com/tech/speech/tts)进行申请
```java
public class CustomApplication extends Application{
  public static CustomApplication instance;
  
  public void onCreate() {
    ...
    instance = this;
    AuthEntity entity = new AuthEntity("appId","appKey","secretKey");
    baiduTextToSpeech = BaiduTextToSpeech.getInstance(this,entity);
  }
  
  public CustomApplication getInstance(){
    return instance;
  }

  public BaiduTextToSpeech getBaiduTextToSpeech() {
      return baiduTextToSpeech;
  }
}
```

三、语音合成
```java
CustomApplication.getInstance().getBaiduTextToSpeech().speak("欢迎使用百度语音合成"));
```

扫码体验Demo

![扫码体验](screenshot/TIM图片20190802091614.png)


# 有好的建议欢迎PR
