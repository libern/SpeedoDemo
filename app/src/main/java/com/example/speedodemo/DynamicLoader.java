package com.example.speedodemo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;

import dalvik.system.DexClassLoader;

public class DynamicLoader {

    private static final String TAG = DynamicLoader.class.getSimpleName();

    static void downJar(Context context) {
        Downloader.download("https://www.someline.com/en/pickhub/images/large/8c799890c10ff383677c19edf0d4ab037815a55b.jpg");
    }

    static void downloadJar(Context context, String jarName) {
        try {
//            String dirName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dynamic";
            String dirName = context.getDir("custom", 0) + "/Dynamic";
            File f = new File(dirName);
            if (!f.exists()) {
                Log.w(TAG, f.getAbsolutePath() + " not exists, creating...");
                f.mkdir();
            }
            if (!f.canWrite()) {
                throw new Exception("Cant write: " + f.getAbsolutePath());
            }
            File file = new File(dirName + File.separator + jarName);
            if (file.exists()) {
                Log.w(TAG, file.getAbsolutePath() + " is exists, re-creating...");
            }
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
//        String dirName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dynamic";
        String dirName = context.getDir("custom", 0) + "/Dynamic";

        File optimizedDexOutputPath = new File(dirName + File.separator + fileName); // 外部路径
        if (optimizedDexOutputPath.exists()) {
            Log.w(TAG, optimizedDexOutputPath.getAbsolutePath() + " exists, loading...");
            String sha1 = Hash.calculateSHA1(optimizedDexOutputPath);
            Log.w(TAG, optimizedDexOutputPath.getAbsolutePath() + ", sha1: " + sha1);
        } else {
            Log.w(TAG, optimizedDexOutputPath.getAbsolutePath() + " not exists, cancelled...");
            return;
        }

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
//            String string = (String) start.invoke(libProviderClazz.newInstance());// 调用方法并获取返回值
            String string = (String) start.invoke(null);// 调用方法并获取返回值
            Toast.makeText(context, string, Toast.LENGTH_LONG).show();
        } catch (Exception exception) {
            // Handle exception gracefully here.
            exception.printStackTrace();
        }

    }

}
