package com.deity.texttospeech.data;

import com.baidu.tts.client.SpeechSynthesizer;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration item initialization
 * Create by fengwenhua at 2018/8/10
 **/
public class BaiduParamsBuilder {

    private Map<String, String> params;

    public BaiduParamsBuilder() {
        params = new HashMap<>();
    }

    /**
     * Set online speaker
     *
     * @param speakerCode Pronunciation code
     */
    public BaiduParamsBuilder setOnlineSpeak(@SpeakerCode.Speaker int speakerCode) {
        params.put(SpeechSynthesizer.PARAM_SPEAKER, String.valueOf(speakerCode));
        return this;
    }

    /**
     * Set the volume
     *
     * @param volume Volume level
     */
    public BaiduParamsBuilder setVolume(@LevelCode.VolumeLevel int volume) {
        params.put(SpeechSynthesizer.PARAM_VOLUME, String.valueOf(volume));
        return this;
    }


    /**
     * Set the speaking rate
     *
     * @param speed Speaking rate
     */
    public BaiduParamsBuilder setSpeed(@LevelCode.SpeedLevel int speed) {
        params.put(SpeechSynthesizer.PARAM_SPEED, String.valueOf(speed));
        return this;
    }

    /**
     * Set the pitch
     *
     * @param pitch Pitch level
     */
    public BaiduParamsBuilder setPitch(@LevelCode.PitchLevel int pitch) {
        params.put(SpeechSynthesizer.PARAM_PITCH, String.valueOf(pitch));
        return this;
    }

    /**
     * Set composition mode
     * This parameter takes effect only in MIX mode. That is, pure online mode (TtsMode.ONLINE) does not take effect.
     * MIX_MODE_DEFAULT defaults to use online under wifi status and offline without wifi. In the online state, the request will automatically go offline after 6s timeout
     * MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI Use online under wifi status, not wifi offline. In the online state, the request will automatically go offline after 1.2s timeout
     * MIX_MODE_HIGH_SPEED_NETWORK, online in 3G 4G wifi state, offline in other states. In the online state, the request timed out 1.2s automatically goes offline
     * MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi use online, other status offline. In the online state, the request timed out 1.2s automatically goes offline
     *
     * @param mixMode Synthesis mode
     * @return Synthesis mode
     */
    public BaiduParamsBuilder setMixMode(String mixMode) {
        params.put(SpeechSynthesizer.PARAM_MIX_MODE, mixMode);
        return this;
    }

}
