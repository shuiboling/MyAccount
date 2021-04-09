package com.reihiei.firstapp.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.Scroller;

import androidx.annotation.Nullable;

import static androidx.customview.widget.ViewDragHelper.INVALID_POINTER;

public class DragLayout extends LinearLayout {

    private OverScroller scroller;
    private int mScaledTouchSlop;

    private static DragLayout dragLayoutCache = null;
    private int displayWidth;
    private int rightWidth = 0;

    private int mLastMotionX;
    private boolean isScroll = false;
    private int mActivePointerId = -1;
    private int downX = 0;

    public DragLayout(Context context) {
        super(context);
        init();
    }

    public DragLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        setOrientation(HORIZONTAL);

        scroller = new OverScroller(getContext());
        mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        displayWidth = getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        rightWidth = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view.getRight() > displayWidth) {
                Log.d("zyy", "view.getRight():" + view.getRight());
                rightWidth += view.getMeasuredWidth();
            }
        }
    }

    private boolean inChild(int x, int y) {
        if (getChildCount() > 0) {
            final int scrollX = getScrollX();
            final View child = getChildAt(0);
            return !(y < child.getTop()
                    || y >= child.getBottom()
                    || x < child.getLeft() - scrollX
                    || x >= child.getRight() - scrollX);
        }
        return false;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
                MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionX = (int) ev.getX(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);

        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onMotionEvent will be called and we do the actual
         * scrolling there.
         */

        /*
         * Shortcut the most recurring case: the user is in the dragging
         * state and he is moving his finger.  We want to intercept this
         * motion.
         */
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (isScroll)) {
            return true;
        }

        if (super.onInterceptTouchEvent(ev)) {
            return true;
        }

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE: {
                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */

                /*
                 * Locally do absolute value. mLastMotionX is set to the x value
                 * of the down event.
                 */
                final int activePointerId = mActivePointerId;
                if (activePointerId == -1) {
                    // If we don't have a valid id, the touch down wasn't on content.
                    break;
                }

                final int pointerIndex = ev.findPointerIndex(activePointerId);
                if (pointerIndex == -1) {
                    Log.e("TAG", "Invalid pointerId=" + activePointerId
                            + " in onInterceptTouchEvent");
                    break;
                }

                final int x = (int) ev.getX(pointerIndex);
                final int xDiff = (int) Math.abs(x - mLastMotionX);
                if (xDiff > mScaledTouchSlop) { //滑动
                    isScroll = true;
                    mLastMotionX = x;
                    if (getParent() != null) getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                final int x = (int) ev.getX();
                if (!inChild(x, (int) ev.getY())) { //点击不在左边区域，说明右边区域展现
                    isScroll = false;
                    break;
                } else {
                    if (dragLayoutCache != null) {
                        if (dragLayoutCache != this) {
                            dragLayoutCache.smoothClose();

                        }
                        return true;
                    }
                }

                /*
                 * Remember location of down touch.
                 * ACTION_DOWN always refers to pointer index 0.
                 */
                mLastMotionX = x;
                mActivePointerId = ev.getPointerId(0);

                /*
                 * If being flinged and user touches the screen, initiate drag;
                 * otherwise don't.  mScroller.isFinished should be false when
                 * being flinged.
                 */
                isScroll = !scroller.isFinished();
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                /* Release the drag */
                isScroll = false;
                mActivePointerId = -1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int index = ev.getActionIndex();
                mLastMotionX = (int) ev.getX(index);
                mActivePointerId = ev.getPointerId(index);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                mLastMotionX = (int) ev.getX(ev.findPointerIndex(mActivePointerId));
                break;
        }

        /*
         * The only time we want to intercept motion events is if we are in the
         * drag mode.
         */
        return isScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        final int action = ev.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                if (getChildCount() == 0) {
                    return false;
                }
                if ((isScroll = !scroller.isFinished())) {
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }

                /*
                 * If being flinged and user touches, stop the fling. isFinished
                 * will be false if being flinged.
                 */
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }

                // Remember where the motion event started

                downX = mLastMotionX = (int) ev.getX();
                mActivePointerId = ev.getPointerId(0);
                break;
            }
            case MotionEvent.ACTION_MOVE:
                final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
                if (activePointerIndex == -1) {
                    Log.e("TAG", "Invalid pointerId=" + mActivePointerId + " in onTouchEvent");
                    break;
                }

                final int x = (int) ev.getX(activePointerIndex);
                int deltaX = mLastMotionX - x;
                if (!isScroll && Math.abs(deltaX) > mScaledTouchSlop) {
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    isScroll = true;
                    if (deltaX > 0) {
                        deltaX -= mScaledTouchSlop;
                    } else {
                        deltaX += mScaledTouchSlop;
                    }
                }
                if (isScroll) {
                    // Scroll to follow the motion event
                    mLastMotionX = x;

                    if (getScrollX() <= rightWidth && getScrollX() >= 0) {
                        scrollBy(deltaX, 0);
                    }
                    if (getScrollX() < 0) {
                        scrollTo(0, 0);
                    }
                    if (getScrollX() > rightWidth) {
                        scrollTo(rightWidth, 0);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!isScroll) {
                    if (inChild((int) ev.getX(), (int) ev.getY())) {
                        scrollTo(0, 0);
                    } else {
                        return false;
                    }
                }
                if (isScroll) {
//
                    if (getChildCount() > 0) {

                        if (downX < ev.getX()) {
                            Log.d("zyy", "isLeftToRight");

                            if (getScrollX() < 9 * rightWidth / 10) {
                                smoothClose();
                            } else {
                                smoothExpand();
                            }

                        } else {
                            Log.d("zyy", "isRightToLeft");

                            if (getScrollX() < rightWidth / 10) {
                                smoothClose();
                            } else {
                                smoothExpand();
                            }
                        }
                    }

                    mActivePointerId = INVALID_POINTER;
                    isScroll = false;

                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
        }
        return true;
    }

    private ValueAnimator mExpandAnim, mCloseAnim;

    private void cancelAnim() {
        if (mCloseAnim != null && mCloseAnim.isRunning()) {
            mCloseAnim.cancel();
        }
        if (mExpandAnim != null && mExpandAnim.isRunning()) {
            mExpandAnim.cancel();
        }
    }

    /**
     * 平滑关闭
     */
    public void smoothClose() {

        dragLayoutCache = null;
        cancelAnim();
        mCloseAnim = ValueAnimator.ofInt(getScrollX(), 0);
        mCloseAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollTo((Integer) animation.getAnimatedValue(), 0);
            }
        });
        mCloseAnim.setInterpolator(new AccelerateInterpolator());

        mCloseAnim.setDuration(200).start();
    }

    public void smoothExpand() {

        dragLayoutCache = DragLayout.this;
        cancelAnim();
        mExpandAnim = ValueAnimator.ofInt(getScrollX(), rightWidth);
        mExpandAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollTo((Integer) animation.getAnimatedValue(), 0);
            }
        });
        mExpandAnim.setInterpolator(new AccelerateInterpolator());
        mExpandAnim.setDuration(200).start();
    }

    @Override
    protected void onDetachedFromWindow() {

        if (this == dragLayoutCache) {
            dragLayoutCache.smoothClose();
            dragLayoutCache = null;
        }

        super.onDetachedFromWindow();

    }
}
