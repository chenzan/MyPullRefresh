package com.chzan.refresh.pullrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chzan.refresh.R;

/**
 * Created by chenzan on 2016/11/15.
 */

public class RefreshFooterLayout extends LoadingLayout {
    private ViewGroup footerLayout;
    private ProgressBar footerProgress;
    private TextView footerText;

    public RefreshFooterLayout(Context context) {
        this(context, null, 0);
    }

    public RefreshFooterLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshFooterLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        footerLayout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.refresh_footer_view, this, false);
        footerProgress = (ProgressBar) footerLayout.findViewById(R.id.progress);
        footerText = (TextView) footerLayout.findViewById(R.id.text);
        footerProgress.setMax(1);
    }

    @Override
    public void onPullingState(float pullingHeight) {
        footerProgress.setProgress((int) (pullHeight / loadingViewHeight));
    }

    @Override
    public void onRefreshingState() {
        footerProgress.setProgress((int) (pullHeight / loadingViewHeight));
    }

    @Override
    public void onRefreshCompleteState() {
        footerProgress.setProgress((int) (pullHeight / loadingViewHeight));
    }

    @Override
    public void onPullReleaseState() {
        footerProgress.setProgress((int) (pullHeight / loadingViewHeight));
    }

    //获得布局文件
    @Override
    public ViewGroup getLoadingLayout() {
        return footerLayout;
    }
}
