package com.deity.texttospeech.callback;

/**
 * 初始化结果回调
 */
public interface InitCallback {
    /**
     * 初始化情况
     * @param result 错误码 0-代表正常
     */
    void result(int result,String message);
}
