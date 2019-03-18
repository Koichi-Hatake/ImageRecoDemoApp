/*
 * Copyright (C) 2019 Koichi Hatakeyama
 * All rights reserved.
 */
package net.seeeno.dl.imagerecodemo;

import android.content.Context;
import android.graphics.Bitmap;

public class MobileNet_DAO extends NNablaImageNetDAO {

    /** */
    private static final String NETWORK_NAME = "MobileNet";
    /** */
    private static final MobileNet_DAO dao = new MobileNet_DAO();
    /** */
    private static boolean mIsFinishedSetParam = false;

    /** */
    private MobileNet_DAO() {

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
        //initNeuralNetwork();
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
