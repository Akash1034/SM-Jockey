package com.app.smjockey.Adapters;

/**
 * Created by Akash Srivastava on 10-07-2016.
 */
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.app.smjockey.Models.Posts;

import java.util.ArrayList;
import java.util.List;

public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    @SuppressWarnings("unused")
    private static final String TAG = SelectableAdapter.class.getSimpleName();

    List<Posts> postsList=new ArrayList<>();

    public SelectableAdapter(List<Posts> postsList) {
        this.postsList=postsList;
    }

    /**
     * Indicates if the item at position position is selected
     * @param position Position of the item to check
     * @return true if the item is selected, false otherwise
     */
    public boolean isSelected(int position) {
        return postsList.get(position).isSelected();
    }

    /**
     * Toggle the selection status of the item at a given position
     * @param position Position of the item to toggle the selection status for
     */
    public void toggleSelection(int position) {
        if(postsList.get(position).isSelected())
            postsList.get(position).setSelected(false);
        else
        postsList.get(position).setSelected(true);
        Log.d(TAG, String.valueOf(postsList.get(position).isSelected()));
        notifyItemChanged(position);
    }

    /**
     * Clear the selection status for all items
     */
    public void clearSelection() {
        for (int i=0;i<postsList.size();i++) {
            postsList.get(i).setSelected(false);
            notifyItemChanged(i);
        }
    }

    /**
     * Count the selected items
     * @return Selected items count
     */
    public int getSelectedItemCount() {
        int count=0;
        for(int i=0;i<postsList.size();i++) {
            if (postsList.get(i).isSelected())
                count++;
        }
        return count;
    }

    /**
     * Indicates the list of selected items
     * @return List of selected items ids
     */
    /*public List<Posts> getSelectedItems() {
        List<Posts> items=new ArrayList<>();
        for(int i=0;i<postsList.size();i++)
        {
            if(postsList.get(i).isSelected())
                items.add(postsList.get(i));
        }
        return items;
    }*/
}