package com.app.smjockey.SwipeUtils;

/**
 * Created by Akash Srivastava on 08-07-2016.
 */
public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
