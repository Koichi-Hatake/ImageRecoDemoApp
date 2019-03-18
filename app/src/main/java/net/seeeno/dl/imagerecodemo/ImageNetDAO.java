/*
 * Copyright (C) 2019 Koichi Hatakeyama
 * All rights reserved.
 */
package net.seeeno.dl.imagerecodemo;

import android.content.Context;
import android.graphics.Bitmap;

public interface ImageNetDAO {
    /** */
    public void loadNetwork(Context context, ProgressHandler processHandler);
    /** */
    public String getName();
    /** */
    public float[] predict(Bitmap bitmap);
    /** */
    public String getCategoryName(int index);
    /** */
    public int getInputWidth();
    /** */
    public int getInputHeight();
    /** */
    public int getInputChannel();

}
