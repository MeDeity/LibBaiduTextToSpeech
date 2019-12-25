package com.deity.texttospeech.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;

import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;
import com.deity.texttospeech.data.InitConfig;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by fujiayi on 2017/12/28.
 */

/**
 * Automatic troubleshooting tools for errors found after integration.
 * <p>
 * The following errors can be detected:
 * 1. PermissionCheck:AndroidManifest,xml Required permissions
 * 2. JniCheck: Check if the so file is installed in the specified directory
 * 3. AppInfoCheck: In the case of networking, check whether the appId appKey secretKey is correct
 * 4. ApplicationIdCheck: Display the package name applicationId, prompt the user to manually check the official website
 * 5. ParamKeyExistCheck:Check if key exists, currently check SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE
 * and PARAM_TTS_SPEECH_MODEL_FILE
 * 6.  OfflineResourceFileCheck Check if the offline resource file (need to be copied from the assets directory) exists
 * <p>
 * <p>
 * Example usage code:
 * AutoCheck.getInstance(getApplicationContext()).check(initConfig, new Handler() {
 *
 * @Override public void handleMessage(Message msg) {
 * if (msg.what == 100) {
 * AutoCheck autoCheck = (AutoCheck) msg.obj;
 * synchronized (autoCheck) {
 * String message = autoCheck.obtainDebugMessage();
 * toPrint(message); // Can be replaced with the following line, view the code in logcat
 * //Log.w("AutoCheckMessage",message);
 * }
 * }
 * }
 * <p>
 * });
 */
public class AutoCheck {

    private static AutoCheck instance;

    private LinkedHashMap<String, Check> checks;

    private static Context context;

    private boolean hasError = false;

    volatile boolean isFinished = false;

    /**
     * Get instance, non-thread safe
     */
    public static AutoCheck getInstance(Context context) {
        if (instance == null || AutoCheck.context != context) {
            instance = new AutoCheck(context);
        }
        return instance;
    }

    public void check(final InitConfig initConfig, final Handler handler) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                AutoCheck obj = innerCheck(initConfig);
                isFinished = true;
                synchronized (obj) { // Sporadic, synchronizing thread information
                    Message msg = handler.obtainMessage(100, obj);
                    handler.sendMessage(msg);
                }
            }
        });
        t.start();

    }

    private AutoCheck innerCheck(InitConfig config) {
        checks.put("Check for applied Android permissions", new PermissionCheck(context));
        checks.put("Check if 4 so files exist", new JniCheck(context));
        checks.put("Check AppId AppKey SecretKey",
                new AppInfoCheck(config.getAppId(), config.getAppKey(), config.getSecretKey()));
        checks.put("Check package name", new ApplicationIdCheck(context, config.getAppId()));

        if (TtsMode.MIX.equals(config.getTtsMode())) {
            Map<String, String> params = config.getParams();
            String fileKey = SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE;
            checks.put("Check offline text file parameters", new ParamKeyExistCheck(params, fileKey,
                    "SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE is not set,"));
            checks.put("Check offline resource text files", new OfflineResourceFileCheck(params.get(fileKey)));
            fileKey = SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE;
            checks.put("Check offline resource Speech file parameters", new ParamKeyExistCheck(params, fileKey,
                    "SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE is not set,"));
            checks.put("Check offline resources Speech files", new OfflineResourceFileCheck(params.get(fileKey)));
        }

        for (Map.Entry<String, Check> e : checks.entrySet()) {
            Check check = e.getValue();
            check.check();
            if (check.hasError()) {
                break;
            }
        }
        return this;
    }

    public String obtainErrorMessage() {
        PrintConfig config = new PrintConfig();
        return formatString(config);
    }

    public String obtainDebugMessage() {
        PrintConfig config = new PrintConfig();
        config.withInfo = true;
        return formatString(config);
    }

    public String obtainAllMessage() {
        PrintConfig config = new PrintConfig();
        config.withLog = true;
        config.withInfo = true;
        return formatString(config);
    }

    public String formatString(PrintConfig config) {
        StringBuilder sb = new StringBuilder();
        hasError = false;

        for (HashMap.Entry<String, Check> entry : checks.entrySet()) {
            Check check = entry.getValue();
            String testName = entry.getKey();
            if (check.hasError()) {
                if (!hasError) {
                    hasError = true;
                }

                sb.append("[ERROR][").append(testName).append("]").append(check.getErrorMessage()).append("\n");
                if (check.hasFix()) {
                    sb.append("[Repair method] [").append(testName).append("]").append(check.getFixMessage()).append("\n");
                }
            }
            if (config.withInfo && check.hasInfo()) {
                sb.append("[Please check manually] [").append(testName).append("]").append(check.getInfoMessage()).append("\n");
            }
            if (config.withLog && (config.withLogOnSuccess || hasError) && check.hasLog()) {
                sb.append("[Log]:" + check.getLogMessage()).append("\n");
            }
        }
        if (!hasError) {
            sb.append("Integrated automated troubleshooting tools: Congratulations no problems detected\n");
        }
        return sb.toString();
    }

    public void clear() {
        checks.clear();
        hasError = false;
    }

    private AutoCheck(Context context) {
        this.context = context;
        checks = new LinkedHashMap<String, Check>();
    }

    private static class PrintConfig {
        public boolean withFix = true;
        public boolean withInfo = false;
        public boolean withLog = false;
        public boolean withLogOnSuccess = false;
    }

    private static class PermissionCheck extends Check {
        private Context context;

        public PermissionCheck(Context context) {
            this.context = context;
        }

        @Override
        public void check() {
            String[] permissions = {
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    // Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    // Manifest.permission.WRITE_SETTINGS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    // Manifest.permission.CHANGE_WIFI_STATE
            };

            ArrayList<String> toApplyList = new ArrayList<String>();

            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(context, perm)) {
                    toApplyList.add(perm);
                    // Enter here means no permissions.
                }
            }
            if (!toApplyList.isEmpty()) {
                errorMessage = "Missing permissions:" + toApplyList;
                fixMessage = "Please copy the relevant permissions from AndroidManifest.xml";
            }
        }
    }

    private static class JniCheck extends Check {
        private Context context;

        private String[] soNames;

        public JniCheck(Context context) {
            this.context = context;
            soNames = new String[]{"libbd_etts.so", "libBDSpeechDecoder_V1.so", "libbdtts.so", "libgnustl_shared.so"};
        }

        @Override
        public void check() {
            String path = context.getApplicationInfo().nativeLibraryDir;
            appendLogMessage("Jni so file directory" + path);
            File[] files = new File(path).listFiles();
            TreeSet<String> set = new TreeSet<>();
            if (files != null) {
                for (File file : files) {
                    if (file.canRead()) {
                        set.add(file.getName());
                    }
                }
            }
            appendLogMessage("Files in the Jni directory:" + set.toString());
            for (String name : soNames) {
                if (!set.contains(name)) {
                    errorMessage = "Jni directory" + path + " Missing readable so file:" + name + ",List of files in this directory:" + set.toString();
                    fixMessage = "If there are no other so files in your app, please copy src / main / jniLibs in the demo to the directory with the same name."
                            + "If there are so files in the app, please merge the directories together (note the intersection of directories and delete unnecessary directories).";
                    break;
                }
            }
        }
    }

    private static class ParamKeyExistCheck extends Check {
        private Map<String, String> params;
        private String key;
        private String prefixErrorMessage;

        public ParamKeyExistCheck(Map<String, String> params, String key, String prefixErrorMessage) {
            this.params = params;
            this.key = key;
            this.prefixErrorMessage = prefixErrorMessage;
        }

        @Override
        public void check() {
            if (params == null || !params.containsKey(key)) {
                errorMessage = prefixErrorMessage + " Not set in the parameters:" + key;
                fixMessage = "Please refer to demo in settings " + key + "parameter";
            }
        }
    }

    private static class OfflineResourceFileCheck extends Check {
        private String filename;
        private String nullMessage;

        public OfflineResourceFileCheck(String filename) {
            this.filename = filename;
            this.nullMessage = nullMessage;
        }

        @Override
        public void check() {
            File file = new File(filename);
            boolean isSuccess = true;
            if (!file.exists()) {
                errorMessage = "资源文件不存在：" + filename;
                isSuccess = false;
            } else if (!file.canRead()) {
                errorMessage = "资源文件不可读：" + filename;
                isSuccess = false;
            }

            if (!isSuccess) {
                fixMessage = "请将demo中src/main/assets目录下同名文件复制到 " + filename;
            }
        }
    }

    private static class ApplicationIdCheck extends Check {

        private String appId;
        private Context context;

        public ApplicationIdCheck(Context context, String appId) {
            this.appId = appId;
            this.context = context;
        }

        @Override
        public void check() {
            infoMessage = "If you encounter offline synthesis initialization problems during integration, please check the appId on the webpage:" + appId
                    + " Whether the application has launched a synthesis service, and the application on the webpage is filled with the Android package name:"
                    + getApplicationId();
        }

        private String getApplicationId() {
            return context.getPackageName();
        }
    }


    private static class AppInfoCheck extends Check {
        private String appId;
        private String appKey;
        private String secretKey;

        public AppInfoCheck(String appId, String appKey, String secretKey) {
            this.appId = appId;
            this.appKey = appKey;
            this.secretKey = secretKey;
        }


        public void check() {
            do {
                appendLogMessage("try to check appId " + appId + " ,appKey=" + appKey + " ,secretKey" + secretKey);
                if (appId == null || appId.isEmpty()) {
                    errorMessage = "appId is empty";
                    fixMessage = "Fill in appID";
                    break;
                }
                if (appKey == null || appKey.isEmpty()) {
                    errorMessage = "appKey is empty";
                    fixMessage = "Fill in appID";
                    break;
                }
                if (secretKey == null || secretKey.isEmpty()) {
                    errorMessage = "secretKey Is empty";
                    fixMessage = "secretKey";
                    break;
                }

            } while (false);
            try {
                checkOnline();
            } catch (UnknownHostException e) {
                infoMessage = "No network or network disconnection, ignore detection:" + e.getMessage();
            } catch (Exception e) {
                errorMessage = e.getClass().getCanonicalName() + ":" + e.getMessage();
                fixMessage = " Recheck whether appId, appKey, appSecret are correct";
            }
        }

        public void checkOnline() throws Exception {
            String urlpath = "http://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials&client_id="
                    + appKey + "&client_secret=" + secretKey;
            URL url = new URL(urlpath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(1000);
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder result = new StringBuilder();
            String line = "";
            do {
                line = reader.readLine();
                if (line != null) {
                    result.append(line);
                }
            } while (line != null);
            String res = result.toString();
            appendLogMessage("openapi return " + res);
            JSONObject jsonObject = new JSONObject(res);
            String error = jsonObject.optString("error");
            if (error != null && !error.isEmpty()) {
                throw new Exception("appkey secretKey error" + ", error:" + error + ", json is" + result);
            }
            String token = jsonObject.getString("access_token");
            if (token == null || !token.endsWith("-" + appId)) {
                throw new Exception("appId is inconsistent with appkey and appSecret.appId = " + appId + " ,token = " + token);
            }
        }


    }

    private abstract static class Check {
        protected String errorMessage = null;

        protected String fixMessage = null;

        protected String infoMessage = null;

        protected StringBuilder logMessage;

        public Check() {
            logMessage = new StringBuilder();
        }

        public abstract void check();

        public boolean hasError() {
            return errorMessage != null;
        }

        public boolean hasFix() {
            return fixMessage != null;
        }

        public boolean hasInfo() {
            return infoMessage != null;
        }

        public boolean hasLog() {
            return !logMessage.toString().isEmpty();
        }

        public void appendLogMessage(String message) {
            logMessage.append(message + "\n");
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String getFixMessage() {
            return fixMessage;
        }

        public String getInfoMessage() {
            return infoMessage;
        }

        public String getLogMessage() {
            return logMessage.toString();
        }


    }
}
