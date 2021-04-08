package com.reihiei.firstapp.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.reihiei.firstapp.R;

public class PullableLayout extends ViewGroup implements NestedScrollingParent {

    private View mTarget; // the target of the gesture，可滑动目标
    private View mHeader,mFooter;
    private Scroller mLayoutScroller;
    private static final int SCROLL_RATIO = 10;

    // 普通状态
    private static final int NORMAL = 0;
    // 意图刷新
    private static final int TRY_REFRESH = 1;
    // 刷新状态
    private static final int REFRESH = 2;
    // 意图加载
    private static final int TRY_LOAD_MORE = 3;
    // 加载状态
    private static final int LOAD_MORE = 4;
    private int status = NORMAL;

    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private final NestedScrollingChildHelper mNestedScrollingChildHelper;


    public PullableLayout(Context context) {
        super(context);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
    }

    //    从xml中加载view时调用，view都加载完成后会调用onFinishInflate()
    public PullableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mHeader = LayoutInflater.from(context).inflate(R.layout.view_head,null);
        mFooter = LayoutInflater.from(context).inflate(R.layout.view_foot,null);
//---------------------------------
//        addView(mHeader);
//        addView(mFooter);
//---------------------------------

        mLayoutScroller = new Scroller(context);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);

    }

    public PullableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);

    }

    public PullableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);

    }

    private TextView tvHead,tvFoot;
    private RoundView rvHead,rvFoot;

    //    当我们的XML布局被加载完后，就会回调onFinshInfalte这个方法，在这个方法中我们可以初始化控件和数据。
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        tvHead = (TextView) mHeader.findViewById(R.id.tv_head);
        rvHead = (RoundView) mHeader.findViewById(R.id.rv_head);

        tvFoot = (TextView) mFooter.findViewById(R.id.tv_foot);
        rvFoot = (RoundView) mFooter.findViewById(R.id.rv_foot);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT
                ,LayoutParams.MATCH_PARENT);
        mHeader.setLayoutParams(params);
        mFooter.setLayoutParams(params);

        addView(mHeader);
        addView(mFooter);
    }

    private void ensureTarget(){
        for(int i = 0;i<getChildCount();i++){
            View child = getChildAt(i);
            if(child != mHeader && child != mFooter){
                mTarget = child;
                break;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mTarget == null) {
            ensureTarget();
        }
    if (mTarget == null) {
            return;
        }

        for(int i = 0; i<getChildCount();i++){
            View child = getChildAt(i);
            child.measure(MeasureSpec.makeMeasureSpec(
                    getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }

        // 置位
        for (int i = 0; i < getChildCount(); i++){
            View child = getChildAt(i);
            if (child == mHeader) { // 头视图隐藏在顶端
                child.layout(0, 0 - child.getMeasuredHeight(), child.getMeasuredWidth(), 0);
            } else if (child == mFooter) { // 尾视图隐藏在layout所有内容视图之后
                child.layout(0, mTarget.getMeasuredHeight(), child.getMeasuredWidth(), mTarget.getMeasuredHeight() + child.getMeasuredHeight());
            } else {
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            }
        }
    }

    //  是否接收嵌套滚动
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes) {
//        纵向返回true
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {

    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
//        Log.d("zyyu","nested stop");
        pullFinish();
        mNestedScrollingParentHelper.onStopNestedScroll(target);

    }

    // 父view是否先消耗滚动参数
    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        Log.d("ttt","onNestedPreScroll dy:"+dy);
        Log.d("ttt","onNestedPreScroll getScrollY():"+getScrollY());
//dy<0-》下拉
        //dy>0 -》上拉

        //如果是上滑且顶部控件未完全隐藏，则消耗掉dy，即consumed[1]=dy;
        //以mHead的bottom=0时为原点，只要mHead露头了，scrollY就是负值
        boolean hiddenTop = dy > 0 && (getScrollY() < 0 || status == REFRESH);//下拉过程中的上拉
        //-1检查view向上=手指向下滑动，1向下
//        如果是下滑且内部View已经无法继续下拉，则消耗掉dy，即consumed[1]=dy，
        boolean showTop = dy < 0 && !mTarget.canScrollVertically(-1);

        boolean hiddenBottom = dy < 0 && (getScrollY() > 0|| status == LOAD_MORE);
        boolean showBottom = dy > 0 && !mTarget.canScrollVertically(1);
//
//        消耗掉的意思，就是自己去执行scrollBy
        if(hiddenTop || showTop || hiddenBottom || showBottom){
//            Log.d("zyyd","onNestedPreScroll consumed");
            if(showBottom){
                pullUp(dy);
            }
            if(hiddenTop){
                if(Math.abs(dy) > Math.abs(getScrollY())) {
                    dy = Math.abs(getScrollY());
                }
                pullUp(dy);
            }

            if (showTop){
                pullDown(dy);
            }
            if(hiddenBottom){
                if(Math.abs(dy) > Math.abs(getScrollY())) {
                    dy = -Math.abs(getScrollY());
                }
                pullDown(dy);
            }

            consumed[1] = dy;
        }


    }

    //子View主动将消费的距离与未消费的距离通知父View
    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        if(dyUnconsumed > 0){
            pullUp(dyUnconsumed);
        }else if(dyUnconsumed < 0){
            pullDown(dyUnconsumed);
        }
    }

    //    是否消耗fling事件
    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);

    }

    //    处理惯性事件
    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

//    //    限制滑动范围
//    @Override
//    public void scrollTo(int x, int y) {
//
//        if(y>0){
//            y=0;
//        }
//
//        int dy = y-getScrollY();
//        //down
//        if(dy <= 0){
//            if (Math.abs(y) >= mHeader.getHeight()){
//                y = -mHeader.getHeight();
//            }
//        }else {
//
//        }
//
//        super.scrollTo(x,y);
//
//    }

    private int mLastMoveY;
    private int effectiveScrollY = 150;

    //    important！
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mLayoutScroller.computeScrollOffset()) {
            scrollTo(0, mLayoutScroller.getCurrY());
        }
        postInvalidate();
    }

    //    important 优化AbsListView滑到顶（底）部后不能直接继续下（上）拉操作
//    原因是AbsListView开始处理滑动事件时，会禁止父view拦截视图
//// Time to start stealing events! Once we've stolen them, don't let anyone
//// steal from us
//   final ViewParent parent = getParent();
//            if (parent != null) {
//        parent.requestDisallowInterceptTouchEvent(true);
//    }
//    如下是Swiperefreshlayout中的解决办法
    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        if ((Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean intercept = false;

        //当前位置
        int y = (int) ev.getY();

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastMoveY = y;
                intercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.d("zyyd"," intercept move");
                if(status == REFRESH || status == LOAD_MORE){
                    //如果不用NestedScrollingParent则不能直接拦截，因为list滚到头后应该滚list本身，所以应该是父view滚一段距离把head隐藏后交给list滚

                    return false;
                }
                //下拉
                if(y > mLastMoveY){
//                    Log.d("zyyd"," intercept move down");

                    if(mTarget instanceof AbsListView){ //ListView、GridView
                        intercept = true;
                        AbsListView adapterChild = (AbsListView) mTarget;
                        //getFirstVisiblePosition():当前显示的第一个item的position
                        //getTop():该item的Top位置
                        if(adapterChild.getFirstVisiblePosition() != 0
                                ||adapterChild.getChildAt(0).getTop() != 0){
                            intercept = false;
                        }
                    }else if(mTarget instanceof RecyclerView){
//                       intercept = false;
//                       RecyclerView recyclerView = (RecyclerView)mTarget;
//
//                       //offset是当前屏幕划过的距离
//                       if(recyclerView.computeVerticalScrollOffset()<= 0){
//                           intercept = true;
//                        }
//                        intercept = !mTarget.canScrollVertically(-1);
                        return false;
                    }
                }else if(y < mLastMoveY){  //上拉
//                    Log.d("zyyd"," intercept move up");

                    if(mTarget instanceof AbsListView){
                        intercept = true;
                        AbsListView adapterChild = (AbsListView) mTarget;

                        if(adapterChild.getLastVisiblePosition() != adapterChild.getCount() -1
                                ||adapterChild.getChildAt(adapterChild.getChildCount() - 1).getBottom() != getMeasuredHeight()){
                            intercept = false;
                        }
                    }else if(mTarget instanceof RecyclerView) {
//                        intercept = false;
//                        RecyclerView recyclerView = (RecyclerView) mTarget;
//                        //extent是当前屏幕显示的距离；range是整个view可滑动的高度
//                        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()
//                                >= recyclerView.computeVerticalScrollRange()){
//                            intercept = true;
//                        }
                        //false表示已滚动底部，拦截
//                        intercept = !mTarget.canScrollVertically(1);
                        return false;
                    }
                }
//                Log.d("zyyd"," intercept up"+intercept);
                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
                break;
        }

        mLastMoveY = y;

        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //当前位置
        int y = (int) event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //保存按下的位置
                mLastMoveY = y;
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.d("zyyd","touch move");
                //获取滑动距离
                int dy = mLastMoveY - y;

                //下拉
                if (dy < 0){
//                    Log.d("zyyd","touch move dy<0");

                    //1.上拉一段距离后，此时dy<0，然后向下滚动还原getScrollY()>0，此时有可能也超过有效距离，因此需要处理
                    // 2.|| 滑动不超过头部1/2
                    pullDown(dy);
//                    if(getScrollY()>0||Math.abs(getScrollY()) <= mHeader.getMeasuredHeight()/2){
                    //滚动
//                        scrollBy(0, dy);

//                        if(status != LOAD_MORE && status != TRY_LOAD_MORE) {
//                            //滑动超过有效距离
//                            if (Math.abs(getScrollY()) >= effectiveScrollY) {
//                                tvHead.setText("松开刷新");
//                                status = REFRESH;
//                            } else {
//                                status = TRY_REFRESH;
//                            }
//                        } else {
//                            status = LOAD_MORE;
//                        }
//                    }

                } else if(dy > 0){  //上拉
//                    Log.d("zyyd","touch move dy>0");

                    pullUp(dy);

                }
//                Log.d("zyyd","touch status:"+status);

                break;
            case MotionEvent.ACTION_UP:
//                Log.d("zyyu","touch up status:"+status);
                pullFinish();
                break;
        }

        mLastMoveY = y;
//        postInvalidate();

        return true;

    }

    private int REFRESH_EFFECTIVE = 300;
    private int LOAD_EFFECTIVE = 300;

    public void pullDown(int dy){
        Log.d("ttt","pullDown getScrollY:"+getScrollY());

        if(getScrollY() >= 0){
            scrollBy(0,dy);
            if(getScrollY() == 0){
                updateStatus(NORMAL);
            }else if(Math.abs(getScrollY()) < LOAD_EFFECTIVE){
                updateStatus(TRY_LOAD_MORE);
            }else {
                updateStatus(LOAD_MORE);
            }
        } else if(Math.abs(getScrollY()) < REFRESH_EFFECTIVE){
            scrollBy(0,dy/SCROLL_RATIO);
            updateStatus(TRY_REFRESH);
        }else {
            scrollBy(0,dy/(SCROLL_RATIO*2));
            updateStatus(REFRESH);
        }
    }

    public void pullUp(int dy){
//        Log.d("zyyd","pullUp getScrollY:"+getScrollY());
        if(getScrollY() <= 0){
            scrollBy(0,dy);
            Log.d("zzz",dy+"q");
            if(getScrollY() == 0){
                updateStatus(NORMAL);
            }else if(Math.abs(getScrollY()) < REFRESH_EFFECTIVE){
                updateStatus(TRY_REFRESH);
            }else {
                updateStatus(REFRESH);
            }
        } else if(Math.abs(getScrollY()) <= LOAD_EFFECTIVE){
            scrollBy(0,dy/SCROLL_RATIO);
            Log.d("zzz",dy/SCROLL_RATIO+"a");

            updateStatus(TRY_LOAD_MORE);
        }else {
            scrollBy(0,dy/(SCROLL_RATIO*2));
            Log.d("zzz",dy/(SCROLL_RATIO*10)+"z");

            updateStatus(LOAD_MORE);
        }
    }

    public void pullFinish(){
        //滑动超过有效距离
        if(status == REFRESH){
            //滑动到可以显示头部的位置
            //getScroll,当前点为0，getScroll是以当前点为原点滑动的距离，下正，上负
            mLayoutScroller.startScroll(0, getScrollY(), 0, -(getScrollY() + effectiveScrollY));
            tvHead.setVisibility(GONE);
            rvHead.setVisibility(VISIBLE);

            if(onRefreshListener != null) {
                onRefreshListener.onRefresh();
            }
        } else if( status == LOAD_MORE){
            //
            mLayoutScroller.startScroll(0,getScrollY(),0,-(getScrollY()-effectiveScrollY));
            tvFoot.setVisibility(GONE);
            rvFoot.setVisibility(VISIBLE);

            if(onRefreshListener != null) {
                onRefreshListener.onLoad();
            }
        }else{
            //滑动距离过短，恢复
            mLayoutScroller.startScroll(0, getScrollY(), 0, -getScrollY());
        }
    }

    public void updateStatus(int status){
        switch (status){
            case NORMAL:
                break;
            case REFRESH:
                tvHead.setVisibility(VISIBLE);
                tvHead.setText("松开刷新");
                rvHead.setVisibility(GONE);
                break;
            case TRY_REFRESH:
                tvHead.setVisibility(VISIBLE);
                tvHead.setText("下拉刷新");
                rvHead.setVisibility(GONE);
                break;
            case LOAD_MORE:
                tvFoot.setVisibility(VISIBLE);
                rvFoot.setVisibility(GONE);
                tvFoot.setText("松开加载更多");
                break;
            case TRY_LOAD_MORE:
                tvFoot.setVisibility(VISIBLE);
                rvFoot.setVisibility(GONE);
                tvFoot.setText("上拉加载更多");
                break;
        }
        this.status = status;
    }

    public void refreshDone(){
        mLayoutScroller.startScroll(0, getScrollY(), 0, -getScrollY());
        rvHead.setVisibility(View.GONE);
        tvHead.setText("下拉刷新");
        tvHead.setVisibility(View.VISIBLE);
        status = NORMAL;

    }

    public void loadDone(){
        mLayoutScroller.startScroll(0, getScrollY(), 0, -getScrollY());
        rvFoot.setVisibility(View.GONE);
        tvFoot.setText("上拉加载");
        tvFoot.setVisibility(View.VISIBLE);
        status = NORMAL;

    }

    private onRefreshListener onRefreshListener;

    public void setRefreshListener(PullableLayout.onRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public interface onRefreshListener{
        void onRefresh();
        void onLoad();

    }

}
