package com.deity.texttospeech.data;

import android.util.Log;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizerListener;

/**
 * 这里添加文件描述
 * Create by fengwenhua at 2018/8/9
 **/
public class MessageListener implements SpeechSynthesizerListener {
    
    private static final String TAG = MessageListener.class.getSimpleName();
    
    @Override
    public void onSynthesizeStart(String message) {
        Log.d(TAG,"准备开始合成,序列号:" + message);
    }

    @Override
    public void onSynthesizeDataArrived(String message, byte[] bytes, int progress) {
        Log.i(TAG, "合成进度回调, progress：" + progress + ";序列号:" + message );
    }

    @Override
    public void onSynthesizeFinish(String message) {
        Log.d(TAG,"合成结束回调, 序列号:" + message);
    }

    @Override
    public void onSpeechStart(String message) {
        Log.d(TAG,"播放开始回调, 序列号:" + message);
    }

    @Override
    public void onSpeechProgressChanged(String message, int progress) {
        Log.i(TAG, "播放进度回调, progress：" + progress + ";序列号:" + message);
    }

    @Override
    public void onSpeechFinish(String message) {
        Log.d(TAG,"播放结束回调, 序列号:" + message);
    }

    @Override
    public void onError(String message, SpeechError speechError) {
        Log.d(TAG,"错误发生：" + speechError.description + "，错误编码："
                + speechError.code + "，序列号:" + message);
    }
}
