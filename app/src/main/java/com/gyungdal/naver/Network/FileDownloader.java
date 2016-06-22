package com.gyungdal.naver.Network;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.gyungdal.naver.Activity.CafeActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by GyungDal on 2016-06-08.
 */

/**
 * 1. Save Path + FILE NAME
 * 2. URL
 */

public class FileDownloader extends AsyncTask<String, Void, Void> {
    private static final String TAG = FileDownloader.class.getName();

    @Override
    protected Void doInBackground(String... params) {
        try{
            Log.i(TAG, "Download start : " + params[1]);
        File file = new File(params[0].substring(0, params[0].lastIndexOf("/")));
        if(!file.exists())
            file.mkdirs();
        file = new File(params[0]);
        if(file.isFile()){
            Log.d(TAG, "File Exist");
            Toast.makeText(CafeActivity.class.newInstance().getApplicationContext(),
                    "파일이 존재합니다.", Toast.LENGTH_SHORT).show();
        return null;
        }
        URL url = new URL(params[1]);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.connect();
        FileOutputStream f = new FileOutputStream(file);
        InputStream in = conn.getInputStream();
        byte[] buffer = new byte[conn.getContentLength()];
        int len1 = 0;

        while ((len1 = in.read(buffer)) > 0) {
            f.write(buffer, 0, len1);
            }
        f.close();
        } catch(Exception e){
            Log.e(TAG, e.getMessage());
        }
        Log.i(TAG, "Download done");
        return null;
    }
}
