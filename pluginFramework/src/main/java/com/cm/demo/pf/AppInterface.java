package com.cm.demo.pf;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

/**
 * 定义插件标准
 * 1、定义方法是activity的生命周期和函数
 * 2、宿主app需主动调用onAttach和onCreate。onAttach传入宿主的上下文，onCreate传入Bundle启动插件activity
 */
public interface AppInterface {

    //生命周期的activity
    public void onAttach(Activity proxyActivity);

    public void onCreate(Bundle savedInstanceState);

    public <T extends View> T findViewById(int id);

    public void onStart();

    public void onResume();

    public void onPause();

    public void onStop();

    public void onDestroy();

    public void onSaveInstanceState(Bundle outState);

    public boolean onTouchEvent(MotionEvent event);

    public void onBackPressed();
}
