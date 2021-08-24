package com.cm.demo.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * HookManager 的代码 是核心 主要加载我们的插件apk和插件资源的
 */
public class PluginManager {
    @SuppressLint("StaticFieldLeak")
    private static final PluginManager instance = new PluginManager();
    private static final String TAG = "App-Main-HookManager";
    private Context context;
    private Resources resources;
    private String entryName;
    private DexClassLoader dexClassLoader;


    public static PluginManager getInstance() {
        return instance;
    }

    private PluginManager() {

    }

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public void loadPath(String path) {
        Log.d(TAG, "loadPath: " + path);
        setEntryName(path);
        setClassLoader(path);
        setResources(path);
    }
    /** 获取插件app入口activity 名称 */
    private void setEntryName(String path) {
        //得到packageManager来获取包信息
        PackageManager packageManager = context.getPackageManager();
        //参数一是apk的路径，参数二是希望得到的内容
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        //得到插件app的入口activity名称（索引跟插件清单中的activity注册顺序有关）
        entryName = packageInfo.activities[0].name;
        Log.d(TAG, "setEntryName end , entryName : " + entryName);
    }
    public String getEntryName() {
        return entryName;
    }

    /** 构造classLoader */
    private void setClassLoader(String path) {
        //dex的缓存路径
        File dexOutFile = context.getDir("dex", Context.MODE_PRIVATE);
        dexClassLoader = new DexClassLoader(path, dexOutFile.getAbsoluteFile().getAbsolutePath(), null, context.getClassLoader());
        Log.d(TAG, "setClassLoader end : " +(dexClassLoader != null));
    }
    public DexClassLoader getDexClassLoader() {
        return dexClassLoader;
    }

    /** 构造resources */
    public Resources getResources() {
        return resources;
    }
    public void setResources(String path) {
        //由于构建resources必须要传入AssetManager，这里先构建一个AssetManager
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, path);
            resources = new Resources(assetManager, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());
            Log.d(TAG, "setResources end : " + (resources != null));
        } catch (Exception e) {

        }
    }


    /** 获取插件路径 */
    public String getPluginPath() {
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            // 假如这里是从网络获取的插件 我们直接从sd卡获取 然后读取到我们的cache目录
            String pluginName = "pd_plugin.apk";
            File pluginDir = context.getExternalFilesDir("plugin");
            String pluginPath = new File(pluginDir, pluginName).getAbsolutePath();
            Log.d(TAG, "loadPlugin: pluginDir = " + pluginDir);
            Log.d(TAG, "loadPlugin: pluginPath = " + pluginPath);
            Log.d(TAG, "loadPlugin: getExternalStorageDirectory = " + Environment.getExternalStorageDirectory());
            File file = new File(pluginPath);
            if (file.exists()) {
                boolean result = file.delete();
                Log.w(TAG, "delete file : " + result);
            }
            //从assets目录拷贝到存储目录
            try{
                InputStream inputStream = context.getAssets().open("pd_plugin.apk");
                os = new FileOutputStream(pluginPath);
                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = inputStream.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    if(os != null){
                        os.close();
                    }
                    if(is != null){
                        is.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return pluginPath;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(os != null){
                    os.close();
                }
                if(is != null){
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

//    private void loadPathToPlugin(Activity activity) {
//        File filesDir = activity.getExternalFilesDir("plugin");
////        File filesDir = activity.getDir("plugin", activity.MODE_PRIVATE);
//        String name = "pd_plugin.apk";
//        String path = new File(filesDir, name).getAbsolutePath();
//
//        //然后我们开始加载我们的apk 使用DexClassLoader
//        File dexOutDir = activity.getDir("dex", activity.MODE_PRIVATE);
//        loader = new DexClassLoader(path, dexOutDir.getAbsolutePath(), null, activity.getClassLoader());
//
//        //通过 PackageManager 来获取插件的包体信息，以便获取第一个activity是哪一个
//        PackageManager packageManager = activity.getPackageManager();
//        packageInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
//
//
//        //然后开始加载我们的资源 肯定要使用Resource 但是它是AssetManager创建出来的 就是AssertManager 有一个addAssertPath 方法，但是是私有的，所以使用反射
//        Class<?> assetManagerClass = AssetManager.class;
//        try {
//            AssetManager assetManager = (AssetManager) assetManagerClass.newInstance();
//            Method addAssetPathMethod = assetManagerClass.getMethod("addAssetPath", String.class);
//            addAssetPathMethod.setAccessible(true);
//            addAssetPathMethod.invoke(assetManager, path);
//            //在创建一个Resource
//            resources = new Resources(assetManager, activity.getResources().getDisplayMetrics(), activity.getResources().getConfiguration());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

}
