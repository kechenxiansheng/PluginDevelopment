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
 * HookManager 的代码是整个插件化的核心：
 * 1、下载插件apk并存储
 * 2、加载插件apk信息
 * 3、加载插件资源
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

    /** 创建加载插件类的ClassLoader
     *  DexClassLoader 参数说明：
     *      dexPath： 指目标类所在的jar/apk文件路径, 多个路径使用 File.pathSeparator分隔, Android里面默认为 ":"
     *      optimizedDirectory： 解压出的dex文件的存放路径，以免被注入攻击，不可存放在外置存储。
     *      libraryPath ：目标类中的C/C++库存放路径。
     *      parent： 父类装载器
     * */
    private void setClassLoader(String path) {
        //dex的缓存路径
        File dexOutFile = context.getDir("dex", Context.MODE_PRIVATE);
        dexClassLoader = new DexClassLoader(path, dexOutFile.getAbsoluteFile().getAbsolutePath(), null, context.getClassLoader());
        Log.d(TAG, "setClassLoader end : " +(dexClassLoader != null));
    }
    public DexClassLoader getDexClassLoader() {
        return dexClassLoader;
    }

    /** 创建获取插件资源的Resources
     *  Resources() 参数说明（不是很明白，暂且直接这么用吧。解释是源码的翻译）：
     *      assets  资源管理器对象
     *      metrics 当前资源对象的有效显示指标
     *      config  当前资源对象有效的当前配置。
     * */
    public Resources getResources() {
        return resources;
    }
    public void setResources(String path) {
        try {
            //由于构建resources必须要传入AssetManager，这里先构建一个AssetManager
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, path);
            resources = new Resources(assetManager, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());
            Log.d(TAG, "setResources end : " + (resources != null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** 下载插件，存储获取插件存储路径 */
    public String getPluginPath() {
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            //TODO 假如这里是从网络获取的插件，我们直接从assets中获取，并放在存储中
            String pluginName = "pd_plugin.apk";
            File pluginDir = context.getExternalFilesDir("plugin");
            String pluginPath = new File(pluginDir, pluginName).getAbsolutePath();
            Log.d(TAG, "loadPlugin: pluginDir = " + pluginDir);
            Log.d(TAG, "loadPlugin: pluginPath = " + pluginPath);
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
        }
        return "";
    }
}
