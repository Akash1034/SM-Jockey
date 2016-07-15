package com.app.smjockey.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.smjockey.Models.LiveWallPosts;
import com.app.smjockey.Models.Streams;
import com.app.smjockey.R;
import com.app.smjockey.Utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

/**
 * Created by Akash Srivastava on 04-07-2016.
 */
public class LiveWallFragment extends android.support.v4.app.Fragment {


    static Bundle bundles;

    private String TAG=com.app.smjockey.Fragments.LiveWallFragment.class.getSimpleName();

    Streams streamItem;
    public LiveWallFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Firebase.setAndroidContext(getContext());

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                streamItem= (Streams) bundles.getSerializable("Stream");
                Log.d(TAG,streamItem.getId()+streamItem.getUuid());
                final Firebase firebase=new Firebase(Constants.livewall_url+streamItem.getUuid()+"/data");
                firebase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {


                        for (final DataSnapshot postSnapshot: snapshot.getChildren()) {
                            if (postSnapshot.child("type").getValue().equals("tweet")) {
                                firebase.child(postSnapshot.getKey()).child("tweet~inserted_at").runTransaction(new Transaction.Handler() {
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        mutableData.setValue(null); // This removes the node.
                                        return Transaction.success(mutableData);
                                    }

                                    public void onComplete(FirebaseError error, boolean b, DataSnapshot data) {
                                        // Handle completion
                                        LiveWallPosts posts=postSnapshot.getValue(LiveWallPosts.class);
                                        Log.d(TAG,"Tweet Post:"+posts.getId()+"-"+posts.getType());
                                    }
                                });
                            }
                            else if(postSnapshot.child("type").getValue().equals("photo"))
                            {
                                Log.d(TAG, String.valueOf(postSnapshot.child("photo~inserted_at").getValue()));
                                firebase.child(postSnapshot.getKey()).child("photo~inserted_at").runTransaction(new Transaction.Handler() {
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        mutableData.setValue(null);
                                        return Transaction.success(mutableData);
                                    }
                                    public void onComplete(FirebaseError error, boolean b, DataSnapshot data) {

                                        Log.d(TAG,"After:"+String.valueOf(postSnapshot.child("photo~inserted_at").getValue()));
                                        LiveWallPosts posts=postSnapshot.getValue(LiveWallPosts.class);
                                        Log.d(TAG,"Photo Post:"+posts.getId()+"-"+posts.getType());
                                    }
                                });


                                //        Posts posts=postSnapshot.getValue(Posts.class);

                                //   Log.d(TAG,posts.getId()+"-"+posts.getType());


                            }
                        }
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });
            }
        },2000);









        return inflater.inflate(R.layout.livewall_fragment, container, false);
    }

    public void getLiveWall()
    {



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