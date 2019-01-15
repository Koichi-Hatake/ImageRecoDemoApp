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
    abstract public void initNetworkImpl(Context context) ;

    protected native void nativeInitNeuralNetwork(String nppPath, String networkName);
    protected native float[] nativePredict(int[] imageData);

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

    protected void copyNetworkFileInAssetsToLocal(Context context, String[] filelist) {

        for (String nnp_file: filelist) {
            try {
                File nnpFile = new File(context.getFilesDir() + "/" + nnp_file);
                // TODO: It should take another method to check.
                if(nnpFile.exists()) {
                    continue;
                }
                InputStream inputStream = context.getAssets().open(nnp_file);
                FileOutputStream fileOutputStream = new FileOutputStream(nnpFile, false);
                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = inputStream.read(buffer)) >= 0) {
                    fileOutputStream.write(buffer, 0, length);
                }
                fileOutputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initNetwork(Context context) {
        initNetworkImpl(context);
    }

}
