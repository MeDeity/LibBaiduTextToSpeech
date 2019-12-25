package com.deity.texttospeech.data;

import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import java.util.Map;

/**
 * 合成引擎的初始化参数
 * <p>
 * Created by fujiayi on 2017/9/13.
 */

public class InitConfig {
    /**
     * appId appKey and secretKey. Note If you need offline synthesis function, please fill in the package name in the application you applied for.
     * The package name of this demo is com.baidu.tts.sample, which is defined in build.gradle.
     */
    private String appId;

    private String appKey;

    private String secretKey;

    /**
     * Pure online or off-line fusion
     */
    private TtsMode ttsMode;


    /**
     * init params
     */
    private Map<String, String> params;

    /**
     * Synthesizer listener
     */
    private SpeechSynthesizerListener listener;

    private InitConfig() {

    }

    public InitConfig(String appId, String appKey, String secretKey, TtsMode ttsMode,
                      Map<String, String> params, SpeechSynthesizerListener listener) {
        this.appId = appId;
        this.appKey = appKey;
        this.secretKey = secretKey;
        this.ttsMode = ttsMode;
        this.params = params;
        this.listener = listener;
    }

    public SpeechSynthesizerListener getListener() {
        return listener;
    }

    public Map<String, String> getParams() {
        return params;
    }


    public String getAppId() {
        return appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public TtsMode getTtsMode() {
        return ttsMode;
    }
}
