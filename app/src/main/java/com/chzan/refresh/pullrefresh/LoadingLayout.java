package com.chzan.refresh.pullrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by chenzan on 2016/11/18.
 */

public abstract class LoadingLayout extends FrameLayout implements ILoadingLayout {
    public float loadingViewHeight;
    public float pullHeight;

    public LoadingLayout(Context context) {
        super(context);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onPullingRefresh(float pullingHeight, float viewHeight, @PullState int pullState) {
        loadingViewHeight = viewHeight;
        pullHeight = pullingHeight;
        switch (pullState) {
            case ILoadingLayout.PULLING_STATE:
                onPullingState(pullingHeight);
                break;
            case ILoadingLayout.PULL_RELEASE:
                onPullReleaseState();
                break;
            case ILoadingLayout.REFRESHING_STATE:
                onRefreshingState();
                break;
            case ILoadingLayout.REFRESH_COMPLETE_STATE:
                onRefreshCompleteState();
                break;
        }
    }

}
