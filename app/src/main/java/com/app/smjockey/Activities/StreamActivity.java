package com.app.smjockey.Activities;

import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.app.smjockey.Adapters.StreamAdapter;
import com.app.smjockey.Models.Streams;
import com.app.smjockey.R;
import com.app.smjockey.Utils.Constants;
import com.app.smjockey.Volley.NetworkCalls;
import com.app.smjockey.Volley.Responses;
import com.firebase.client.Firebase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StreamActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private final String TAG=StreamActivity.class.getSimpleName();
    String user_token=null;

    SharedPreferences pref;
    SharedPreferences.Editor editor;


    private List<Streams> streamsList;
    private List<Streams> tempList;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager layoutManager;


    JSONArray resultsArray;
    JSONObject streamObject;
    JSONArray tagArray;
    JSONObject tagObject;
    ArrayList<String> tags;
    int page=1;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pref = getApplicationContext().getSharedPreferences(Constants.TOKEN_FILE,0);
        editor=pref.edit();
        user_token=pref.getString("user_token",null);

        Log.d(TAG,"My token:"+user_token);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
        }
        layoutManager = new LinearLayoutManager(StreamActivity.this);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(layoutManager);
        }

        streamsList=new ArrayList<>();

        adapter=new StreamAdapter(StreamActivity.this,streamsList);
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
        }

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"Inside Run");
                getStreams();
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
                            page++;
                            getStreams();
                            Log.d(TAG,"Getting data");
                        }
                        isEndOfList = true;
                    } else {
                        isEndOfList = false;
                    }
                }
            });
        }

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
                page--;
                swipeRefreshLayout.setRefreshing(false);
            }
        },Constants.streams_url+page,user_token);

    }

    @Override
    public void onRefresh() {
        page=1;
        Log.d(TAG,"page:"+page);
        if(tags!=null && !tags.isEmpty())
            tags.clear();
        if(!streamsList.isEmpty())
            streamsList.clear();
        adapter.notifyDataSetChanged();
        getStreams();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            editor.clear();
            editor.commit();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
