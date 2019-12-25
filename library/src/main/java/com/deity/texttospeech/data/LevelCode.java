package com.deity.texttospeech.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 等级
 * Create by fengwenhua at 2018/8/10
 **/
public class LevelCode {
    public static final int LEVEL_0 = 0;
    public static final int LEVEL_1 = 1;
    public static final int LEVEL_2 = 2;
    public static final int LEVEL_3 = 3;
    public static final int LEVEL_4 = 4;
    public static final int LEVEL_5 = 5;
    public static final int LEVEL_6 = 6;
    public static final int LEVEL_7 = 7;
    public static final int LEVEL_8 = 8;
    public static final int LEVEL_9 = 9;

    /**volume*/
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LEVEL_0,LEVEL_1,LEVEL_2,LEVEL_3,LEVEL_4,LEVEL_5,LEVEL_6,LEVEL_7,LEVEL_8,LEVEL_9})
    public @interface VolumeLevel{}

    /**speed*/
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LEVEL_0,LEVEL_1,LEVEL_2,LEVEL_3,LEVEL_4,LEVEL_5,LEVEL_6,LEVEL_7,LEVEL_8,LEVEL_9})
    public @interface SpeedLevel{}

    /**pitch*/
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LEVEL_0,LEVEL_1,LEVEL_2,LEVEL_3,LEVEL_4,LEVEL_5,LEVEL_6,LEVEL_7,LEVEL_8,LEVEL_9})
    public @interface PitchLevel {}
}
