package com.example.speedodemo;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class DynamicLoader {

    private static final String TAG = DynamicLoader.class.getSimpleName();

    static void downloadJar(Context context, String jarName) {
        try {
            String dirName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dynamic";
            File f = new File(dirName);
            if (!f.exists()) {
                Log.w(TAG, f.getAbsolutePath() + " not exists, creating...");
                f.mkdir();
            }
            if (!f.canWrite()) {
                throw new Exception("Cant write: " + f.getAbsolutePath());
            }
            File file = new File(dirName + File.separator + jarName);
            InputStream is = context.getAssets().open(jarName);
            byte[] bs = new byte[1024];
            int len;
            OutputStream os = new FileOutputStream(file);
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            os.close();
            os.flush();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void loadJar(Context context, String fileName) {
        String dirName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dynamic";

        File optimizedDexOutputPath = new File(dirName + File.separator + fileName); // 外部路径
        File dexOutputDir = context.getDir("dex", 0); // 无法直接从外部路径加载.dex文件，需要指定APP内部路径作为缓存目录（.dex文件会被解压到此目录）
        DexClassLoader dexClassLoader = new DexClassLoader(optimizedDexOutputPath.getAbsolutePath(),
                dexOutputDir.getAbsolutePath(),
                null,
                context.getClassLoader());

        Class libProviderClazz = null;
        try {
            libProviderClazz = dexClassLoader.loadClass("com.example.libspeedo.LibEntry");
            // 遍历类里所有方法
            Method[] methods = libProviderClazz.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Log.e(TAG, methods[i].toString());
            }
            Method start = libProviderClazz.getDeclaredMethod("init");// 获取方法
            start.setAccessible(true);// 把方法设为public，让外部可以调用
            String string = (String) start.invoke(libProviderClazz.newInstance());// 调用方法并获取返回值
            Toast.makeText(context, string, Toast.LENGTH_LONG).show();
        } catch (Exception exception) {
            // Handle exception gracefully here.
            exception.printStackTrace();
        }

    }

}
