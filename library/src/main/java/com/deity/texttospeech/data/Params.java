package com.deity.texttospeech.data;

import com.baidu.tts.client.TtsMode;
import com.deity.texttospeech.utils.OfflineResource;

import java.io.File;

/**
 * 常量配置
 * Create by fengwenhua at 2018/8/3
 **/
public class Params {

    // TtsMode.MIX; off-line integration, online priority; TtsMode.ONLINE pure online; no pure offline
    public static TtsMode ttsMode = TtsMode.MIX;

    // Offline pronunciation selection, VOICE_FEMALE is the offline female voice.
    // bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat in the assets directory is an offline male voice model;
    // bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat in the assets directory is an offline female voice model
    public static String offlineVoice = OfflineResource.VOICE_DUYY;


    // Selecting TtsMode.ONLINE does not require setting the following parameters; selecting TtsMode.MIX requires setting the path of the following 2 offline resource files
    //important! Please manually copy the 3 dat files under the assets directory to this directory
    private static final String TEMP_DIR = "/sdcard/baiduTTS";

    // Text model file path (for offline engines), note that TEXT_FILENAME must exist and be readable
    public static final String TEXT_FILENAME = TEMP_DIR + File.separator + "bd_etts_text.dat";

    // Acoustic model file path (for offline engine use), note that TEXT_FILENAME must exist and change (m15 is offline male voice)
    public static final String MODEL_FILENAME = TEMP_DIR + File.separator + "bd_etts_common_speech_as_mand_eng_high_am_v3.0.0_20170516.dat";
}
