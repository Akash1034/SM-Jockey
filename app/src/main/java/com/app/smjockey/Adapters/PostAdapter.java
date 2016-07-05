package com.app.smjockey.Adapters;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.app.smjockey.Models.Posts;
import com.app.smjockey.PostImageView;
import com.app.smjockey.R;
import com.app.smjockey.Volley.AppController;

import java.util.List;

/**
 * Created by Akash Srivastava on 05-07-2016.
 */
public class PostAdapter extends BaseAdapter {


    private Activity activity;
    private LayoutInflater inflater;
    private List<Posts> postsList;

    ImageLoader imageLoader= AppController.getInstance().getImageLoader();

    public PostAdapter(Activity activity, List<Posts> postsList) {
        this.activity = activity;
        this.postsList = postsList;
    }

    @Override
    public int getCount() {
        return postsList.size();
    }

    @Override
    public Object getItem(int position) {
        return postsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(inflater==null)
            inflater=(LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView==null)
            convertView=inflater.inflate(R.layout.post_card,null);

        if(imageLoader==null)
            imageLoader=AppController.getInstance().getImageLoader();

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView username = (TextView) convertView
                .findViewById(R.id.username);
        TextView post_text = (TextView) convertView
                .findViewById(R.id.post_text);
        NetworkImageView profilePic = (NetworkImageView) convertView
                .findViewById(R.id.profilePic);
        PostImageView postImageView = (PostImageView) convertView
                .findViewById(R.id.postImage);

        Posts postItem=postsList.get(position);

        name.setText(postItem.getName());

        // Converting timestamp into x ago format

        username.setText(postItem.getUsername());

        // Chcek for empty status message
        if (!TextUtils.isEmpty(postItem.getText())) {
            post_text.setText(postItem.getText());
            post_text.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            post_text.setVisibility(View.GONE);
        }

        // Checking for null feed url


        // user profile pic
        profilePic.setImageUrl(postItem.getProfile_image(), imageLoader);

        // Feed image
        if (postItem.getContent_image() != null) {
            postImageView.setImageUrl(postItem.getContent_image(), imageLoader);
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



        return convertView;

    }
}
