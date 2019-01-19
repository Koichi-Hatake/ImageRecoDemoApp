/*
 * Copyright (C) 2018 Koichi Hatakeyama
 * All rights reserved.
 */
package net.seeeno.dl.imagerecodemo;

import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.MemoryCategory;

public class MainActivity extends AppCompatActivity implements
        ImageNetFragment.OnFragmentInteractionListener,
        OpenImagesFragment.OnFragmentInteractionListener {

    // Used to load the 'nnabla_android' library on application startup.
    static {
        System.loadLibrary("nnabla_android");
    }

    private android.content.Context mContext;
    private static final int REQUEST_READ_STORAGE = 0;
    private TabLayout mTabLayout;
    private ImageView mCurrentResultImageView;
    private AbstractDatasetFragment mDatasetFragmet;
    private ImageNetFragment mImageNetFragmet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GlideApp.get(this).setMemoryCategory(MemoryCategory.HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        } else {
            replaceFragment();
        }

        mTabLayout = (TabLayout) findViewById(R.id.dataset_tab);
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        ViewPager viewPager = (ViewPager) findViewById(R.id.dataset_pager);

        final String[] pageTitle = {"ImageNet", "Open Images v4"};
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;
                switch(position) {
                    case 0:
                        mImageNetFragmet = ImageNetFragment.newInstance("ImageNet", "1");
                        fragment = (Fragment)mImageNetFragmet;
                        break;
                    case 1:
                        fragment = (Fragment)OpenImagesFragment.newInstance("Open Images", "2");
                        //mCurrentResultImageView = ((ImageNetFragment)fragment).getImageView();
                        break;
                    default:
                }
                return fragment;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return pageTitle[position];
            }

            @Override
            public int getCount() {
                return pageTitle.length;
            }
        };

        viewPager.setAdapter(adapter);

        ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //Toast.makeText(MainActivity.this, "Current position: "+position, Toast.LENGTH_SHORT).show();
                //int id = navigation.getMenu().getItem(position).getItemId();
                //navigation.setSelectedItemId(id);
                //mTabLayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        viewPager.addOnPageChangeListener(listener);
        mTabLayout.setupWithViewPager(viewPager);

        mContext = getApplicationContext();

    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{permission.READ_EXTERNAL_STORAGE},
                REQUEST_READ_STORAGE);
    }

    private void replaceFragment() {
        Fragment fragment = new HorizontalGalleryFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_gallery_container, fragment)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    replaceFragment();
                } else {
                    Toast.makeText(this, "Storage permission is required", Toast.LENGTH_LONG)
                            .show();
                    requestStoragePermission();
                }
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void doImageRecognition(Uri uri, String mimeType) {

        AbstractDatasetFragment datasetFragment = getSelectedFragment();
        datasetFragment.doImageRecognition(getBaseContext(), uri, mimeType);

    }

    private AbstractDatasetFragment getSelectedFragment() {
        int selectedTab = mTabLayout.getSelectedTabPosition();
        switch (selectedTab) {
            case 0:
            case 1:
        }
        return mImageNetFragmet;
    }

    /**
     * A native method that is implemented by the 'nnabla_android' native library,
     * which is packaged with this application.
     */
    private native void nativeInitNeuralNetwork(String nppPath, String networkName);
    private native float[] nativePredict(int[] imageData);
}
