package com.chzan.refresh.pullrefresh;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.chzan.refresh.R;

/**
 * 带下拉上拉头的view容器
 * Created by chenzan on 2016/11/15.
 */

public class RefreshHeaderLayout extends LoadingLayout {
    private ViewGroup headerLayout;
    private ImageView headerImage;
    private TextView headerText;
    private RotateAnimation rotateAnimation;
    private Matrix imageMatrix;

    public RefreshHeaderLayout(Context context) {
        this(context, null, 0);
    }

    public RefreshHeaderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        headerLayout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.refresh_header_view, this, false);
        headerImage = (ImageView) headerLayout.findViewById(R.id.image);
        headerText = (TextView) headerLayout.findViewById(R.id.text);
        initRoteAnim();
        imageMatrix = headerImage.getMatrix();
    }

    private void initRoteAnim() {
        rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateAnimation.setDuration(800);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatMode(Animation.RESTART);
    }

    @Override
    public void onPullReleaseState() {

    }

    @Override
    public void onRefreshCompleteState() {
        headerImage.clearAnimation();
    }

    @Override
    public void onRefreshingState() {
        headerImage.setImageMatrix(imageMatrix);
        headerImage.startAnimation(rotateAnimation);
    }

    @Override
    public void onPullingState(float pullingHeight) {
        double angle = 0;
        angle = pullingHeight * 2;
        headerImage.setRotation((float) angle);
    }

    //获得布局文件
    @Override
    public ViewGroup getLoadingLayout() {
        return headerLayout;
    }
}
