/*
 * Copyright (C) 2019 Koichi Hatakeyama
 * All rights reserved.
 */
package net.seeeno.dl.imagerecodemo;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CountDownLatch;

public class NetworkFileDownload {
    private final String TAG = "NetworkFileDownload";
    private final int TIMEOUT_READ = 5000;
    private final int TIMEOUT_CONNECT = 30000;

    public Activity owner;
    private final int BUFFER_SIZE = 1024;

    private String mUrlString;
    private File mOutputFile;
    private FileOutputStream fileOutputStream;
    private InputStream inputStream;
    private BufferedInputStream bufferedInputStream;

    private long totalByte = 0;
    private long currentByte = 0;

    private byte[] buffer = new byte[BUFFER_SIZE];

    private URL url;
    private URLConnection urlConnection;

    /** */
    public NetworkFileDownload(String url, File outFile) {
        mUrlString = url;
        mOutputFile = outFile;
    }

    /** */
    public Boolean startDownload() {
        try{
            connect();
        }catch(IOException e){
            Log.d(TAG, "ConnectError:" + e.toString());
        }

        if (bufferedInputStream !=  null){
            try{
                int len;
                while((len = bufferedInputStream.read(buffer)) != -1){
                    fileOutputStream.write(buffer, 0, len);
                    currentByte += len;
                }
            }catch(IOException e){
                Log.d(TAG, e.toString());
                return false;
            }
        }else{
            Log.d(TAG, "bufferedInputStream == null");
        }

        try{
            close();
        }catch(IOException e){
            Log.d(TAG, "CloseError:" + e.toString());
        }
        Log.d(TAG, "Download finished.");
        return true;
    }

    /** */
    private void connect() throws IOException {
        url = new URL(mUrlString);
        urlConnection = url.openConnection();
        urlConnection.setReadTimeout(TIMEOUT_READ);
        urlConnection.setConnectTimeout(TIMEOUT_CONNECT);
        inputStream = urlConnection.getInputStream();
        bufferedInputStream = new BufferedInputStream(inputStream, BUFFER_SIZE);
        fileOutputStream = new FileOutputStream(mOutputFile);

        totalByte = urlConnection.getContentLength();
        currentByte = 0;
        Log.v(TAG, "Total Byte: " + totalByte);
    }

    /** */
    private void close() throws IOException {
        fileOutputStream.flush();
        fileOutputStream.close();
        bufferedInputStream.close();
    }

    /** */
    public int getLoadedBytePercent() {
        if(totalByte <= 0){
            return 0;
        }
        //Log.v(TAG, "Current Byte/Total: " + currentByte + "/" + totalByte);
        return (int)(100 * currentByte/totalByte);
    }

}
