package com.deity.texttospeech.data;

import android.util.Log;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizerListener;

/**
 * Create by fengwenhua at 2018/8/9
 **/
public class MessageListener implements SpeechSynthesizerListener {
    
    private static final String TAG = MessageListener.class.getSimpleName();
    
    @Override
    public void onSynthesizeStart(String message) {
        Log.d(TAG,"start Synthesize,message:" + message);
    }

    @Override
    public void onSynthesizeDataArrived(String message, byte[] bytes, int i, int i1) {
        Log.i(TAG, "Synthesize callback, message:" + message +":"+i+"/"+i1 );
    }

    @Override
    public void onSynthesizeFinish(String message) {
        Log.d(TAG,"Synthesize finish,message:" + message);
    }

    @Override
    public void onSpeechStart(String message) {
        Log.d(TAG,"speech start, message:" + message);
    }

    @Override
    public void onSpeechProgressChanged(String message, int progress) {
        Log.i(TAG, "speech progress:" + progress + ";message:" + message);
    }

    @Override
    public void onSpeechFinish(String message) {
        Log.d(TAG,"speech finish, message:" + message);
    }

    @Override

    public void onError(String message, SpeechError speechError) {
        Log.d(TAG,"description:" + speechError.description + ",code:" + speechError.code + ",serial:" + message);
    }
}
