package com.app.smjockey.Activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.app.smjockey.Adapters.ViewPagerAdapter;
import com.app.smjockey.Fragments.AnnouncementFragment;
import com.app.smjockey.Fragments.LiveWallFragment;
import com.app.smjockey.Fragments.PostFragment;
import com.app.smjockey.R;

public class PostActivity extends AppCompatActivity {

    private String TAG = com.app.smjockey.Activities.PostActivity.class.getSimpleName();

    private PostFragment postFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Bundle bundle = getIntent().getExtras();
        Log.d(TAG, bundle.getString("Stream ID") + " " + bundle.getString("Stream Name"));

        PostFragment.setArgument(bundle);
        LiveWallFragment.setArgument(bundle);
        AnnouncementFragment.setArgument(bundle);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        postFragment = new PostFragment();
        AnnouncementFragment announcementFragment = new AnnouncementFragment();
        LiveWallFragment liveWallFragment = new LiveWallFragment();
        adapter.addFragment(postFragment, "Posts");
        adapter.addFragment(liveWallFragment, "LiveWall");
        adapter.addFragment(announcementFragment, "Notice");
        viewPager.setAdapter(adapter);
    }


    public void sendDataToPostFragment(String id) {
        if (postFragment != null) {
            postFragment.sendPost(id);
        }
    }


}
