#简单的上拉下拉容器
主要使用view的offsetTopAndBottom(int offset)方法

项目地址：<https://github.com/chenzan/MyPullRefresh>

## 效果图：
![](https://github.com/chenzan/MyPullRefresh/raw/master/gif/GIF.gif)

## 常用API
* 设置下拉的最大距离`public void setMaxPullDownHeight(int maxPullDownHeight)`
* 设置上拉的最大距离,默认为footerView的高度`public void setMaxPullUpHeight(int maxPullUpHeight)`

###针对RefreshContainerLayout
* 设置刷新完成状态`public void setRefreshComplete()` 
* 设置刷新状态监听接口`setOnRefreshListener(RefreshContainerLayout.OnRefreshListener listener)`
* 设置了四种状态 `PULLING_STATE, REFRESHING_STATE, REFRESH_COMPLETE_STATE, PULL_RELEASE`

## 类介绍
* ILoadingLayout 头布局与尾布局的接口类
* LoadingLayout 头布局与尾布局的基类
####头布局和尾布局去实现LoadingLayout基类，实现自己的状态改变

## Developed by
* chenzan
