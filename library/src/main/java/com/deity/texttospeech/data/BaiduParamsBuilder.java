package com.deity.texttospeech.data;

import com.baidu.tts.client.SpeechSynthesizer;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置项初始化
 * Create by fengwenhua at 2018/8/10
 **/
public class BaiduParamsBuilder {

    private Map<String, String> params;

    public BaiduParamsBuilder() {
        params = new HashMap<>();
    }

    /**
     * 设置在线发音人
     *
     * @param speakerCode 发音人代码
     * @return 发音人
     */
    public BaiduParamsBuilder setOnlineSpeak(@SpeakerCode.Speaker int speakerCode) {
        params.put(SpeechSynthesizer.PARAM_SPEAKER, String.valueOf(speakerCode));
        return this;
    }

    /**
     * 设置音量
     *
     * @param volume 音量等级
     * @return 音量
     */
    public BaiduParamsBuilder setVolume(@LevelCode.VolumeLevel int volume) {
        params.put(SpeechSynthesizer.PARAM_VOLUME, String.valueOf(volume));
        return this;
    }


    /**
     * 设置语速
     *
     * @param speed 语速等级
     * @return 语速
     */
    public BaiduParamsBuilder setSpeed(@LevelCode.SpeedLevel int speed) {
        params.put(SpeechSynthesizer.PARAM_SPEED, String.valueOf(speed));
        return this;
    }

    /**
     * 设置音调
     *
     * @param pitch 音调等级
     * @return 音调
     */
    public BaiduParamsBuilder setPitch(@LevelCode.PitchLevel int pitch) {
        params.put(SpeechSynthesizer.PARAM_PITCH, String.valueOf(pitch));
        return this;
    }

    /**
     * 设置合成模式
     * 该参数只有在MIX模式下生效。即纯在线模式(TtsMode.ONLINE)不生效。
     * MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
     * MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
     * MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
     * MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
     *
     * @param mixMode 合成模式
     * @return 合成模式
     */
    public BaiduParamsBuilder setMixMode(String mixMode) {
        params.put(SpeechSynthesizer.PARAM_MIX_MODE, mixMode);
        return this;
    }

}
