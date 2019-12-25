package com.deity.texttospeech.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Set online voice: 0 Normal female voice (default) 1 Normal male voice 2 Special male voice 3 Emotional male voice Du xiaoyao 4 Emotional child voice Du yaya
 * Create by fengwenhua at 2018/8/10
 **/
@SuppressWarnings("ALL")
public class SpeakerCode {
    public static final int SPEAKER_DEFAULT_FEMALE = 0;
    public static final int SPEAKER_DEFAULT_MALE = 1;
    public static final int SPEAKER_SPECIAL_MALE = 2;
    public static final int SPEAKER_DU_XY = 3;
    public static final int SPEAKER_DU_YY = 4;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SPEAKER_DEFAULT_FEMALE,SPEAKER_DEFAULT_MALE,SPEAKER_SPECIAL_MALE,SPEAKER_DU_XY,SPEAKER_DU_YY})
    public @interface Speaker{}
}
