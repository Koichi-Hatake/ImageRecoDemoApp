/*
 * Copyright (C) 2019 Koichi Hatakeyama
 * All rights reserved.
 */
package net.seeeno.dl.imagerecodemo;

import android.os.AsyncTask;

abstract class TakeLongTask extends AsyncTask<Object, Void, Void> {

    /** */
    abstract int getLoadedBytePercentImpl();
    /** */
    public int getLoadedBytePercent() {
        return getLoadedBytePercentImpl();
    }

}
