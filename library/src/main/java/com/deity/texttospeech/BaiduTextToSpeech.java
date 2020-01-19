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
import com.deity.texttospeech.callback.InitCallback;
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
    /**Baidu speech synthesizer*/
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
            Log.d(TAG,"[error]:copy files from assets failed." + e.getMessage());
        }
        return offlineResource;
    }

    private void initBaiduTextToSpeechHandleThread(final AuthEntity entity, final Map<String, String> params, final InitCallback initCallback){
        initHandleThread = new HandlerThread("initHandleThread");
        initHandleThread.start();
        initHandler = new Handler(initHandleThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case INIT:
                        initBaiduTextToSpeech(context,entity,params,initCallback);
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

    private void initBaiduTextToSpeech(Context context, AuthEntity entity,Map<String, String> params,InitCallback initCallback){
        WeakReference<Context> weakReference = new WeakReference<>(context);
        // Offline resource files, copied from the assets directory to the temporary directory, need to be completed before the initTTs method
        OfflineResource offlineResource = createOfflineResource(weakReference.get(), Params.offlineVoice);
        // 1. Get instance
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(weakReference.get());
        // 2. Set listener
        SpeechSynthesizerListener listener = new MessageListener();
        mSpeechSynthesizer.setSpeechSynthesizerListener(listener);
        // 3. Set appId, appKey.secretKey
        int result = mSpeechSynthesizer.setAppId(entity.getAppId());
        Log.d(TAG,"setAppId:"+result);
        checkResult(result, "setAppId");
        result = mSpeechSynthesizer.setApiKey(entity.getAppKey(), entity.getSecretKey());
        Log.d(TAG,"setApiKey:"+result);
        checkResult(result, "setApiKey");
        // 4. If you support offline, you need to set offline model
        if (Params.ttsMode.equals(TtsMode.MIX)) {
            // Check whether the offline authorization file is downloaded successfully. The SDK automatically downloads and manages the offline authorization file when it is connected to the network. The validity period is 3 years, and it is automatically updated in the last month after 3 years.
            AuthInfo auth = checkAuth();
            if (!auth.isSuccess()) {
                String errorMsg = auth.getTtsError().getDetailMessage();
                initCallback.result(-1,"Authentication failed:"+errorMsg);
                return;
            }
            // Text model file path (for offline engines), note that TEXT_FILENAME must exist and be readable
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, Params.TEXT_FILENAME);
            // Acoustic model file path (for offline engines), note that TEXT_FILENAME must exist and be readable
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, Params.MODEL_FILENAME);
        }


        // 5. The following setParam parameters are optional. If not filled, the default value will take effect
        // Set online voice: 0 common female voice (default) 1 common male voice 2 special male voice 3 emotional male voice 4 emotional child voice
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // Set composition volume, 0-9, default 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");
        //Set synthetic speech rate, 0-9, default 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        // Set synthetic intonation, 0-9, default 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);

        if (null!=params){//setParam Optional. If not filled, the default value will take effect
            for (Map.Entry<String, String> entry:params.entrySet()){
                mSpeechSynthesizer.setParam(entry.getKey(),entry.getValue());
            }
        }
        mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_IN_CALL);
        // No compressed transmission
        // mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_ENCODE, SpeechSynthesizer.AUDIO_ENCODE_PCM);
        // mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_RATE, SpeechSynthesizer.AUDIO_BITRATE_PCM);

        // x. Extra: whether the automatic so file is copied correctly and the parameters set above
        Map<String, String> checkParams = new HashMap<>();
        // Copy the mSpeechSynthesizer.setParam parameter above
        if (Params.ttsMode.equals(TtsMode.MIX)) {
            checkParams.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, Params.TEXT_FILENAME);
            checkParams.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, Params.MODEL_FILENAME);
        }
        InitConfig initConfig =  new InitConfig(entity.getAppId(), entity.getAppKey(), entity.getSecretKey(), Params.ttsMode, checkParams, listener);
        AutoCheck.getInstance(weakReference.get()).check(initConfig, new Handler() {
            @Override
            public void handleMessage(Message msg) {//Open a new thread check, callback after success
                if (msg.what == 100) {
                    AutoCheck autoCheck = (AutoCheck) msg.obj;
                    synchronized (autoCheck) {
                        String message = autoCheck.obtainDebugMessage();
                        Log.w(TAG, message);
                    }
                }
            }

        });
        // 6. initialization
        result = mSpeechSynthesizer.initTts(Params.ttsMode);
        Log.d(TAG, "initTts:"+result);
        checkResult(result, "initTts");
        initCallback.result(result,"");

    }

    private BaiduTextToSpeech(Context context,AuthEntity entity,Map<String, String> params,InitCallback initCallback){
        this.context = context;
        initBaiduTextToSpeechHandleThread(entity,params,initCallback);
    }

//    private static class NoLeakHandler extends Handler{
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//        }
//    }


    /**
     * Get unique instance
     * @param context context
     * @param params  params
     * @return  Get unique instance
     */
    public static BaiduTextToSpeech getInstance(Context context,AuthEntity entity,Map<String, String> params,InitCallback initCallback){
        if (null==baiduTextToSpeech){
            synchronized (BaiduTextToSpeech.class){
                if (null==baiduTextToSpeech){
                    baiduTextToSpeech = new BaiduTextToSpeech(context,entity,params,initCallback);
                }
            }
        }
        return baiduTextToSpeech;
    }

    public static BaiduTextToSpeech getInstance(Context context, AuthEntity entity, InitCallback initCallback){
        if (null==baiduTextToSpeech){
            synchronized (BaiduTextToSpeech.class){
                if (null==baiduTextToSpeech){
                    baiduTextToSpeech = new BaiduTextToSpeech(context,entity,null,initCallback);
                }
            }
        }
        return baiduTextToSpeech;
    }

    /**
     * 尝试初始化插件
     * @return 方法是否成功调用
     */
    public boolean initBaiduConfig(){
        if(null!=initHandler) {
            initHandler.sendEmptyMessage(INIT);
        }
        return (null!=initHandler);
    }

    /**
     * Call this method for voice output
     * @param message Text content
     */
    public void speak(String message){
        if (mSpeechSynthesizer == null) {
            Log.e(TAG,"[ERROR], Initialization failed");
            return;
        }
        int result = mSpeechSynthesizer.speak(message);
        Log.d(TAG, "speak:"+result);
    }


    /**
     * Check whether the appId ak sk is filled in correctly, and check whether the package name set in the official website application is consistent with the package name at runtime. The package name of this demo is defined in the build.gradle file
     * Offline authorization requires the application on the website to fill in the package name. The package name of this demo is com.baidu.tts.sample, which is defined in build.gradle
     * @return Whether authorization is successful
     */
    private AuthInfo checkAuth() {
        AuthInfo authInfo = mSpeechSynthesizer.auth(Params.ttsMode);
        return authInfo;
//        if (!authInfo.isSuccess()) {
//            String errorMsg = authInfo.getTtsError().getDetailMessage();
//            Log.d(TAG,"[Error] Authentication failed errorMsg =" + errorMsg);
//            return false;
//        } else {
//            Log.d(TAG,"Validation passed, offline official authorization file exists.");
//            return true;
//        }
    }


    /**Release resources*/
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
