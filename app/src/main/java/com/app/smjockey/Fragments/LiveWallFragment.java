package com.app.smjockey.Fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.app.smjockey.Adapters.LiveWallAdapter;
import com.app.smjockey.Models.LiveWallPosts;
import com.app.smjockey.Models.Streams;
import com.app.smjockey.R;
import com.app.smjockey.Utils.Constants;
import com.app.smjockey.Utils.PostImageView;
import com.app.smjockey.Volley.AppController;
import com.app.smjockey.Volley.NetworkCalls;
import com.app.smjockey.Volley.Responses;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Akash Srivastava on 04-07-2016.
 */
public class LiveWallFragment extends android.support.v4.app.Fragment implements LiveWallAdapter.OnItemClickListener {


    static Bundle bundles;

    private String TAG=com.app.smjockey.Fragments.LiveWallFragment.class.getSimpleName();

    private List<LiveWallPosts> liveWallPostsList;
    Streams streamItem;

    static String user_token=null;


    SharedPreferences settings;


    //Creating Views
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    public TextView textName;
    public TextView textUsername;
    public TextView post_text;
    public NetworkImageView profilePic;
    public PostImageView postImageView;
    public LinearLayout selectedOverlay;
    public LinearLayout firstLayout;
    public Button removeButton;
    ImageLoader imageLoader;
    LiveWallPosts liveWallPostsTempItem;
    int tempPosition;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Firebase.setAndroidContext(getContext());

        settings = getActivity().getSharedPreferences(Constants.TOKEN_FILE, 0);

        user_token = settings.getString("user_token", null);

        View rootView = inflater.inflate(R.layout.livewall_fragment, container, false);

        textName = (TextView)rootView.findViewById(R.id.name);
        textUsername = (TextView)rootView.findViewById(R.id.username);
        post_text = (TextView)rootView.findViewById(R.id.post_text);
        profilePic = (NetworkImageView)rootView.findViewById(R.id.profilePic);
        postImageView = (PostImageView)rootView.findViewById(R.id.postImage);
        selectedOverlay = (LinearLayout)rootView.findViewById(R.id.selected_overlay);
        firstLayout=(LinearLayout)rootView.findViewById(R.id.firstLayout);
        removeButton=(Button)rootView.findViewById(R.id.removeButton);

        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        liveWallPostsList=new ArrayList<>();
        adapter=new LiveWallAdapter(getActivity(),liveWallPostsList,firstLayout);
        recyclerView.setAdapter(adapter);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                streamItem= (Streams) bundles.getSerializable("Stream");
                Log.d(TAG,streamItem.getId()+streamItem.getUuid());
                getLiveWall();
            }
        },3000);


        recyclerView.addOnItemTouchListener(
                new LiveWallAdapter(getActivity(), new LiveWallAdapter.OnItemClickListener() {
                    @Override public void onItemClick(View view, final int position) {
                        // TODO Handle item



                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                getActivity());

                        // set title
                        alertDialogBuilder.setTitle(liveWallPostsList.get(position).getAccount().getName());

                        // set dialog message
                        alertDialogBuilder
                                .setMessage("LivePost")
                                .setPositiveButton("Show Now",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                        final LiveWallPosts liveWallPostItem=liveWallPostsList.get(position);
                                        if(firstLayout.getVisibility()==View.VISIBLE) {
                                            Firebase firebase = new Firebase(Constants.livewall_url + streamItem.getUuid()).child("pin");
                                            firebase.setValue(liveWallPostItem.getP_id());
                                            adapter.notifyDataSetChanged();
                                        }
                                        else if (firstLayout.getVisibility()==View.GONE)
                                        {
                                            firstLayout.setVisibility(View.VISIBLE);
                                            Firebase firebase = new Firebase(Constants.livewall_url + streamItem.getUuid()).child("pin");
                                            firebase.setValue(liveWallPostItem.getP_id());
                                            adapter.notifyDataSetChanged();
                                        }

                                        if(imageLoader==null)
                                            imageLoader= AppController.getInstance().getImageLoader();



                                        textName.setText(liveWallPostItem.getAccount().getName());


                                        textUsername.setText(liveWallPostItem.getAccount().getUsername());

                                        // Chcek for empty status message
                                        if (!TextUtils.isEmpty(liveWallPostItem.getText())) {
                                            post_text.setText(liveWallPostItem.getText());
                                            post_text.setVisibility(View.VISIBLE);
                                        } else {
                                            // status is empty, remove from view
                                            post_text.setVisibility(View.GONE);
                                        }

                                        // Checking for null feed url


                                        // user profile pic
                                        profilePic.setImageUrl(liveWallPostItem.getAccount().getProfile_image(), imageLoader);

                                        // Feed image
                                        if (liveWallPostItem.getJson().getEntities().getFull_url() != null) {
                                            postImageView.setImageUrl(liveWallPostItem.getJson().getEntities().getFull_url(), imageLoader);
                                            postImageView.setVisibility(View.VISIBLE);
                                            postImageView.setResponseObserver(new PostImageView.ResponseObserver() {
                                                @Override
                                                public void onError() {

                                                }

                                                @Override
                                                public void onSuccess() {

                                                }
                                            });
                                        } else {
                                            postImageView.setVisibility(View.GONE);
                                        }



                                        /*Firebase firebase = new Firebase(Constants.livewall_url + streamItem.getUuid()).child("pin");
                                        firebase.setValue(liveWallPostItem.getP_id());
                                        liveWallPostsList.remove(liveWallPostItem);
                                        liveWallPostsTempItem=liveWallPostsList.get(position);
                                        Log.d(TAG,"da"+liveWallPostsTempItem.getAccount().getName());
                                        tempPosition=position;
                                        adapter.notifyDataSetChanged();*/

                                        removeButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                Firebase firebase=new Firebase(Constants.livewall_url+streamItem.getUuid());
                                                firebase.child("pin").setValue(null);
                                                firstLayout.setVisibility(View.GONE);
                                            }
                                        });
                                        // if this button is clicked, close
                                        // current activity



                                    }
                                })
                                .setNegativeButton("Remove",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, just close
                                        // the dialog box and do nothing
                                        NetworkCalls.deleteData(new Responses() {
                                            @Override
                                            public void onSuccessResponse(JSONObject response) {
                                                liveWallPostsList.remove(position);

                                                adapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onSuccessResponse(String response) {

                                            }

                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        },"https://brands.eventifier.com/api/v1/streams/"+streamItem.getId()+"/posts/"+liveWallPostsList.get(position).getId()+"/livewall/",user_token);

                                    }
                                });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                    }
                })
        );



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

                Log.d(TAG,"on:"+jsonObject.toString());

                if(snapshot.child("type").getValue().equals("photo")) {

                    jsonObject.remove("photo~inserted_at");
                    ObjectMapper objectMapper=new ObjectMapper();
                    LiveWallPosts liveWallPosts = null;
                    try {
                        liveWallPosts = objectMapper.readValue(String.valueOf(jsonObject),LiveWallPosts.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, liveWallPosts.getAccount().getName());
                    liveWallPostsList.add(0,liveWallPosts);
                }

                else if(snapshot.child("type").getValue().equals("tweet"))
                {
                    jsonObject.remove("tweet~inserted_at");
                    ObjectMapper objectMapper=new ObjectMapper();
                    LiveWallPosts liveWallPosts = null;
                    try {
                        liveWallPosts = objectMapper.readValue(String.valueOf(jsonObject),LiveWallPosts.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG,"YEah:"+liveWallPosts.getAccount().getName());
                    liveWallPostsList.add(0,liveWallPosts);
                }
                adapter.notifyDataSetChanged();
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(0);
                    }
                });


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

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, String.valueOf(position));
    }
}

