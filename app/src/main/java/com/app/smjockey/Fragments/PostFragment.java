package com.app.smjockey.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.app.smjockey.Adapters.PostAdapter;
import com.app.smjockey.Models.Posts;
import com.app.smjockey.R;
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
public class PostFragment extends android.support.v4.app.Fragment{

    String user_token=null;
    String streamID;

    SharedPreferences settings;

    List<Posts> postsList;

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

    private ListView listView;


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

        listView = (ListView) rootView.findViewById(R.id.list);

        settings = getActivity().getSharedPreferences(Constants.TOKEN_FILE, 0);

        user_token=settings.getString("user_token",null);
        streamID=bundles.getString("Stream ID");
        Log.d(TAG,streamID);

        postsList=new ArrayList<>();

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

            }
        },Constants.posts_url+"/"+streamID+"/posts/?original=true",user_token);

        return rootView;
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
                postsList.add(postItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PostAdapter adapter = new PostAdapter(getActivity(), postsList);
        listView.setAdapter(adapter);
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
}