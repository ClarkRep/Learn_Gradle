package com.example.zhaodanyang.knowledge_gradle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.lib_a.ALibUtils;
import com.example.lib_b.BLibUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String apiUrl = BuildConfig.API_URL;
        Log.i("haha", "aipUrl：" + apiUrl);

        String string = getResources().getString(R.string.test);
        Log.i("haha", "test的值：" + string);

        String versionName = BuildConfig.VERSION_NAME;
        Log.i("haha", "versionName：" + versionName);

        String appName = getResources().getString(R.string.app_name);
        Log.i("haha", "appName：" + appName);

        ALibUtils.showToast(getApplicationContext());
        BLibUtils.showToast(getApplicationContext());
    }
}
