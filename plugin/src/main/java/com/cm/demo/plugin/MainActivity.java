package com.cm.demo.plugin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * 插件app
 */
public class MainActivity extends BaseActivity {

    private static final String TAG = "App-Plugin-MainAct";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: 这是插件 MainActivity");
        /** 插件内部启动第二个activity */
        findViewById(R.id.startSecond).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + (mActivity == null));
                Intent intent = new Intent(mActivity, SecondActivity.class);
                /** 调用BaseActivity重写的方法，并最终通过宿主上下文启动 */
                startActivity(intent);
            }
        });
    }
}