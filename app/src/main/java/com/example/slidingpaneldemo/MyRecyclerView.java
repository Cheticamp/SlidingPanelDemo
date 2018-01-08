package com.example.slidingpaneldemo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class MyRecyclerView extends RecyclerView {
    private AppBarTracking mAppBarTracking = null;
    @SuppressWarnings("FieldCanBeLocal")
    private View mView = null;
    @SuppressWarnings("FieldCanBeLocal")
    private int mTopPos = 0;
    private LinearLayoutManager mLayoutManager = null;

    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    interface AppBarTracking {
        boolean isAppBarIdle();

        boolean isAppBarExpanded();
    }

    /*
     * dispatchNestedPreScroll addresses appbar scrolling defects introduced in API 26. Refer to
     * <a href="https://issuetracker.google.com/issues/65448468"  target="_blank">"Bug in design support library"</a>
     */

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow,
                                           int type) {
        if (type == ViewCompat.TYPE_NON_TOUCH && mAppBarTracking.isAppBarIdle()
            && isNestedScrollingEnabled()) {
            if (dy > 0) {
                if (mAppBarTracking.isAppBarExpanded()) {
                    consumed[1] = dy;
                    return true;
                }
            } else {
                mTopPos = mLayoutManager.findFirstVisibleItemPosition();
                if (mTopPos == 0) {
                    mView = mLayoutManager.findViewByPosition(mTopPos);
                    if (-mView.getTop() + dy <= 0) {
                        consumed[1] = dy - mView.getTop();
                        return true;
                    }
                }
            }
        }
        if (dy < 0 && type == ViewCompat.TYPE_TOUCH && mAppBarTracking.isAppBarExpanded()) {
            consumed[1] = dy;
            return true;
        }

        boolean returnValue = super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
        if (offsetInWindow != null && !isNestedScrollingEnabled() && offsetInWindow[1] != 0)
            offsetInWindow[1] = 0;

        return returnValue;
    }

    @Override
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        super.setLayoutManager(layoutManager);
        mLayoutManager = (LinearLayoutManager) layoutManager;
    }

    public void setAppBarTracking(AppBarTracking appBarTracking) {
        mAppBarTracking = appBarTracking;
    }

    @SuppressWarnings("unused")
    private static final String TAG = "MyRecyclerView";
}
