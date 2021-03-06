package com.app.smjockey.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.app.smjockey.Adapters.PostAdapter;
import com.app.smjockey.Models.Posts;
import com.app.smjockey.R;
import com.app.smjockey.SwipeUtils.ItemTouchHelperAdapter;
import com.app.smjockey.SwipeUtils.OnStartDragListener;
import com.app.smjockey.SwipeUtils.SimpleItemTouchHelperCallback;
import com.app.smjockey.Utils.Constants;
import com.app.smjockey.Volley.NetworkCalls;
import com.app.smjockey.Volley.Responses;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akash Srivastava on 04-07-2016.
 */
public class PostFragment extends android.support.v4.app.Fragment implements OnStartDragListener, SwipeRefreshLayout.OnRefreshListener{

    static String user_token=null;
    static String streamID;

    SharedPreferences settings;

    private static final String TAG = com.app.smjockey.Fragments.PostFragment.class.getSimpleName();

    JSONArray resultsArray;
    JSONObject postJSONObject;
    JSONObject json;
    JSONObject entities;
    JSONObject accountJSONObject;


    String id;
    String profile_image;
    String username;
    String type;
    String name;
    String image_url;
    String text;


    int offset=1;

    SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView.Adapter adapter;
    private LinearLayoutManager layoutManager;

    private List<Posts> postsList;

    private ItemTouchHelper mItemTouchHelper;

    static Bundle bundles;

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.post_fragment, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);


        settings = getActivity().getSharedPreferences(Constants.TOKEN_FILE, 0);

        user_token = settings.getString("user_token", null);
        streamID = bundles.getString("Stream ID");
        Log.d(TAG, streamID);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
        }
        layoutManager = new LinearLayoutManager(getActivity());
        if (recyclerView != null) {
            recyclerView.setLayoutManager(layoutManager);
        }

        postsList = new ArrayList<>();

        adapter = new PostAdapter(getActivity(), postsList);
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
        }

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback((ItemTouchHelperAdapter) adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Inside Run");
                getPosts();
            }
        });


        if (recyclerView != null) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                int pastVisiblesItems;
                int visibleItemCount;
                int totalItemCount;
                boolean isEndOfList;

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstCompletelyVisibleItemPosition();
                    //  Log.d(TAG,visibleItemCount+"-"+pastVisiblesItems+"-"+totalItemCount);

                    if (totalItemCount > visibleItemCount)
                        checkEndOfList();

                }

                private synchronized void checkEndOfList() {
                    if (pastVisiblesItems >= (totalItemCount - 3)) {
                        if (!isEndOfList) {
                            offset++;
                            getPosts();
                            Log.d(TAG, "Getting data");
                        }
                        isEndOfList = true;
                    } else {
                        isEndOfList = false;
                    }
                }
            });

        }
        return rootView;
    }

    public void getPosts()
    {
        NetworkCalls.fetchData(new Responses() {
            @Override
            public void onSuccessResponse(JSONObject response) {

                Log.d(TAG,"post response"+response.toString());
                parseJsonFeed(response);
            }

            @Override
            public void onSuccessResponse(String response) {

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                offset--;

            }
        },Constants.posts_url+"/"+streamID+"/posts/?original=true&livewall=false&page="+offset,user_token);

    }
    
    private void parseJsonFeed(JSONObject response)
    {
        try {
            resultsArray=new JSONObject(response.toString()).getJSONArray("results");
            for(int i=0;i<resultsArray.length();i++)
            {
                postJSONObject=(JSONObject)resultsArray.get(i);
                type = postJSONObject.optString("type");
                accountJSONObject = postJSONObject.getJSONObject("account");
                id = postJSONObject.optString("id");
                profile_image = accountJSONObject.optString("profile_image");
                username = accountJSONObject.optString("username");
                name = accountJSONObject.optString("name");
                json = postJSONObject.getJSONObject("json");
                text = json.optString("text");
                switch (type) {
                    case "photo":
                        entities = json.getJSONObject("entities");
                        image_url = entities.optString("full_url");
                        Log.d(TAG, "Image url:" + image_url);

                        break;
                    case "tweet":
                        image_url = null;
                        Log.d(TAG, "Tweet Image url:" + null);
                        break;
                    case "blog":
                        image_url = null;
                        entities = json.getJSONObject("entities");
                        text = "Title:" + entities.optString("title") + " Description:" + entities.optString("description");
                        Log.d(TAG, "Blog text:" + text);

                        break;
                }
                Posts postItem=new Posts();
                postItem.setId(id);
                postItem.setType(type);
                postItem.setUsername(username);
                postItem.setName(name);
                postItem.setContent_image(image_url);
                postItem.setProfile_image(profile_image);
                postItem.setText(text);
                postItem.setActive(false);
                postsList.add(postItem);
                swipeRefreshLayout.setRefreshing(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();

    }

    public static void sendPost(String id)
    {
        NetworkCalls.postData(new Responses() {
            @Override
            public void onSuccessResponse(JSONObject response) {

            }

            @Override
            public void onSuccessResponse(String response) {

                Log.d(TAG,response.toString());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,error.getLocalizedMessage());

            }
        },"https://brands.eventifier.com/api/v1/streams/"+streamID+"/posts/"+id+"/livewall/",user_token);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            Log.d("PostFragment", "Visible");

            if (!isVisibleToUser) {
                Log.d("PostFragment", "Not Visible");
                // TODO stop audio playback
            }
        }
    }

    public static void setArgument(Bundle bundle)
    {
        bundles=bundle;
    }

    @Override
    public void onRefresh() {

        offset=1;
        if(!postsList.isEmpty())
            postsList.clear();
        adapter.notifyDataSetChanged();
        getPosts();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}