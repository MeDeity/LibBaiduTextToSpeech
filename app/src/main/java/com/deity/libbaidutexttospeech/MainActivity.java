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
        et_content.setText("厦门科拓通讯技术股份有限公司是中国智慧停车场的缔造者，致力于研发并提供多元全面的智慧停车场应用解决方案，包括全视频智慧停车场解决方案、多样化停车场车位引导解决方案、城市停车诱导解决方案，构成了一套各有针对性的完整的解决方案体系，为增加停车场使用率、提升经营者经济效益、改善城市交通状况、提高驾驶人员停车效率提供有效的技术支持");
        btn_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = et_content.getText().toString();
                String result = TextUtils.isEmpty(data)?"你好,我是科拓速泊小管家,调皮,年轻，活泼，捣蛋,成长,更快，更强，更智能":data;
                BaiduTextToSpeech.getInstance(MainActivity.this).Speek(result);
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
