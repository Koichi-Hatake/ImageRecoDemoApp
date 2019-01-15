/*
 * Copyright (C) 2018 Koichi Hatakeyama
 * All rights reserved.
 */
package net.seeeno.dl.imagerecodemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import java.util.List;

public class HorizontalGalleryFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<MediaStoreData>> {

    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(R.id.loader_id_media_store_data, null, this);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView = (RecyclerView) result.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnItemTouchListener(new RecyclerAdapter.RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new RecyclerAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
               // bundle.putSerializable("images", images);
                bundle.putInt("position", position);
                //Toast.makeText(getActivity(), "Current position: "+position, Toast.LENGTH_SHORT).show();
                /*
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
                */
                RecyclerAdapter adpter = (RecyclerAdapter)recyclerView.getAdapter();
                MediaStoreData data = adpter.getPreloadItems(position).get(0);
                //String imageInfo = adpter.getPreloadItems(position).toString();
                //Toast.makeText(getActivity(), "Img info: "+ imageInfo, Toast.LENGTH_SHORT).show();
                MainActivity activity = (MainActivity)getActivity();
                activity.doImageRecognition(data.uri, data.mimeType);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return result;
    }

    @Override
    public Loader<List<MediaStoreData>> onCreateLoader(int i, Bundle bundle) {
        return new MediaStoreDataLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<MediaStoreData>> loader,
                               List<MediaStoreData> mediaStoreData) {
        GlideRequests glideRequests = GlideApp.with(this);
        RecyclerAdapter adapter =
                new RecyclerAdapter(getActivity(), mediaStoreData, glideRequests);
        RecyclerViewPreloader<MediaStoreData> preloader =
                new RecyclerViewPreloader<>(glideRequests, adapter, adapter, 3);
        recyclerView.addOnScrollListener(preloader);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<List<MediaStoreData>> loader) {
        // Do nothing.
    }
}
