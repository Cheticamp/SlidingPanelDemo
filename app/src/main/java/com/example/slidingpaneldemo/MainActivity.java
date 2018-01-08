package com.example.slidingpaneldemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements MyRecyclerView.AppBarTracking {
    private MyRecyclerView mNestedView;
    private int mAppBarOffset = 0;
    private boolean mAppBarIdle = true;
    private int mAppBarMaxOffset = 0;
    private AppBarLayout mAppBar;
    private boolean mIsExpanded = false;
    private ImageView mArrowImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LinearLayout expandCollapse;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        expandCollapse = findViewById(R.id.expandCollapseButton);
        mArrowImageView = findViewById(R.id.arrowImageView);
        mNestedView = findViewById(R.id.nestedView);
        mAppBar = findViewById(R.id.appBar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mAppBar.post(new Runnable() {
            @Override
            public void run() {
                mAppBarMaxOffset = -mAppBar.getTotalScrollRange();

                // Don't let the appbar be dragged open or closed with a direct touch.
                CoordinatorLayout.LayoutParams lp =
                    (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
                AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) lp.getBehavior();
                behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                    @Override
                    public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                        return false;
                    }
                });
            }
        });
        mNestedView.setAppBarTracking(this);
        mNestedView.setLayoutManager(new LinearLayoutManager(this));
        mNestedView.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(
                    LayoutInflater.from(parent.getContext())
                        .inflate(android.R.layout.simple_list_item_1, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((TextView) holder.itemView.findViewById(android.R.id.text1))
                    .setText("Item " + position);
            }

            @Override
            public int getItemCount() {
                return 200;
            }

            class ViewHolder extends RecyclerView.ViewHolder {
                public ViewHolder(View view) {
                    super(view);
                }
            }
        });

        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mAppBarOffset = verticalOffset;
                int totalScrollRange = appBarLayout.getTotalScrollRange();
                float progress = (float) (-verticalOffset) / (float) totalScrollRange;
                mArrowImageView.setRotation(-progress * 180);
                mIsExpanded = verticalOffset == 0;
                mAppBarIdle = mAppBarOffset >= 0 || mAppBarOffset <= mAppBarMaxOffset;
            }
        });

        expandCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExpandAndCollapseEnabled(true);
                if (mIsExpanded) {
                    setExpandAndCollapseEnabled(false);
                }
                mIsExpanded = !mIsExpanded;
                mNestedView.stopScroll();
                mAppBar.setExpanded(mIsExpanded, true);
            }
        });
    }

    private void setExpandAndCollapseEnabled(boolean enabled) {
        if (mNestedView.isNestedScrollingEnabled() != enabled) {
            mNestedView.setNestedScrollingEnabled(enabled);
        }
    }

    @Override
    public boolean isAppBarExpanded() {
        return mAppBarOffset == 0;
    }

    @Override
    public boolean isAppBarIdle() {
        return mAppBarIdle;
    }
}
