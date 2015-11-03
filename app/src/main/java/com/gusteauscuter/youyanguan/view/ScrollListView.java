package com.gusteauscuter.youyanguan.view;

/**
 * Created by Strang on 2015/9/27.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;


/**
 * Triggers a event when scrolling reaches bottom.
 *
 * Created by martinsandstrom on 2013-05-02.
 *
 * Usage:
 *
 *  listView.setOnBottomReachedListener(
 *      new ScrollListView.OnBottomReachedListener() {
 *          @Override
 *          public void onBottomReached() {
 *              // do something
 *          }
 *      }
 *  );
 */
public class ScrollListView extends ListView implements AbsListView.OnScrollListener {
    private OnBottomReachedListener mListener;

    /**
     * Scroll position offset value to trigger earlier bottom reached events.
     */
    private int mOffset = 0;
    private boolean isTriggeredOnce;

    public ScrollListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        defineScrolling();
    }

    public ScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        defineScrolling();
    }

    public ScrollListView(Context context) {
        super(context);
        defineScrolling();
    }

    /**
     * Defines scrolling behaviour by subscribing a scroll listener.
     */
    private void defineScrolling() {
        this.setOnScrollListener(this);
    }

    /**
     * Removes internal scroll listener.
     */
    public void reset() {
        this.setOnScrollListener(null);
    }


    // Listeners

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {


    }



    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {

        int position = firstVisibleItem+visibleItemCount;
        int limit = totalItemCount - mOffset;
        if (position >= limit && totalItemCount > 0 && mListener != null && !isTriggeredOnce) {
            if (view.getChildAt(visibleItemCount - 1).getBottom() <= view.getHeight()) {
                mListener.onBottomReached();
            }
        }
//        // Check if bottom has been reached
//        if (position >= limit && totalItemCount > 0) {
//            if (mListener != null && !isTriggeredOnce) {
//                    mListener.onBottomReached();
//
//            }
//        }
    }


    // Getters & Setters

    public OnBottomReachedListener getOnBottomReachedListener() {
        return mListener;
    }

    public void setOnBottomReachedListener(
            OnBottomReachedListener onBottomReachedListener) {
        this.mListener = onBottomReachedListener;
    }

    public int getOffset() {
        return mOffset;
    }

    public void setOffset(int offset) {
        mOffset = offset;
    }

    public void setTriggeredOnce(boolean b) {
        isTriggeredOnce = b;
    }


    /**
     * Event listener.
     */
    public interface OnBottomReachedListener {
        public void onBottomReached();
    }
}
