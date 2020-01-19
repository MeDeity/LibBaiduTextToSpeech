package com.deity.libbaidutexttospeech;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.deity.texttospeech.BaiduTextToSpeech;
import com.deity.texttospeech.callback.InitCallback;
import com.deity.texttospeech.data.AuthEntity;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private EditText et_content;
    private Button btn_speak;
    private Button btn_init;
    private BaiduTextToSpeech instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //TODO 请自行变更为自己申请的ID
        final AuthEntity entity = new AuthEntity("16722234","pbXeOaX72Vi34I7m2CDz7K3k","wMDXW780SshFBkIgnAjf0SnSfftG2Ko7");
        instance = BaiduTextToSpeech.getInstance(MainActivity.this, entity, new InitCallback() {
            @Override
            public void result(int result,String message) {
                System.out.println("初始化结果："+result+"->"+message);
            }
        });
        btn_speak = this.findViewById(R.id.btn_speak);
        btn_init = this.findViewById(R.id.btn_init);
        et_content = this.findViewById(R.id.et_content);
        et_content.setText("欢迎使用百度语音合成工具库");
        btn_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = et_content.getText().toString();
                String result = TextUtils.isEmpty(data)?"欢迎使用百度语音合成工具库":data;
                instance.speak(result);
            }
        });
        btn_init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instance.initBaiduConfig();
            }
        });
        initPermission();
    }


    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.
            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }
}
