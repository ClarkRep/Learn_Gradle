package com.example.zhaodanyang.knowledge_gradle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String apiUrl = BuildConfig.API_URL;
        Log.i("haha", apiUrl);

        String string = getResources().getString(R.string.test);
        Log.i("haha", string);
    }
}
