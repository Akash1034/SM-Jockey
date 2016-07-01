package com.app.smjockey.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.smjockey.Models.Streams;
import com.app.smjockey.R;

import java.util.List;

/**
 * Created by Akash Srivastava on 01-07-2016.
 */
public class StreamAdapter extends RecyclerView.Adapter<StreamAdapter.ViewHolder> {

    private final String TAG = StreamAdapter.class.getSimpleName();
    private List<Streams> streamList;
    private Context mContext;
    String tags = "";
    Streams streamItem = null;

    public StreamAdapter(Context context, List<Streams> streamList) {
        super();
        this.streamList = streamList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stream_card, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        streamItem = streamList.get(position);
        holder.titleView.setText(Html.fromHtml(streamItem.getName()));

        for (int i = 0; i < streamItem.getTags().size(); i++) {
            tags += streamItem.getTags().get(i);
        }
        holder.tagsView.setText(tags.substring(0, tags.length() - 1));
        tags = "";
        holder.streamItem=streamItem;
    }

    @Override
    public int getItemCount() {
        return (null != streamList ? streamList.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView titleView;
        protected TextView tagsView;
        protected CardView cardView;
        public Streams streamItem;

        public ViewHolder(View view) {
            super(view);
            this.titleView = (TextView) view.findViewById(R.id.title);
            this.tagsView = (TextView) view.findViewById(R.id.tags);
            this.cardView = (CardView) view.findViewById(R.id.stream_card);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Log.d(TAG, "Stream id and name:" + streamItem.getId() + " " + streamItem.getName());
                    Intent intent=new Intent(mContext,PostActivity.class);
                    intent.putExtra("Stream ID",streamItem.getId());
                    intent.putExtra("Stream Name",streamItem.getName());
                    mContext.startActivity(intent);*/
                    Toast.makeText(mContext,"Card Clicked",Toast.LENGTH_SHORT).show();
                }
            });


        }
    }
}
