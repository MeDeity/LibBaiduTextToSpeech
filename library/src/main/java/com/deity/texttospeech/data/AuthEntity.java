package com.deity.texttospeech.data;

public class AuthEntity {

    public String appId;

    public String appKey;

    public String secretKey;

    public AuthEntity(String appId, String appKey, String secretKey) {
        this.appId = appId;
        this.appKey = appKey;
        this.secretKey = secretKey;
    }


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
