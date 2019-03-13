/*
 * Copyright (C) 2018 Koichi Hatakeyama
 * All rights reserved.
 */
package net.seeeno.dl.imagerecodemo;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public abstract class AbstractDatasetFragment extends Fragment {

    abstract public void doImageRecognition(Context context, Uri uri, String mimeType);
    abstract protected void initNetworkImpl(Context context) ;

    protected Bitmap openSelectedImage(Context context, Uri uri) {

        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        Bitmap bitmap = null;
        try {
            File file = null;
            if (cursor != null) {
                String path = null;
                if (cursor.moveToFirst()) {
                    path = cursor.getString(0);
                }
                cursor.close();
                if (path != null) {
                    file = new File(path);
                }
            }

            InputStream inputStream = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(inputStream);
            //Toast.makeText(context, "Img info: "+ file.toString() + "Img: " + bitmap.getWidth() + ":" + bitmap.getHeight(), Toast.LENGTH_SHORT).show();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public void initNetwork(Context context) {
        initNetworkImpl(context);
    }

}
