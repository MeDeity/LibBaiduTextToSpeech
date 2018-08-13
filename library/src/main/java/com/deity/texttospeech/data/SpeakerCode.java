package com.deity.texttospeech.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
 * Create by fengwenhua at 2018/8/10
 **/
@SuppressWarnings("ALL")
public class SpeakerCode {
    /**默认女音*/
    public static final int SPEAKER_DEFAULT_FEMALE = 0;
    /**默认男音*/
    public static final int SPEAKER_DEFAULT_MALE = 1;
    /**特别男音*/
    public static final int SPEAKER_SPECIAL_MALE = 2;
    /**度逍遥*/
    public static final int SPEAKER_DU_XY = 3;
    /**度丫丫*/
    public static final int SPEAKER_DU_YY = 4;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SPEAKER_DEFAULT_FEMALE,SPEAKER_DEFAULT_MALE,SPEAKER_SPECIAL_MALE,SPEAKER_DU_XY,SPEAKER_DU_YY})
    public @interface Speaker{}
}
