package com.app.smjockey.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.smjockey.R;

/**
 * Created by Akash Srivastava on 04-07-2016.
 */
public class LiveWallFragment extends android.support.v4.app.Fragment {

    public LiveWallFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("LiveWall","fuck1");
        return inflater.inflate(R.layout.livewall_fragment, container, false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            Log.d("LiveWallFragment", "Visible");

            if (!isVisibleToUser) {
                Log.d("LiveWallFragment", "Not visible");
                // TODO stop audio playback
            }
        }
    }

}