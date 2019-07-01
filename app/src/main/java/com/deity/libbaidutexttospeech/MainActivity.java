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

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private EditText et_content;
    private Button btn_speak;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BaiduTextToSpeech.getInstance(MainActivity.this);
        btn_speak = this.findViewById(R.id.btn_speak);
        et_content = this.findViewById(R.id.et_content);
        et_content.setText("泉州立亿软件有限公司成立于2017年，公司设于泉州软件园。致力于信息技术研发和专业人才培养，集软件开发，服务外包，大数据应用及企业技术服务，高端人才培训于一体。目前公司需要产品主要有物业管理软件，皮革生产管理系统，市区县绩效综合目标管理考评系统，二级分销会员系统，电梯维保云平台，派单管理软件等");
        btn_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = et_content.getText().toString();
                String result = TextUtils.isEmpty(data)?"欢迎使用百度语音合成工具库":data;
                BaiduTextToSpeech.getInstance(MainActivity.this).speak(result);
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
