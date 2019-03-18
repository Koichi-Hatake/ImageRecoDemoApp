/*
 * Copyright (C) 2019 Koichi Hatakeyama
 * All rights reserved.
 */
package net.seeeno.dl.imagerecodemo;

import android.os.Handler;
import android.os.Message;
import android.os.AsyncTask;
import android.widget.ProgressBar;

public class ProgressHandler extends Handler {

    private ProgressBar mProgressBar;
    private TakeLongTask mTaskTakeLong;

    /** */
    private final static int POLLING_PERIOD_MS = 100;

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        if(mTaskTakeLong.getStatus() == AsyncTask.Status.FINISHED){
            mProgressBar.setProgress(0);
        }else{
            mProgressBar.setProgress(mTaskTakeLong.getLoadedBytePercent());
            this.sendEmptyMessageDelayed(0, POLLING_PERIOD_MS);
        }
    }

    /** */
    public void setProgressBar(ProgressBar prog_bar) {
        mProgressBar = prog_bar;
    }
    /** */
    public void setTaskTakeLong(TakeLongTask task) {
        mTaskTakeLong = task;
    }
    /** */
    public void setProgress(int value) {
        mProgressBar.setProgress(value);
    }
}
