package com.app.smjockey.Adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.app.smjockey.Activities.PostActivity;
import com.app.smjockey.Fragments.PostFragment;
import com.app.smjockey.Models.Posts;
import com.app.smjockey.R;
import com.app.smjockey.SwipeUtils.ItemTouchHelperAdapter;
import com.app.smjockey.SwipeUtils.ItemTouchHelperViewHolder;
import com.app.smjockey.Utils.PostImageView;
import com.app.smjockey.Volley.AppController;

import java.util.List;

/**

 * Created by Akash Srivastava on 05-07-2016.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> implements ItemTouchHelperAdapter
        {


    private String TAG=com.app.smjockey.Adapters.PostAdapter.class.getSimpleName();
    private Context context;
    private LayoutInflater inflater;
    private List<Posts> postsList;
    String id;
            public static int check=0;
            int buttoncheck=0;

    ClickListener clickListener;
    PostFragment postFragment;

    ImageLoader imageLoader= AppController.getInstance().getImageLoader();
    public static int count=0;

    public PostAdapter(Context context, List<Posts> postsList, ClickListener clickListener, PostFragment postFragment) {
        this.context = context;
        this.postsList = postsList;
        this.clickListener=clickListener;
        this.postFragment=postFragment;
    }

       @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_card, null);
           return new ViewHolder(view,clickListener);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(PostAdapter.ViewHolder holder, int position) {

        if(imageLoader==null)
            imageLoader=AppController.getInstance().getImageLoader();

        Posts postItem=postsList.get(position);

        holder.name.setText(postItem.getName());

        // Converting timestamp into x ago format

        holder.username.setText(postItem.getUsername());

        // Chcek for empty status message
        if (!TextUtils.isEmpty(postItem.getText())) {
            holder.post_text.setText(postItem.getText());
            holder.post_text.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            holder.post_text.setVisibility(View.GONE);
        }

        // Checking for null feed url


        // user profile pic
        holder.profilePic.setImageUrl(postItem.getProfile_image(), imageLoader);

        // Feed image
        if (postItem.getContent_image() != null) {
            holder.postImageView.setImageUrl(postItem.getContent_image(), imageLoader);
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
        if(postItem.isSelected()) {
            holder.selectedOverlay.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));

        }
        else
            holder.selectedOverlay.setBackground(context.getDrawable(R.drawable.card_background));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return (null != postsList ? postsList.size() : 0);
    }



    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        Posts posts = postsList.get(position);
        id=posts.getId();
        posts.setSelected(true);
        ((PostActivity)context).sendDataToPostFragment(posts.getId());
        notifyItemRemoved(position);
    }

    public void addItems(List<Posts> postsList1){

        postsList = postsList1;
        notifyDataSetChanged();
    }




    class ViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder,View.OnClickListener, View.OnLongClickListener{
        TextView name;
        TextView username;
        TextView post_text;
        NetworkImageView profilePic;
        PostImageView postImageView;
        Posts postItem;

        private ClickListener listener;
        private final LinearLayout selectedOverlay;

        public ViewHolder(final View itemView, ClickListener listener) {
            super(itemView);


            this.listener = listener;
            name = (TextView) itemView.findViewById(R.id.name);
            username = (TextView) itemView
                    .findViewById(R.id.username);
            post_text = (TextView) itemView
                    .findViewById(R.id.post_text);
            profilePic = (NetworkImageView) itemView
                    .findViewById(R.id.profilePic);
            postImageView = (PostImageView) itemView
                    .findViewById(R.id.postImage);
            selectedOverlay = (LinearLayout) itemView.findViewById(R.id.selected_overlay);

            itemView.setOnClickListener(this);

            itemView.setOnLongClickListener(this);
        }



        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }


        @Override
        public void onClick(View v) {

            if (getAdapterPosition() != -1) {
                postItem = postsList.get(getAdapterPosition());
            }

            if (check==1) {
                    if (postItem.isSelected()) {
                        postItem.setSelected(false);
                        for(Posts posts : postsList)
                        {
                            if(posts.isSelected())
                            {
                                buttoncheck=1;
                                break;
                            }
                        }
                        if(buttoncheck==0) {
                            postFragment.button.setVisibility(View.INVISIBLE);
                            check = 0;
                        }
                        count--;
                    } else {
                        postItem.setSelected(true);
                        count++;
                    }
                    final Toast toast = Toast.makeText(context, count + " Selected", Toast.LENGTH_SHORT);
                    toast.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 500);

                    notifyItemChanged(getAdapterPosition());
                    listener.onItemClicked(getAdapterPosition());

//                Log.d("After click",postItem.getName());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (getAdapterPosition() != -1) {
                postItem = postsList.get(getAdapterPosition());
            }

            if (listener != null) {
                postFragment.button.setVisibility(View.VISIBLE);
                check=1;
//                longClick=1;
                //     Log.d("After Long Click",postItem.getName());
                if(postItem.isSelected()){
                    postItem.setSelected(false);
                    count--;
                }else{
                    postItem.setSelected(true);
                    count++;
                }
                final Toast toast = Toast.makeText(context,count+" Selected", Toast.LENGTH_SHORT);
                toast.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 500);
                notifyItemChanged(getAdapterPosition());
                return listener.onItemLongClicked(getAdapterPosition());
            }
            return false;
        }



    }


}
