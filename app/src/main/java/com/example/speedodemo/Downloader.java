package com.example.speedodemo;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

class Downloader {

    private static final String TAG = Downloader.class.getSimpleName();

    public static void download(final String f_url) {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                int count;
                try {
                    String root = Environment.getExternalStorageDirectory().toString();

                    Log.d(TAG, "Downloading");
                    URL url = new URL(f_url);

                    URLConnection conection = url.openConnection();
                    conection.connect();
                    // getting file length
                    int lenghtOfFile = conection.getContentLength();

                    // input stream to read file - with 8k buffer
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);

                    // Output stream to write file

                    OutputStream output = new FileOutputStream(root + "/downloadedfile.jpg");
                    byte data[] = new byte[1024];

                    long total = 0;
                    while ((count = input.read(data)) != -1) {
                        total += count;

                        // writing data to file
                        output.write(data, 0, count);

                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();

                    Log.d(TAG, "Downloaded: " + total + "/" + lenghtOfFile);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        })).start();
    }

}