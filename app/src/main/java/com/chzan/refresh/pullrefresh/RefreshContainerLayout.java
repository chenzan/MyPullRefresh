package com.chzan.refresh.pullrefresh;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;

/**
 * Created by chenzan on 2016/11/14.
 */

public class RefreshContainerLayout extends FrameLayout {
    private static final int INVALID_VALUE = -1;
    private Context mContext;
    private int mTouchSlop;
    private View mContainerView;
    private int maxPullDownHeight = INVALID_VALUE;///最大的下拉高度
    private int maxPullUpHeight = INVALID_VALUE;///最大的上拉高度
    private ValueAnimator valueAnimator;
    private float mInitDownY;
    private boolean mIsBeingDragged = false;
    private int mDirection = 1;//下拉为1，上拉为-1
    private OnRefreshListener mOnRefreshListener;
    private View mRefreshHeaderView;
    private View mRefreshFooterView;
    private ILoadingLayout refreshHeaderLayout;
    private ILoadingLayout refreshFooterLayout;
    private int refreshHeaderHeight = INVALID_VALUE;//刷新显示的头的高度
    private int refreshFooterHeight = INVALID_VALUE;//刷新显示的尾的高度
    private int finishHeight = INVALID_VALUE;
    private boolean mRefreshing = false;
    private int mActivePointerId;
    private int pointerIndex;

    public RefreshContainerLayout(Context context) {
        this(context, null, 0);
    }

    public RefreshContainerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshContainerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        refreshHeaderLayout = new RefreshHeaderLayout(mContext);
        refreshFooterLayout = new RefreshFooterLayout(mContext);
        mRefreshHeaderView = refreshHeaderLayout.getLoadingLayout();
        mRefreshFooterView = refreshFooterLayout.getLoadingLayout();
        mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        setRefreshLoadView();
    }

    //set refresh header view
    private void setRefreshLoadView() {
        if (mRefreshHeaderView != null)
            this.addView(mRefreshHeaderView, 0);
        if (mRefreshFooterView != null) {
            FrameLayout.LayoutParams layoutParams = (LayoutParams) mRefreshFooterView.getLayoutParams();
            layoutParams.gravity = Gravity.BOTTOM;
            this.addView(mRefreshFooterView, 1);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0)
                    return false;
                mInitDownY = ev.getY(pointerIndex);
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                float y = ev.getY(pointerIndex);
                Log.e("onInterceptTouchEvent", "move");
                if (mRefreshing)
                    return true;
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
                mActivePointerId = INVALID_VALUE;
                break;
        }
        return mIsBeingDragged;
    }

    //判断当前内容空间是否可以滚动
    private boolean canChildScrollDown() {
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
    private boolean canChildScrollUp() {
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
                pointerIndex = event.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0)
                    return false;
                return true;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = event.findPointerIndex(mActivePointerId);
                Log.e("onTouchEvent", "move");
                int distance = getOffsetDistance(event);
                //加判断解决向边界外拖动问题
                if (mDirection > 0 && mContainerView.getTop() >= 0 && !canChildScrollDown() ||
                        mDirection < 0 && mContainerView.getTop() <= 0 && !canChildScrollUp()) {
                    Log.e("dddddddd", distance + "");
                    mContainerView.offsetTopAndBottom(distance);
                    setRefreshHeaderFooterState(ILoadingLayout.PULLING_STATE);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                pointerIndex = event.getActionIndex();
                if (pointerIndex < 0)
                    return false;
                mActivePointerId = event.getPointerId(pointerIndex);
                pointerIndex = event.findPointerIndex(mActivePointerId);
                //重新赋值初始位置
                mInitDownY = event.getY(pointerIndex);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.e("onTouchEvent", "up_cancel");
                releaseDrag(mDirection);
                mActivePointerId = INVALID_VALUE;
                break;
        }
        return false;
    }

    private void onSecondaryPointerUp(MotionEvent event) {
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = event.getPointerId(newPointerIndex);
        }
    }

    //获得移动的距离
    private int getOffsetDistance(MotionEvent event) {
        float distance = event.getY(pointerIndex) - mInitDownY;
        mInitDownY = event.getY(pointerIndex);//重新赋值初始的y坐标
        float percent;
        int canPullHeight;
        if (mDirection < 0) {//上拉 滑动阻力效果
            percent = 1 - event.getY(pointerIndex) / this.getMeasuredHeight();
            //默认上拉最大高度为footerView的高度
            if (maxPullUpHeight == INVALID_VALUE && mRefreshFooterView != null)
                maxPullUpHeight = mRefreshFooterView.getMeasuredHeight();
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

    //释放时
    private void releaseDrag(final int direction) {
        mContainerView.clearAnimation();
        getStopPosition(direction);
        valueAnimator = ValueAnimator.ofFloat(mContainerView.getTop(), finishHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFaction = valueAnimator.getAnimatedFraction();
                mContainerView.offsetTopAndBottom((int) ((-mContainerView.getTop() + finishHeight) *
                        animatedFaction));
                //刷新中的回调
                if (!mRefreshing && animatedFaction == 1 && ((mContainerView.getTop() >= refreshHeaderHeight && mDirection > 0) ||
                        (Math.abs(mContainerView.getTop()) >= refreshFooterHeight && mDirection < 0))) {//和高度做对比 确认是刷新触发
                    mRefreshing = true;
                    if (mOnRefreshListener != null && mContainerView.getTop() > 0 && mDirection > 0)
                        mOnRefreshListener.onPullDownRefresh();
                    if (mOnRefreshListener != null && mContainerView.getTop() < 0 && mDirection < 0)
                        mOnRefreshListener.onPullUpRefresh();
                    setRefreshHeaderFooterState(ILoadingLayout.REFRESHING_STATE);
                } else {
                    setRefreshHeaderFooterState(ILoadingLayout.PULL_RELEASE);
                }
            }
        });
        //根据距离调节动画时间
        valueAnimator.setDuration(Math.abs((long) (1000 * ((mContainerView.getTop() + 0f) /
                (this.getMeasuredHeight() - finishHeight)))));
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
    }

    //获得应该停留的距离
    private float getStopPosition(int direction) {
        if (direction == 1)//判断方向 下拉
            finishHeight = refreshHeaderHeight < 0 ? 0 : refreshHeaderHeight;
        else
            finishHeight = -(refreshFooterHeight < 0 ? 0 : refreshFooterHeight);
        if (Math.abs(mContainerView.getTop()) < Math.abs(finishHeight))//判断松手的位置 设置结束停留的位置
            finishHeight = 0;
        return finishHeight;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (valueAnimator != null)
            valueAnimator.cancel();
    }

    //1
    @Override
    protected void onFinishInflate() {
        //fill the container to set background color by white.
        mContainerView = getChildAt(getChildCount() - 1);
        mContainerView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        mContainerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        getChildAt(getChildCount() - 1).setBackgroundColor(Color.WHITE);
//        Log.e("refresh", "onFinishInflate");
    }

    //末
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        Log.e("refresh", "onSizeChanged");
        if (mRefreshHeaderView != null)
            refreshHeaderHeight = mRefreshHeaderView.getMeasuredHeight();
        if (refreshFooterLayout != null)
            refreshFooterHeight = mRefreshFooterView.getMeasuredHeight();
    }

    /**
     * 设置下拉的最大距离
     *
     * @param maxPullDownHeight
     */
    public void setMaxPullDownHeight(int maxPullDownHeight) {
        this.maxPullDownHeight = Math.max(refreshHeaderHeight, maxPullDownHeight);
    }

    /**
     * 设置上拉的最大距离
     * 默认为footerView的高度
     *
     * @param maxPullUpHeight
     */
    public void setMaxPullUpHeight(int maxPullUpHeight) {
        this.maxPullUpHeight = Math.max(refreshFooterHeight, maxPullUpHeight);
    }

    /**
     * 设置完成状态
     */
    public void setRefreshComplete() {
        mRefreshing = false;
        refreshComplete();
    }

    //刷新完成
    private void refreshComplete() {
        mContainerView.clearAnimation();
        if (valueAnimator != null)
            valueAnimator.cancel();
        valueAnimator = ValueAnimator.ofFloat(mContainerView.getTop(), 0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (mContainerView != null) {
                    float animatedFaction = valueAnimator.getAnimatedFraction();
                    mContainerView.offsetTopAndBottom((int) ((-mContainerView.getTop()) *
                            animatedFaction));
                    //拖动时的动态改变
                    setRefreshHeaderFooterState(ILoadingLayout.REFRESH_COMPLETE_STATE);
                }
            }
        });
        //根据距离调节动画时间
        valueAnimator.setDuration(80);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
    }

    private void setRefreshHeaderFooterState(@ILoadingLayout.PullState int pullState) {
        if (mDirection > 0)
            refreshHeaderLayout.onPullingRefresh(mContainerView.getTop(), finishHeight, pullState);
        else
            refreshFooterLayout.onPullingRefresh(mContainerView.getTop(), finishHeight, pullState);
    }

    /**
     * 刷新监听接口
     * @param listener
     */
    public void setOnRefreshListener(RefreshContainerLayout.OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }

    public interface OnRefreshListener {
        void onPullDownRefresh();

        void onPullUpRefresh();
    }

}
