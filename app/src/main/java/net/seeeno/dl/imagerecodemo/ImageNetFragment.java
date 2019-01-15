/*
 * Copyright (C) 2018 Koichi Hatakeyama
 * All rights reserved.
 */
package net.seeeno.dl.imagerecodemo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
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

    private static final String TAG = "MainActivity";
    private OnFragmentInteractionListener mListener;
    private String[] mNetworkStrings;
    private String[] mNetworkFilenames;
    private String[] mNetworkNames;
    private String[] mClassList;
    private ImageView mImageNetImageView;
    private Spinner mNetworkSpinner;
    private TextView mResultText;
    private TextView mElapsedTimeText;
    private boolean hasCopied = false;

    private final static int INPUT_WIDTH = 640;
    private final static int INPUT_HEIGHT = 480;
    private final static int INPUT_CHANNEL = 3;

    private final static int RESULT_NUM_SHOWN = 3;

    /** */
    public ImageNetFragment() {
        // Required empty public constructor
    }

    public static ImageNetFragment newInstance(String param1, String param2) {
        ImageNetFragment fragment = new ImageNetFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources res = getResources();
        mNetworkStrings = res.getStringArray(R.array.imagenet_network_string_array);
        mNetworkFilenames =res.getStringArray(R.array.imagenet_network_filename_array);
        mNetworkNames =res.getStringArray(R.array.imagenet_network_name_array);
        mClassList = res.getStringArray(R.array.imagenet_class_list_array);
        if (!hasCopied) {
            copyNetworkFileInAssetsToLocal(getContext(), mNetworkFilenames);
            hasCopied = true;
        }
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
        mNetworkSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initNetworkImpl(getContext());
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

    private Bitmap resizeToPrefferdSizeBitmap(Bitmap bitmap, int width, int height) {
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

        // Check Mime Type
        //Toast.makeText(context, "Img info: "+ file.toString() + "Img: " + bitmap.getWidth() + ":" + bitmap.getHeight(), Toast.LENGTH_SHORT).show();

        // Load Bitmap
        Bitmap selectedBitmap = openSelectedImage(context, uri);

        // Resize Bitmap
        Bitmap resizedBitmap = resizeToPrefferdSizeBitmap(selectedBitmap, INPUT_WIDTH, INPUT_HEIGHT);

        // Create array
        //int[] rgb_array = new int[INPUT_WIDTH * INPUT_HEIGHT *  INPUT_CHANNEL];
        //int[] rgb_array = new int[307200];
        int[] rgb_array = new int[INPUT_WIDTH * INPUT_HEIGHT];
        resizedBitmap.getPixels(rgb_array, 0, INPUT_WIDTH, 0, 0, INPUT_WIDTH, INPUT_HEIGHT);
        /*
        java.util.Random rnd = new java.util.Random();
        for(int i=0; i < rgb_array.length; i++) {
            rgb_array[i] = rnd.nextInt();
        }
        */
        for(int i=0; i < 10; i++) {
            Log.v(TAG,"RGB Data: 0x" + Integer.toHexString(rgb_array[i]));
        }

        // Predict
        long startTime = System.currentTimeMillis();
        float predictArray[] = nativePredict(rgb_array);
        long finishTime = System.currentTimeMillis();
        int process_time = (int)(finishTime - startTime);

        // Process results
        TreeMap<Integer, Float> resultMap = new TreeMap<>();
        for (int i=0; i < predictArray.length; i++) {
            if (predictArray[i] != 0) {
                //resultMap.put(Float.valueOf(predictArray[i]), Integer.valueOf(i));
                resultMap.put(Integer.valueOf(i), Float.valueOf(predictArray[i]));
                //Log.v(TAG,"Result:" + i  + ": " + predictArray[i]);
            }
        }
        List<Map.Entry<Integer, Float>> list_entries = new ArrayList<Map.Entry<Integer, Float>>(resultMap.entrySet());
        Collections.sort(list_entries, new Comparator<Map.Entry<Integer, Float>>() {
            public int compare(Map.Entry<Integer, Float> obj1, Map.Entry<Integer, Float> obj2) {
                return obj2.getValue().compareTo(obj1.getValue());
                //return obj2.getKey().compareTo(obj1.getKey());
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
            Log.v(TAG,index + ": " + entry.getValue() + " : " + mClassList[index]);
            resultStr += mClassList[index] + ": " + precisionForm.format(entry.getValue()) + "\n";
            ctr++;
        }
        /*
        for (float result: predictArray) {
            //Log.v(TAG, "Result: " + Float.toString(result));
        }
        */

        mResultText.setText(resultStr);
        Log.v(TAG, "Elapsed Time: " + Integer.toString(process_time));
        mElapsedTimeText.setText(Integer.toString(process_time));


        // Set bitmap to main image view.
        mImageNetImageView.setImageBitmap(resizedBitmap);

    }

    public void initNetworkImpl(Context context) {
        int currentNetwork = mNetworkSpinner.getSelectedItemPosition();
        String network_file = mNetworkFilenames[currentNetwork];
        String network_name = mNetworkNames[currentNetwork];

        nativeInitNeuralNetwork(context.getFilesDir() + "/" + network_file, network_name);

        Toast.makeText(context, "Neural Network has changed: " + mNetworkStrings[currentNetwork], Toast.LENGTH_SHORT).show();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
