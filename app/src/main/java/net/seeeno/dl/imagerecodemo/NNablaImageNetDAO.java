/*
 * Copyright (C) 2019 Koichi Hatakeyama
 * All rights reserved.
 */
package net.seeeno.dl.imagerecodemo;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public abstract class NNablaImageNetDAO implements ImageNetDAO {

    /** */
    private static final String TAG = "NNablaImageNetDAO";
    /** */
    protected native void nativeInitNeuralNetwork(String nppPath, String networkName);
    protected native float[] nativePredict(int[] imageData);

    /** */
    private final static String NNABLA_JSON_FILENAME = "imagenet_nnabla.json";
    /** */
    private final static String JSON_NETWORK_ARRAY_KEY = "network_list";
    private final static String JSON_NETWORK_NAME_KEY = "name";
    private final static String JSON_NETWORK_FILENAME_KEY = "network_filename";
    private final static String JSON_NETWORK_FILE_LOCALTION_KEY = "file_location";
    private final static String JSON_NETWORK_EXECUTOR_NAME_KEY = "executor_name";
    private final static String JSON_NETWORK_CATEGORY_FILENAME_KEY = "category_filename";
    private final static String JSON_INPUT_WIDTH_KEY = "input_width";
    private final static String JSON_INPUT_HEIGHT_KEY = "input_height";

    /** */
    private static boolean mIsFinishedLoadParam = false;

    /** */
    private static ArrayList<HashMap<String, String>> mNetworkList = new ArrayList<HashMap<String, String>>();
    private HashMap<String, String> mParamList;
    private int mInputChannel = 3;
    private Context mContext;
    private List<String> mCategoryList = new ArrayList<String>();

    /** */
    private void loadParametersFromJson(AssetManager assetManager) {
        Log.v(TAG, "Pass: loadParametersFromJson()");
        // Parse JSON
        InputStream is = null;
        String jsondata = null;
        try {
            is = assetManager.open(NNABLA_JSON_FILENAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsondata = new String(buffer, "UTF-8");
        } catch (IOException e) {
            Log.v(TAG, "Json File open error");
        }

        try {
            JSONObject obj = new JSONObject(jsondata);
            JSONArray jArry = obj.getJSONArray(JSON_NETWORK_ARRAY_KEY);
            HashMap<String, String> nets;

            for (int i = 0; i < jArry.length(); i++) {
                JSONObject jo_inside = jArry.getJSONObject(i);
                //Log.d(TAG, "Details-->" + jo_inside.getString(JSON_NETWORK_NAME_KEY));
                //Log.d(TAG, "Details-->" + jo_inside.getString(JSON_NETWORK_FILENAME_KEY));

                String network_name_value = jo_inside.getString(JSON_NETWORK_NAME_KEY);
                String network_filename_value = jo_inside.getString(JSON_NETWORK_FILENAME_KEY);
                String network_file_location_value = jo_inside.getString(JSON_NETWORK_FILE_LOCALTION_KEY);
                String network_executor_name_value = jo_inside.getString(JSON_NETWORK_EXECUTOR_NAME_KEY);
                String network_category_filename_value = jo_inside.getString(JSON_NETWORK_CATEGORY_FILENAME_KEY);
                String network_input_width_value = jo_inside.getString(JSON_INPUT_WIDTH_KEY);
                String network_input_height_value = jo_inside.getString(JSON_INPUT_HEIGHT_KEY);

                // Add values in 'ArrayList' as below:
                nets = new HashMap<String, String>();
                nets.put(JSON_NETWORK_NAME_KEY, network_name_value);
                nets.put(JSON_NETWORK_FILENAME_KEY, network_filename_value);
                nets.put(JSON_NETWORK_FILE_LOCALTION_KEY, network_file_location_value);
                nets.put(JSON_NETWORK_EXECUTOR_NAME_KEY, network_executor_name_value);
                nets.put(JSON_NETWORK_CATEGORY_FILENAME_KEY, network_category_filename_value);
                nets.put(JSON_INPUT_WIDTH_KEY, network_input_width_value);
                nets.put(JSON_INPUT_HEIGHT_KEY, network_input_height_value);

                mNetworkList.add(nets);
            }
        } catch (JSONException e) {
            //e.printStackTrace();
        }

    }

    /** */
    protected void loadParameters(Context context) {
        Log.v(TAG, "Pass: loadParameters()");
        mContext = context;
        // Load all parameters
        if (!mIsFinishedLoadParam) {
            loadParametersFromJson(context.getAssets());
            mIsFinishedLoadParam = true;
        }
    }

    /** */
    protected void setParameters(String key) {
        Log.v(TAG, "Pass: setParameters()");
        // Error check
        if (!mIsFinishedLoadParam) {
            Log.d(TAG, "Parameters is not initialized yet.");
            return;
        }

        // Set specific parameters
        for (HashMap<String, String> net:  mNetworkList) {
            String network_name = net.get(JSON_NETWORK_NAME_KEY);
            if(key.equals(network_name)) {
                mParamList = net;
            }
        }
        // Load category name list
        loadCategoryNameList();

    }

    /** */
    private void loadCategoryNameList() {
        Log.v(TAG, "Pass: loadCategoryNameList()");
        // Open category name file
        String paramCategoryFilename = mParamList.get(JSON_NETWORK_CATEGORY_FILENAME_KEY);
        InputStream is = null;
        try {
            is = mContext.getAssets().open(paramCategoryFilename);
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String category;
            while((category = in.readLine()) != null) {
                mCategoryList.add(category);
            }
        } catch (IOException e) {
            Log.v(TAG, "Category file open error: " + paramCategoryFilename);
        }
    }

    /** */
    protected void loadNetwork() {
        Log.v(TAG, "Pass: loadNetwork()");

        String fileLocation = mParamList.get(JSON_NETWORK_FILE_LOCALTION_KEY);
        String networkFilename = mParamList.get(JSON_NETWORK_FILENAME_KEY);

        if(fileLocation.equals("assets")) {
            loadNetworkFromAssets(networkFilename);
        } else {
            loadNetworkFromRemote();
        }

        // Initialize network
        initNeuralNetwork();

    }

    /** */
    private void loadNetworkFromAssets(String networkFilename) {
        // Copy network file from assets to local
        try {
            File nnpLocalFile = new File(mContext.getFilesDir() + "/" + networkFilename);
            // TODO: It should take another method to check.
            if(nnpLocalFile.exists()) {
                return;
            }
            InputStream inputStream = mContext.getAssets().open(networkFilename);
            FileOutputStream fileOutputStream = new FileOutputStream(nnpLocalFile, false);
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

    /** */
    private NetworkFileDownload networkFiledownload;

    /** */
    private void loadNetworkFromRemote() {
        // Load parameters
        String networkFilename = mParamList.get(JSON_NETWORK_FILENAME_KEY);
        String fileLocation = mParamList.get(JSON_NETWORK_FILE_LOCALTION_KEY);

        // Open local file
        Log.v(TAG, "Download: " + fileLocation + networkFilename);
        File nnpLocalFile = new File(mContext.getFilesDir() + "/" + networkFilename);
        // TODO: It should take another method to check.
        if(nnpLocalFile.exists()) {
            return;
        }
        // Start download
        networkFiledownload = new NetworkFileDownload(fileLocation + networkFilename, nnpLocalFile);
        networkFiledownload.startDownload();

    }

    /** */
    @Override
    public int getLoadNetworkProgress() {
        int prog = 0;
        if (networkFiledownload != null) {
            prog = networkFiledownload.getLoadedBytePercent();
        }
        return prog;
    }

    /** */
    private void initNeuralNetwork() {
        Log.v(TAG, "Pass: initNeuralNetwork()");
        String networkFilename = mParamList.get(JSON_NETWORK_FILENAME_KEY);
        String executorName = mParamList.get(JSON_NETWORK_EXECUTOR_NAME_KEY);
        nativeInitNeuralNetwork(mContext.getFilesDir() + "/" + networkFilename, executorName);
    }

    /** */
    @Override
    public float[] predict(Bitmap bitmap) {
        int inputWidth = getInputWidth();
        int inputHeight = getInputHeight();
        int inputChannel = getInputChannel();
        int colorOffset = inputWidth * inputHeight;

        // Create array
        int[] color_array = new int[inputWidth * inputHeight];
        bitmap.getPixels(color_array, 0, inputWidth, 0, 0, inputWidth, inputHeight);
        int[] rgb_array = new int[inputWidth * inputHeight * inputChannel];

        for(int i=0; i < color_array.length; i++) {
            rgb_array[i] = Color.red(color_array[i]); // Red
            rgb_array[colorOffset + i] = Color.green(color_array[i]); // Green
            rgb_array[colorOffset*2 + i] = Color.blue(color_array[i]); // Blue
        }

        // Predict
        float predictArray[] = nativePredict(rgb_array);

        return predictArray;
    }

    /** */
    @Override
    public String getCategoryName(int index) {
        return (String)mCategoryList.get(index);
    }

    /** */
    @Override
    public int getInputWidth() {
        return Integer.parseInt(mParamList.get(JSON_INPUT_WIDTH_KEY));
    }

    /** */
    @Override
    public int getInputHeight() {
        return Integer.parseInt(mParamList.get(JSON_INPUT_HEIGHT_KEY));
    }

    /** */
    @Override
    public int getInputChannel() {
        return 3;
    }

}
