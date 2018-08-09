package com.deity.texttospeech.data;

import com.baidu.tts.client.TtsMode;
import com.deity.texttospeech.utils.OfflineResource;

import java.io.File;

/**
 * 常量配置
 * Create by fengwenhua at 2018/8/3
 **/
public class Params {

    public static String appId = "11655957";

    public static String appKey = "e5BpBvO6y8gL6sT7WwYm9inH";

    public static String secretKey = "YDCZ0D1o5IHIyovYFqL7jwZylmoyGVRU";

    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    public static TtsMode ttsMode = TtsMode.MIX;

    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat为离线男声模型；
    // assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat为离线女声模型
    public static String offlineVoice = OfflineResource.VOICE_DUYY;


    // ================选择TtsMode.ONLINE  不需要设置以下参数; 选择TtsMode.MIX 需要设置下面2个离线资源文件的路径
    private static final String TEMP_DIR = "/sdcard/baiduTTS"; // 重要！请手动将assets目录下的3个dat 文件复制到该目录

    // 文本模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
    public static final String TEXT_FILENAME = TEMP_DIR + File.separator + "bd_etts_text.dat";

    // 声学模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读(m15是离线男声)
    public static final String MODEL_FILENAME = TEMP_DIR + File.separator + "bd_etts_common_speech_as_mand_eng_high_am_v3.0.0_20170516.dat";
}
