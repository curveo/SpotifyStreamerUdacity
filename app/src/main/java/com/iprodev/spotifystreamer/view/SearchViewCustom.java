package com.iprodev.spotifystreamer.view;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;

/**
 * Created by curtis on 7/6/15.
 */
public class SearchViewCustom extends SearchView {

    private SearchViewCallback mCallback;

    public interface SearchViewCallback {
        public void onCollapsed();
//        public void onExpanded();
    }

    public SearchViewCustom(Context context) {
        super(context);
    }

    public SearchViewCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchViewCustom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCallback(SearchViewCallback callback) {
        mCallback = callback;
    }

    @Override
    public void onActionViewCollapsed() {
        super.onActionViewCollapsed();
        if(mCallback != null)
            mCallback.onCollapsed();
    }

    @Override
    public void onActionViewExpanded() {
        super.onActionViewExpanded();
    }
}
