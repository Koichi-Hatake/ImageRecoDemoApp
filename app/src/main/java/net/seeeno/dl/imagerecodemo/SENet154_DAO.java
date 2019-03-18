/*
 * Copyright (C) 2019 Koichi Hatakeyama
 * All rights reserved.
 */
package net.seeeno.dl.imagerecodemo;

import android.content.Context;
import android.graphics.Bitmap;

public class SENet154_DAO extends NNablaImageNetDAO {

    /** */
    private static final String NETWORK_NAME = "SENet-154";
    /** */
    private static final SENet154_DAO dao = new SENet154_DAO();
    /** */
    private static boolean mIsFinishedSetParam = false;

    /** */
    private SENet154_DAO() {

    }

    /** */
    public static ImageNetDAO getInstance() {
        return dao;
    }

    /** */
    public void loadNetwork(Context context, ProgressHandler processHandler) {
        loadParameters(context);
        if(!mIsFinishedSetParam) {
            setParameters(NETWORK_NAME);
            mIsFinishedSetParam = true;
        }
        loadNetwork(processHandler);
    }

    /** */
    public String getName() {
        return NETWORK_NAME;
    }

    /** */
    public float[] predict(Bitmap bitmap) {
        return super.predict(bitmap);
    }

    /** */
    public String getCategoryName(int index) {
        return super.getCategoryName(index);
    }

}
