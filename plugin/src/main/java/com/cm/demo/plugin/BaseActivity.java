package com.cm.demo.plugin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cm.demo.pf.AppInterface;

import androidx.annotation.NonNull;

/**
 * 这是插件的基类，所有的activity都要继承这个类。实现了定义插件标准的接口
 *
 * */
public class BaseActivity extends Activity implements AppInterface {
    private static final String TAG = "App-plugin-BaseAct";
    //这里的 mActivity 指的是我们的宿主app，因为插件是没有安装的 是没有上下文的
    protected Activity mActivity = null;

    @Override
    public void onAttach(Activity proxyActivity) {
        Log.d(TAG, "onAttach");
        this.mActivity = proxyActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (mActivity == null) {
            super.onCreate(savedInstanceState);
        }
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onStart() {
        if (mActivity == null) {
            super.onStart();
        }
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        if (mActivity == null) {
            super.onResume();
        }
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        if (mActivity == null) {
            super.onPause();
        }
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        if (mActivity == null) {
            super.onStop();
        }
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        if (mActivity == null) {
            super.onDestroy();
        }
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    //按下返回键
    @Override
    public void onBackPressed() {
        if(mActivity == null){
            super.onBackPressed();
        }
    }

    @Override
    public void setContentView(View view) {
        //调用宿主的activity
        if (mActivity != null) {
            mActivity.setContentView(view);
        } else {
            super.setContentView(view);
        }
    }

    /**
     * super.setContentView(layoutResID)最终调用的是系统给我们注入的上下文
     */
    @Override
    public void setContentView(int layoutResID) {
        if (mActivity == null) {
            super.setContentView(layoutResID);
        } else {
            mActivity.setContentView(layoutResID);
        }
    }

    @Override
    public <T extends View> T findViewById(int id) {
        if (mActivity == null) {
            return super.findViewById(id);
        } else {
            return mActivity.findViewById(id);
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        if (mActivity == null) {
            return super.getClassLoader();
        } else {
            return mActivity.getClassLoader();
        }
    }

    @NonNull
    @Override
    public LayoutInflater getLayoutInflater() {
        if (mActivity == null) {
            return super.getLayoutInflater();
        } else {
            return mActivity.getLayoutInflater();
        }
    }


    @Override
    public WindowManager getWindowManager() {
        if (mActivity == null) {
            return super.getWindowManager();
        } else {
            return mActivity.getWindowManager();
        }
    }

    @Override
    public Window getWindow() {
        if (mActivity == null) {
            return super.getWindow();
        } else {
            return mActivity.getWindow();
        }
    }

    @Override
    public void startActivity(Intent intent) {
        Log.d(TAG, "intent is null ? " + (intent==null));
        Log.d(TAG, "startActivity: " + intent.getComponent().getClassName());
        Intent intent1 = new Intent();
        intent1.putExtra("ClassName", intent.getComponent().getClassName());
        //这里会继续调用宿主MainActivity的startActivity
        mActivity.startActivity(intent1);
    }

}