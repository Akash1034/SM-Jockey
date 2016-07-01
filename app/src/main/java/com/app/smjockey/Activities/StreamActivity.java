package com.app.smjockey.Activities;

import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.app.smjockey.Adapters.StreamAdapter;
import com.app.smjockey.Models.Streams;
import com.app.smjockey.R;
import com.app.smjockey.Utils.Constants;
import com.app.smjockey.Volley.NetworkCalls;
import com.app.smjockey.Volley.Responses;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StreamActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private final String TAG=StreamActivity.class.getSimpleName();
    String user_token=null;

    SharedPreferences pref;


    private List<Streams> streamsList;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager layoutManager;


    JSONArray resultsArray;
    JSONObject streamObject;
    JSONArray tagArray;
    JSONObject tagObject;
    ArrayList<String> tags;
    int page=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        pref = getApplicationContext().getSharedPreferences(Constants.TOKEN_FILE,0);
        user_token=pref.getString("user_token",null);

        Log.d(TAG,"My token:"+user_token);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
        }
        layoutManager = new LinearLayoutManager(StreamActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        streamsList=new ArrayList<>();

        adapter=new StreamAdapter(StreamActivity.this,streamsList);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"Inside Run");
                getStreams();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean loading = true;
            int pastVisiblesItems;
            int visibleItemCount;
            int totalItemCount;
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
                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            page++;
                            getStreams();
                        }
                    }

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tags!=null && !tags.isEmpty())
            tags.clear();
        if(!streamsList.isEmpty())
            streamsList.clear();
    }

    private void getStreams()
    {

        NetworkCalls.fetchData(new Responses() {
            @Override
            public void onSuccessResponse(JSONObject response) {

                Log.d(TAG,"Stream response: "+ response.toString());
                try {
                    resultsArray=new JSONObject(response.toString()).getJSONArray("results");
                    for(int i=0;i<resultsArray.length();i++)
                    {
                        tags=new ArrayList<>();
                        streamObject=(JSONObject)resultsArray.get(i);
                        tagArray=new JSONObject(streamObject.toString()).getJSONArray(("tags"));
                        for (int j=0;j<tagArray.length();j++) {
                            tagObject = (JSONObject) tagArray.get(j);
                            //         Log.d(TAG, "result and tag name:" + streamObject.get("name") + " " + tagObject.get("tag"));

                            tags.add((tagObject.get("tag"))+",");

                        }
                        Streams stream= new Streams();
                        //      Log.d(TAG,"Stream id and name:"+streamObject.optString("id")+" "+streamObject.getString("name"));
                        stream.setId(streamObject.optString("id"));
                        stream.setName(streamObject.optString("name"));
                        stream.setTags(tags);
                        streamsList.add(stream);
                        swipeRefreshLayout.setRefreshing(false);
                    }

                } catch (JSONException e) {
                    Log.d(TAG,"Stream Response Error:"+e.toString());
                }
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onSuccessResponse(String response) {

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Volley Error: " + error.toString());
                swipeRefreshLayout.setRefreshing(false);
            }
        },Constants.streams_url+page,user_token);

    }

    @Override
    public void onRefresh() {
        if(tags!=null && !tags.isEmpty())
            tags.clear();
        if(!streamsList.isEmpty())
            streamsList.clear();
        getStreams();
    }
}
