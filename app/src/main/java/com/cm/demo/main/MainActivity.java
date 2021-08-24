package com.cm.demo.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * 宿主app
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "App-Main";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PluginManager.getInstance().setContext(this);

        Log.d(TAG, "onCreate: " + MainActivity.class);

        findViewById(R.id.load_plugin)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadPlugin();
                    }
                });

        findViewById(R.id.start_plugin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProxy();
            }
        });
    }

    /**
     * 加载插件
     */
    private void loadPlugin() {
        String path = PluginManager.getInstance().getPluginPath();
        PluginManager.getInstance().loadPath(path);
        Toast.makeText(this, "加载完成", Toast.LENGTH_LONG).show();
    }

    /**
     * 跳转插件
     */
    private void startProxy() {
        Intent intent = new Intent(this, ProxyActivity.class); //这里就是一个占坑的activity
        //这里是拿到我们加载的插件的第一个activity的全类名
        intent.putExtra("ClassName", PluginManager.getInstance().getEntryName());
        startActivity(intent);
    }
}