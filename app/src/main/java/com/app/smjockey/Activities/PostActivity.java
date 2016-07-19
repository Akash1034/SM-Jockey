package com.app.smjockey.Activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.app.smjockey.Adapters.ViewPagerAdapter;
import com.app.smjockey.Fragments.LiveWallFragment;
import com.app.smjockey.Fragments.PostFragment;
import com.app.smjockey.Models.Streams;
import com.app.smjockey.R;

public class PostActivity extends AppCompatActivity {

    private String TAG=com.app.smjockey.Activities.PostActivity.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Bundle bundle=getIntent().getExtras();
        Log.d(TAG,bundle.getString("Stream ID")+" "+bundle.getString("Stream Name"));
        Streams streams= (Streams) bundle.getSerializable("Stream");
        PostFragment.setArgument(bundle);
        LiveWallFragment.setArgument(bundle);
        viewPager=(ViewPager)findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PostFragment(), "Posts");
        adapter.addFragment(new LiveWallFragment(), "LiveWallPosts");
        viewPager.setAdapter(adapter);
    }




}
