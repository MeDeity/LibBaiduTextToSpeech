package com.deity.texttospeech;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.deity.texttospeech.data.AuthEntity;
import com.deity.texttospeech.data.InitConfig;
import com.deity.texttospeech.data.MessageListener;
import com.deity.texttospeech.utils.AutoCheck;
import com.deity.texttospeech.utils.OfflineResource;
import com.deity.texttospeech.data.Params;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * TTS使用入口
 * Create by fengwenhua at 2018/8/9
 **/
public class BaiduTextToSpeech {
    private static final String TAG = BaiduTextToSpeech.class.getSimpleName();

    private static final int INIT = 1;

    private static final int RELEASE = 11;

    private static BaiduTextToSpeech baiduTextToSpeech;
    /**百度语音合成器*/
    private SpeechSynthesizer mSpeechSynthesizer;
    private HandlerThread initHandleThread;
    private Handler initHandler;
    private Context context;


    private OfflineResource createOfflineResource(Context context, String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(context, voiceType);
        } catch (IOException e) {
            // IO 错误自行处理
            Log.d(TAG,"【error】:copy files from assets failed." + e.getMessage());
        }
        return offlineResource;
    }

    private void initBaiduTextToSpeechHandleThread(final AuthEntity entity, final Map<String, String> params){
        initHandleThread = new HandlerThread("initHandleThread");
        initHandleThread.start();
        initHandler = new Handler(initHandleThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case INIT:
                        initBaiduTextToSpeech(context,entity,params);
                        break;
                    case RELEASE:
                        break;
                    default:
                        break;
                }
            }
        };
        initHandler.sendEmptyMessage(INIT);
    }

    private void checkResult(int result, String method) {
        if (result != 0) {
            System.err.println("error code :" + result + " method:" + method);
        }
    }

    private void initBaiduTextToSpeech(Context context, AuthEntity entity,Map<String, String> params){
        WeakReference<Context> weakReference = new WeakReference<>(context);
        // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
        OfflineResource offlineResource = createOfflineResource(weakReference.get(), Params.offlineVoice);
        // 1. 获取实例
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(weakReference.get());
        // 2. 设置listener
        SpeechSynthesizerListener listener = new MessageListener();
        mSpeechSynthesizer.setSpeechSynthesizerListener(listener);
        // 3. 设置appId，appKey.secretKey
        int result = mSpeechSynthesizer.setAppId(entity.getAppId());
        Log.d(TAG,"setAppId:"+result);
        checkResult(result, "setAppId");
        result = mSpeechSynthesizer.setApiKey(entity.getAppKey(), entity.getSecretKey());
        Log.d(TAG,"setApiKey:"+result);
        checkResult(result, "setApiKey");
        // 4. 支持离线的话，需要设置离线模型
        if (Params.ttsMode.equals(TtsMode.MIX)) {
            // 检查离线授权文件是否下载成功，离线授权文件联网时SDK自动下载管理，有效期3年，3年后的最后一个月自动更新。
            if (!checkAuth()) {
                return;
            }
            // 文本模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, Params.TEXT_FILENAME);
            // 声学模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, Params.MODEL_FILENAME);
        }


        // 5. 以下setParam 参数选填。不填写则默认值生效
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "4");
        // 设置合成的音量，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);

        if (null!=params){//setParam 参数选填。不填写则默认值生效
            for (Map.Entry<String, String> entry:params.entrySet()){
                mSpeechSynthesizer.setParam(entry.getKey(),entry.getValue());
            }
        }
        mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_IN_CALL);
        // 不使用压缩传输
        // mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_ENCODE, SpeechSynthesizer.AUDIO_ENCODE_PCM);
        // mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_RATE, SpeechSynthesizer.AUDIO_BITRATE_PCM);

        // x. 额外 ： 自动so文件是否复制正确及上面设置的参数
        Map<String, String> checkParams = new HashMap<>();
        // 复制下上面的 mSpeechSynthesizer.setParam参数
        if (Params.ttsMode.equals(TtsMode.MIX)) {
            checkParams.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, Params.TEXT_FILENAME);
            checkParams.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, Params.MODEL_FILENAME);
        }
        InitConfig initConfig =  new InitConfig(entity.getAppId(), entity.getAppKey(), entity.getSecretKey(), Params.ttsMode, checkParams, listener);
        AutoCheck.getInstance(weakReference.get()).check(initConfig, new Handler() {
            @Override
            public void handleMessage(Message msg) {//开新线程检查，成功后回调
                if (msg.what == 100) {
                    AutoCheck autoCheck = (AutoCheck) msg.obj;
                    synchronized (autoCheck) {
                        String message = autoCheck.obtainDebugMessage();
                        Log.w(TAG, message);
                    }
                }
            }

        });
        // 6. 初始化
        result = mSpeechSynthesizer.initTts(Params.ttsMode);
        Log.d(TAG, "initTts:"+result);
        checkResult(result, "initTts");
    }

    private BaiduTextToSpeech(Context context,AuthEntity entity,Map<String, String> params){
        this.context = context;
        initBaiduTextToSpeechHandleThread(entity,params);
    }

//    private static class NoLeakHandler extends Handler{
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//        }
//    }


    /**
     * 获取唯一实例
     * @param context 上下文
     * @param params  参数内容
     * @return  实例
     */
    public static BaiduTextToSpeech getInstance(Context context,AuthEntity entity,Map<String, String> params){
        if (null==baiduTextToSpeech){
            synchronized (BaiduTextToSpeech.class){
                if (null==baiduTextToSpeech){
                    baiduTextToSpeech = new BaiduTextToSpeech(context,entity,params);
                }
            }
        }
        return baiduTextToSpeech;
    }

    public static BaiduTextToSpeech getInstance(Context context,AuthEntity entity){
        if (null==baiduTextToSpeech){
            synchronized (BaiduTextToSpeech.class){
                if (null==baiduTextToSpeech){
                    baiduTextToSpeech = new BaiduTextToSpeech(context,entity,null);
                }
            }
        }
        return baiduTextToSpeech;
    }

    /**
     * 调用该方法进行语音输出
     * @param message 文本内容
     */
    public void speak(String message){
        if (mSpeechSynthesizer == null) {
            Log.d(TAG,"[ERROR], 初始化失败");
            return;
        }
        int result = mSpeechSynthesizer.speak(message);
        Log.d(TAG, "speak:"+result);
    }


    /**
     * 检查appId ak sk 是否填写正确，另外检查官网应用内设置的包名是否与运行时的包名一致。本demo的包名定义在build.gradle文件中
     * 离线授权需要网站上的应用填写包名。本demo的包名是com.baidu.tts.sample，定义在build.gradle中
     * @return 是否授权成功
     */
    private boolean checkAuth() {
        AuthInfo authInfo = mSpeechSynthesizer.auth(Params.ttsMode);
        if (!authInfo.isSuccess()) {
            //
            String errorMsg = authInfo.getTtsError().getDetailMessage();
            Log.d(TAG,"【error】鉴权失败 errorMsg=" + errorMsg);
            return false;
        } else {
            Log.d(TAG,"验证通过，离线正式授权文件存在。");
            return true;
        }
    }


    /**释放资源*/
    public void onDestroy() {
        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.stop();
            mSpeechSynthesizer.release();
            mSpeechSynthesizer = null;
        }
        if (null!=baiduTextToSpeech){
            baiduTextToSpeech = null;
        }
    }


}
