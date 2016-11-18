package com.chzan.refresh.pullrefresh;

import android.support.annotation.IntDef;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by chenzan on 2016/11/15.
 */

public interface ILoadingLayout {
    int PULLING_STATE = 0;
    int REFRESHING_STATE = 1;
    int REFRESH_COMPLETE_STATE = 3;
    int PULL_RELEASE = 4;

    @IntDef({PULLING_STATE, REFRESHING_STATE, REFRESH_COMPLETE_STATE, PULL_RELEASE})
    @Retention(RetentionPolicy.SOURCE)
    @interface PullState {
    }

    //下拉状态各种变化的数值
    void onPullingRefresh(float pullingHeight, float headerHeight, @PullState int pullState);

    /**
     * 拉动的状态
     *
     * @param pullingHeight
     */
    void onPullingState(float pullingHeight);

    /**
     * 刷新的状态
     */
    void onRefreshingState();

    /**
     * 刷新完成状态
     */
    void onRefreshCompleteState();

    /**
     * 释放时的状态
     */
    void onPullReleaseState();

    //获取加载的布局
    ViewGroup getLoadingLayout();
}
