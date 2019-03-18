/*
 * Copyright (C) 2018 Koichi Hatakeyama
 * All rights reserved.
 */
package net.seeeno.dl.imagerecodemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 *
 */
public class ImageNetFragment extends AbstractDatasetFragment {

    private static final String TAG = "ImageNetFragment";
    private OnFragmentInteractionListener mListener;
    //private String[] mNetworkStrings;
    //private String[] mNetworkFilenames;
    private String[] mNetworkNames;
    //private String[] mClassList;
    private ImageView mImageNetImageView;
    private Spinner mNetworkSpinner;
    private TextView mResultText;
    private TextView mElapsedTimeText;
    private ProgressBar mProgressBar;

    private ProgressHandler mProgressHandler;
    private int mLastProcessedTime = 0;

    private final static int RESULT_NUM_SHOWN = 3;

    /** */
    private ImageNetDAO mCurrentNetwork;

    /** */
    public ImageNetFragment() {
        // Required empty public constructor
    }

    public static ImageNetFragment newInstance(String param1, String param2) {
        ImageNetFragment fragment = new ImageNetFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Resources res = getResources();
        //mNetworkNames = res.getStringArray(R.array.imagenet_network_name_array);
        mNetworkNames = (String [])ImageNetDAOFactory.getLineup().toArray(new String[ImageNetDAOFactory.getLineup().size()]);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.imagenet_fragment, container, false);

        mImageNetImageView = (ImageView) v.findViewById(R.id.imagenet_reco_img);
        mElapsedTimeText = (TextView)v.findViewById(R.id.elapsed_time_text);
        mResultText = (TextView)v.findViewById(R.id.imagenet_result_str);
        mNetworkSpinner = (Spinner) v.findViewById(R.id.imagenet_network_spinner);
        mProgressBar = (ProgressBar)v.findViewById(R.id.progress_bar);
        // Init Progress handler
        mProgressHandler = new ProgressHandler();
        mProgressHandler.setProgressBar(mProgressBar);
        // Register spinner listener
        mNetworkSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initNetwork(getContext());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /*
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //Toast.makeText(HomeFragment.this.getContext(), "" + position,
                //        Toast.LENGTH_SHORT).show();
                mOriginalImageView.setImageResource(mThumbIds[position]);
            }
        }); */

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /** */
    private Bitmap resizeToPreferredSizeBitmap(Bitmap bitmap, int width, int height) {
        Bitmap resizedBitmap;
        Bitmap finalBitmap;

        int inputWidth = bitmap.getWidth();
        int inputHeight = bitmap.getHeight();

        float x_ratio = (float)inputWidth / width;
        float y_ratio = (float)inputHeight / height;
        float scale;
        int x_resized, y_resized;
        int x_start, y_start;

        if (x_ratio > y_ratio) {
            scale = y_ratio;
            x_resized = (int)((float)inputWidth / scale);
            y_resized = height;
            x_start = (x_resized - width) / 2;
            y_start = 0;
        } else {
            scale = x_ratio;
            x_resized = width;
            y_resized = (int)((float)inputHeight / scale);
            x_start = 0;
            y_start = (y_resized - height) / 2;
        }
        Log.v(TAG, "Original Bitmap: " + inputWidth + ":" + inputHeight);
        Log.v(TAG, "Resize scale: " + scale + ": " + x_ratio + ":" + y_ratio);
        Log.v(TAG, "Resize Bitmap: " + x_resized + ":" + y_resized);
        Log.v(TAG, "Clop rect: " + x_start + ":" + y_start);
        resizedBitmap =  Bitmap.createScaledBitmap(bitmap, x_resized, y_resized, true);
        finalBitmap = Bitmap.createBitmap(resizedBitmap, x_start, y_start, width, height);

        return finalBitmap;
    }

    public void doImageRecognition(Context context, Uri uri, String mimeType) {
        PredictTask predictTask = new PredictTask();
        predictTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, context, uri);
        mProgressHandler.setTaskTakeLong(predictTask);
        mProgressHandler.setProgress(0);
        mProgressHandler.sendEmptyMessage(0);
    }

    public void initNetworkImpl(Context context) {
        int currentNetwork = mNetworkSpinner.getSelectedItemPosition();
        String network_name = mNetworkNames[currentNetwork];

        InitNeuralNetworkTask initNeuralNetworkTask = new InitNeuralNetworkTask();
        initNeuralNetworkTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, context, network_name);
        mProgressHandler.setTaskTakeLong(initNeuralNetworkTask);
        mProgressHandler.setProgress(0);
        mProgressHandler.sendEmptyMessage(0);
        /*
        mCurrentNetwork = ImageNetDAOFactory.getImageNetDAO(network_name);
        mCurrentNetwork.loadNetwork(context, mProgressHandler);
        */
    }

    /** */
    public class InitNeuralNetworkTask extends TakeLongTask {
        /** */
        private Context mContext;
        private String mNetName;
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected Void doInBackground(Object... params) {
            Log.v(TAG, "Pass: InitNeuralNetwork::doInBackground()");
            mContext = (Context)params[0];
            mNetName = (String)params[1];
            mCurrentNetwork = ImageNetDAOFactory.getImageNetDAO(mNetName);
            mCurrentNetwork.loadNetwork(mContext);
            return null;
        }
        @Override
        protected void onProgressUpdate(Void... values) {
        }
        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(mContext, "Neural Network has been loaded: " + mNetName,  Toast.LENGTH_SHORT).show();
        }
        @Override
        public int getLoadedBytePercentImpl() {
            return mCurrentNetwork.getLoadNetworkProgress();
        }
    }

    /** */
    public class PredictTask extends TakeLongTask {

        private Bitmap mResizedBitmap;
        private int mProcessTime = 0;
        private float[] mPredictArray;

        private int pollingCtr = 0;

        @Override
        protected void onPreExecute() {
        }
        @Override
        protected Void doInBackground(Object... params) {
            Log.v(TAG, "Pass: PredictTask::doInBackground()");
            Context context = (Context)params[0];
            Uri uri = (Uri)params[1];
            pollingCtr = 0;

            // Load Bitmap
            Bitmap selectedBitmap = openSelectedImage(context, uri);

            // Resize Bitmap
            int inputWidth = mCurrentNetwork.getInputWidth();
            int inputHeight = mCurrentNetwork.getInputHeight();
            if (mResizedBitmap != null) {
                mResizedBitmap.recycle();
            }
            mResizedBitmap = resizeToPreferredSizeBitmap(selectedBitmap, inputWidth, inputHeight);
            selectedBitmap.recycle();

            // Predict
            long startTime = System.currentTimeMillis();
            mPredictArray = mCurrentNetwork.predict(mResizedBitmap);
            long finishTime = System.currentTimeMillis();
            mProcessTime = (int)(finishTime - startTime);
            mLastProcessedTime = mProcessTime;

            return null;
        }
        @Override
        protected void onProgressUpdate(Void... values) {
        }
        @Override
        protected void onPostExecute(Void result) {

            // Process results
            TreeMap<Integer, Float> resultMap = new TreeMap<>();
            for (int i=0; i < mPredictArray.length; i++) {
                if (mPredictArray[i] != 0) {
                    resultMap.put(Integer.valueOf(i), Float.valueOf(mPredictArray[i]));
                    //Log.v(TAG,"Result:" + i  + ": " + predictArray[i]);
                }
            }
            List<Map.Entry<Integer, Float>> list_entries = new ArrayList<Map.Entry<Integer, Float>>(resultMap.entrySet());
            Collections.sort(list_entries, new Comparator<Map.Entry<Integer, Float>>() {
                public int compare(Map.Entry<Integer, Float> obj1, Map.Entry<Integer, Float> obj2) {
                    return obj2.getValue().compareTo(obj1.getValue());
                }
            });
            int ctr=0;
            String resultStr = "";
            DecimalFormat precisionForm = new DecimalFormat("##0.00%");
            for(Map.Entry<Integer, Float> entry : list_entries) {
                if (ctr >= RESULT_NUM_SHOWN) {
                    break;
                }
                int index = entry.getKey();
                Log.v(TAG,index + ": " + entry.getValue() + " : " + mCurrentNetwork.getCategoryName(index));
                resultStr += mCurrentNetwork.getCategoryName(index) + ": " + precisionForm.format(entry.getValue()) + "\n";
                ctr++;
            }
            // Show results
            mResultText.setText(resultStr);
            Log.v(TAG, "Elapsed Time: " + Integer.toString(mProcessTime));
            mElapsedTimeText.setText(Integer.toString(mProcessTime));

            // Set bitmap to main image view.
            mImageNetImageView.setImageBitmap(mResizedBitmap);

        }
        @Override
        public int getLoadedBytePercentImpl() {
            int prog = 0;
            if (mLastProcessedTime != 0) {
                prog = (int)(++pollingCtr * 100 * 100 / mLastProcessedTime);
            }
            return prog;
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
