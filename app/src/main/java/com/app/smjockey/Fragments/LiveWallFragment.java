package com.app.smjockey.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.smjockey.Adapters.LiveWallAdapter;
import com.app.smjockey.Models.LiveWallPosts;
import com.app.smjockey.Models.Streams;
import com.app.smjockey.R;
import com.app.smjockey.Utils.Constants;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Akash Srivastava on 04-07-2016.
 */
public class LiveWallFragment extends android.support.v4.app.Fragment {


    static Bundle bundles;

    private String TAG=com.app.smjockey.Fragments.LiveWallFragment.class.getSimpleName();

    private List<LiveWallPosts> liveWallPostsList;
    Streams streamItem;

    //Creating Views
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Firebase.setAndroidContext(getContext());

        View rootView = inflater.inflate(R.layout.post_fragment, container, false);

        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        liveWallPostsList=new ArrayList<>();
        adapter=new LiveWallAdapter(getActivity(),liveWallPostsList);
        recyclerView.setAdapter(adapter);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                streamItem= (Streams) bundles.getSerializable("Stream");
                Log.d(TAG,streamItem.getId()+streamItem.getUuid());
                getLiveWall();
            }
        },2500);


        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(liveWallPostsList!=null)
            liveWallPostsList.clear();
    }

    public void getLiveWall()
    {
        final Firebase firebase=new Firebase(Constants.livewall_url+streamItem.getUuid()+"/data");

        firebase.addChildEventListener(new ChildEventListener() {
            // Retrieve new posts as they are added to the database
            @Override
            public void onChildAdded(final DataSnapshot snapshot, String previousChildKey) {

                HashMap value=snapshot.getValue(HashMap.class);
                JSONObject jsonObject=new JSONObject(value);

                Log.d(TAG,"json:"+jsonObject.toString());
                Gson gson=new GsonBuilder().create();
                LiveWallPosts liveWallPosts=gson.fromJson(jsonObject.toString(),LiveWallPosts.class);
                liveWallPostsList.add(liveWallPosts);
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Log.d(TAG,"Child Removed:"+ dataSnapshot.child("id").getValue());
                for(int i=0;i<liveWallPostsList.size();i++)
                {
                    Log.d(TAG,"Inside Removed:"+liveWallPostsList.get(i).getId()+"-"+ dataSnapshot.child("id").getValue());
                    Long id=(Long)dataSnapshot.child("id").getValue();

                    Log.d(TAG, String.valueOf(liveWallPostsList.get(i).getId().equals(String.valueOf(id))));
                    if(liveWallPostsList.get(i).getId().equals(String.valueOf(id)))
                    {

                        Log.d(TAG,"Inside:"+liveWallPostsList.get(i).getId()+"-"+ dataSnapshot.child("id").getValue());
                        liveWallPostsList.remove(i);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
            //... ChildEventListener also defines onChildChanged, onChildRemoved,
            //    onChildMoved and onCanceled, covered in later sections.
        });


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...

            if (!isVisibleToUser) {
                // TODO stop audio playback
            }
        }
    }

    public static void setArgument(Bundle bundle)    {
        bundles=bundle;
    }

}

