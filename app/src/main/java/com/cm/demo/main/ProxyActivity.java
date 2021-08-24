package com.cm.demo.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cm.demo.pf.AppInterface;

import java.lang.reflect.Constructor;

/**
 * 占位activity
 * 用于宿主启动插件时占位，并确定跳转到插件的具体activity
 */
public class ProxyActivity extends AppCompatActivity {

    private static final String TAG = "App-Main-ProxyActivity";

    //要跳转的具体activity的类路径
    private String className = "";
    private static AppInterface appInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在这里拿到真实跳转的activity并跳转
        className = getIntent().getStringExtra("ClassName");
        Log.d(TAG, "onCreate: className = " + className) ;
        /**
         * 通过反射拿到class，
         * 但不能用以下方式
         * classLoader.loadClass(className)
         * Class.forName(className)
         * 因为插件app没有被安装！ 这里我们调用重写的classLoader
         */
        try {
            Class<?> activityClass = getClassLoader().loadClass(className);
            Constructor<?> constructor = activityClass.getConstructor();
            Object instance = constructor.newInstance();

            appInterface = (AppInterface) instance;
            appInterface.onAttach(this);
            Bundle bundle = new Bundle();
            appInterface.onCreate(bundle);
        } catch (Exception e) {
            if (e.getClass().getSimpleName() .equals("ClassCastException")){
                //我这里是直接拿到异常判断的 ，也可的 拿到上面的plugClass对象判断有没有实现我们的接口
                Toast.makeText(this,"非法页面",Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        appInterface.onStart();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        appInterface.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        appInterface.onPause();
        super.onPause();
    }

    //重写classLoader
    @Override
    public ClassLoader getClassLoader() {
        return PluginManager.getInstance().getDexClassLoader();
    }

    //重写Resource
    @Override
    public Resources getResources() {
        return PluginManager.getInstance().getResources();
    }

    /**
     * 为什么要重写这个呢？因为插件内部 startActivity 调用的是宿主app的 startActivity
     * 将需要跳转的类名传过来，跳转至占位activity，再跳转至目标activity
     * */
    @Override
    public void startActivity(Intent intent) {
        String className1 = intent.getStringExtra("ClassName");
        Intent intent1 = new Intent(this, ProxyActivity.class);
        intent1.putExtra("ClassName", className1);
        super.startActivity(intent1);
    }

}