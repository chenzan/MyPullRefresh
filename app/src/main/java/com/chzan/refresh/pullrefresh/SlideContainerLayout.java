package com.chzan.refresh.pullrefresh;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.chzan.refresh.R;

/**
 * 可以下拉上拉的容器
 * Created by chenzan on 2016/11/14.
 */

public class SlideContainerLayout extends FrameLayout {
    private Context mContext;
    private int mTouchSlop;
    private View mRefreshHeaderView;
    private View mContainerView;
    private int maxPullUpHeight = -1;///最大的下拉高度
    private int maxPullDownHeight = -1;///最大的
    private ValueAnimator valueAnimator;
    private float mInitDownY;
    private boolean mIsBeingDragged = false;
    private int mDirection = 1;//下拉为1，上拉为-1
    private static final int INVALID_POINTER = -1;
    private int mActivePointerId = INVALID_POINTER;
    private int pointerIndex;

    public SlideContainerLayout(Context context) {
        this(context, null, 0);
    }

    public SlideContainerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideContainerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        mRefreshHeaderView = LayoutInflater.from(mContext).inflate(R.layout.refresh_header_view, this, false);
        mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        setRefreshHeaderView();
    }

    //set refresh header view
    private void setRefreshHeaderView() {
        if (mRefreshHeaderView != null)
            this.addView(mRefreshHeaderView, 0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0)
                    return false;
                float methodEventY = ev.getY(pointerIndex);
//                if (methodEventY < 0)
//                    return false;
                mInitDownY = methodEventY;
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER)
                    return false;
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                float y = ev.getY(pointerIndex);
                Log.e("onInterceptTouchEvent", "move");
                if (y > mInitDownY) {//下拉
                    if (Math.abs(y - mInitDownY) > mTouchSlop && !canChildScrollDown() && !mIsBeingDragged)
                        mIsBeingDragged = true;
                    mDirection = 1;
                } else {//上拉
                    if (Math.abs(y - mInitDownY) > mTouchSlop && !canChildScrollUp() && !mIsBeingDragged)
                        mIsBeingDragged = true;
                    mDirection = -1;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.e("onInterceptTouchEvent", "up_cancel");
                mIsBeingDragged = false;
                break;
        }
        return mIsBeingDragged;
    }

    //判断当前内容空间是否可以滚动
    public boolean canChildScrollDown() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mContainerView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mContainerView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mContainerView, -1) || mContainerView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mContainerView, -1);
        }
    }

    //判断当前内容空间是否可以滚动
    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mContainerView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mContainerView;
                return absListView.getChildCount() > 0
                        && (absListView.getLastVisiblePosition() < absListView.getChildCount() - 1 ||
                        absListView.getChildAt(absListView.getChildCount() - 1).getBottom() < absListView.getPaddingBottom());
            } else {
                return ViewCompat.canScrollVertically(mContainerView, 1) || mContainerView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mContainerView, 1);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = event.getPointerId(0);
                return true;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = event.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0)
                    return false;
                Log.e("onTouchEvent", "move");
                int distance = getOffsetDistance(event);
                mContainerView.offsetTopAndBottom(distance);
                break;
            case MotionEventCompat.ACTION_POINTER_DOWN:
                pointerIndex = event.getActionIndex();
                if (pointerIndex < 0)
                    return false;
                mActivePointerId = event.getPointerId(pointerIndex);
                mInitDownY = event.getY(pointerIndex);//重新设置初始位置为新的按下点
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                pointerIndex = event.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0)
                    return false;
                Log.e("onTouchEvent", "up_cancel");
                releaseDrag();
                mActivePointerId = INVALID_POINTER;
                break;
        }
        return true;
    }

    //获得移动的距离
    private int getOffsetDistance(MotionEvent event) {
        float distance = event.getY(pointerIndex) - mInitDownY;
        mInitDownY = event.getY(pointerIndex);//重新赋值初始的y坐标
        float percent;
        int canPullHeight;
        if (mDirection < 0) {//上拉 滑动阻力效果
            percent = 1 - event.getY(pointerIndex) / this.getMeasuredHeight();
            canPullHeight = maxPullUpHeight;
        } else {
            canPullHeight = maxPullDownHeight;
            percent = event.getY(pointerIndex) / this.getMeasuredHeight();
        }
        distance = distance * (1 - percent);
        if (Math.abs(mContainerView.getTop()) >= canPullHeight && canPullHeight > 0/*设置了最大距离*/)
            distance = 0;
        return distance == 0 ? 0 : (int) (distance + 0.5f);
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    /**
     * 设置最大上拉高度
     *
     * @param maxPullUpHeight
     */
    public void setMaxPullUpHeight(int maxPullUpHeight) {
        if (maxPullUpHeight < 0)
            maxPullUpHeight = INVALID_POINTER;
        this.maxPullUpHeight = maxPullUpHeight;
    }

    /**
     * 设置值最大下拉高度
     *
     * @param maxPullDownHeight
     */
    public void setMaxPullDownHeight(int maxPullDownHeight) {
        if (maxPullDownHeight < 0)
            maxPullDownHeight = INVALID_POINTER;
        this.maxPullDownHeight = maxPullDownHeight;
    }

    //释放时
    private void releaseDrag() {
        mContainerView.clearAnimation();
        valueAnimator = ValueAnimator.ofFloat(mContainerView.getTop(), 0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFaction = valueAnimator.getAnimatedFraction();
                mContainerView.offsetTopAndBottom((int) (-mContainerView.getTop() * animatedFaction));
            }
        });
        valueAnimator.setDuration(500);
        valueAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (valueAnimator != null)
            valueAnimator.cancel();
    }

    @Override
    protected void onFinishInflate() {
        mContainerView = getChildAt(getChildCount() - 1);
        mContainerView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        mContainerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        getChildAt(getChildCount() - 1).setBackgroundColor(Color.WHITE);
    }
}
