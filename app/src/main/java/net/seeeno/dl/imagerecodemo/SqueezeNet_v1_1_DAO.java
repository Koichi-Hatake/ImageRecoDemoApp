/*
 * Copyright (C) 2019 Koichi Hatakeyama
 * All rights reserved.
 */
package net.seeeno.dl.imagerecodemo;

import android.content.Context;
import android.graphics.Bitmap;

public class SqueezeNet_v1_1_DAO extends NNablaImageNetDAO {

    /** */
    private static final String NETWORK_NAME = "SqueezeNet_v1.1";
    /** */
    private static final SqueezeNet_v1_1_DAO dao = new SqueezeNet_v1_1_DAO();
    /** */
    private static boolean mIsFinishedSetParam = false;

    /** */
    private SqueezeNet_v1_1_DAO() {

    }

    /** */
    public static ImageNetDAO getInstance() {
        return dao;
    }

    /** */
    public void loadNetwork(Context context) {
        loadParameters(context);
        if(!mIsFinishedSetParam) {
            setParameters(NETWORK_NAME);
            mIsFinishedSetParam = true;
        }
        loadNetwork();
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
