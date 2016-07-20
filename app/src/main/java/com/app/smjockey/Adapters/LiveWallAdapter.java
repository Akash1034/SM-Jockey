package com.app.smjockey.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.app.smjockey.Models.LiveWallPosts;
import com.app.smjockey.R;
import com.app.smjockey.Utils.PostImageView;
import com.app.smjockey.Volley.AppController;

import java.util.List;

/**
 * Created by Akash Srivastava on 15-07-2016.
 */
public class LiveWallAdapter extends RecyclerView.Adapter<LiveWallAdapter.ViewHolder>implements RecyclerView.OnItemTouchListener {

    private String TAG=com.app.smjockey.Adapters.PostAdapter.class.getSimpleName();
    private Context context;
    private LayoutInflater inflater;
    private List<LiveWallPosts> liveWallPostList;

    private OnItemClickListener mListener;
    LinearLayout firstLayout;


    GestureDetector mGestureDetector;


    ImageLoader imageLoader= AppController.getInstance().getImageLoader();

    public LiveWallAdapter(Context context, List<LiveWallPosts> liveWallPostList,LinearLayout firstLayout) {
        this.context = context;
        this.liveWallPostList=liveWallPostList;
        this.firstLayout=firstLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_card, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(imageLoader==null)
            imageLoader=AppController.getInstance().getImageLoader();

        LiveWallPosts liveWallPostItem=liveWallPostList.get(position);

        holder.name.setText(liveWallPostItem.getAccount().getName());


        holder.username.setText(liveWallPostItem.getAccount().getUsername());

        // Chcek for empty status message
        if (!TextUtils.isEmpty(liveWallPostItem.getText())) {
            holder.post_text.setText(liveWallPostItem.getText());
            holder.post_text.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            holder.post_text.setVisibility(View.GONE);
        }

        // Checking for null feed url


        // user profile pic
        holder.profilePic.setImageUrl(liveWallPostItem.getAccount().getProfile_image(), imageLoader);

        // Feed image
        if (liveWallPostItem.getJson().getEntities().getFull_url() != null) {
            holder.postImageView.setImageUrl(liveWallPostItem.getJson().getEntities().getFull_url(), imageLoader);
            holder.postImageView.setVisibility(View.VISIBLE);
            holder.postImageView.setResponseObserver(new PostImageView.ResponseObserver() {
                @Override
                public void onError() {

                }

                @Override
                public void onSuccess() {

                }
            });
        } else {
            holder.postImageView.setVisibility(View.GONE);
        }
        holder.liveWallPostItem=liveWallPostItem;
    }

    @Override
    public int getItemCount() {
        return liveWallPostList.size();
    }



    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }



    public LiveWallAdapter(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }



    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView username;
        TextView post_text;
        NetworkImageView profilePic;
        PostImageView postImageView;
        LiveWallPosts liveWallPostItem;



        public ViewHolder(final View itemView) {
            super(itemView);


            name = (TextView) itemView.findViewById(R.id.name);
            username = (TextView) itemView
                    .findViewById(R.id.username);
            post_text = (TextView) itemView
                    .findViewById(R.id.post_text);
            profilePic = (NetworkImageView) itemView
                    .findViewById(R.id.profilePic);
            postImageView = (PostImageView) itemView
                    .findViewById(R.id.postImage);


        }
    }





}
