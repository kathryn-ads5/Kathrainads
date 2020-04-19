package com.kathryn.kathryn.ad_detail.full_screen_image.photoview;

import android.content.Context;
import androidx.core.view.MotionEventCompat;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Found at http://stackoverflow.com/questions/7814017/is-it-possible-to-disable-scrolling-on-a-viewpager.
 * Convenient way to temporarily disable ViewPager navigation while interacting with ImageView.
 * <p>
 * Julia Zudikova
 */

/**
 * Hacky fix for Issue #4 and
 * http://code.google.com/p/android/issues/detail?id=18990
 * <p/>
 * ScaleGestureDetector seems to mess up the touch events, which means that
 * ViewGroups which make use of onInterceptTouchEvent throw a lot of
 * IllegalArgumentException: pointerIndex out of range.
 * <p/>
 * There's not much I can do in my code for now, but we can mask the result by
 * just catching the problem and ignoring it.
 *
 * @author Chris Banes
 */
public class HackyViewPager extends ViewPager {
    private int mode = 0;
    private int mActivePointerId = 0;

    private boolean isLocked;

    public HackyViewPager(Context context) {
        super(context);
        isLocked = false;
    }

    public HackyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        isLocked = false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            if (ev.getAction() == MotionEvent.ACTION_CANCEL) {
                int pointerCount = MotionEventCompat.getPointerCount(ev);
                int index = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                index = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (index > -1 && index < pointerCount) {
                    super.onInterceptTouchEvent(ev);
                } else {
                    return true;
                }
            } else if (ev.getAction() == MotionEventCompat.ACTION_POINTER_DOWN && super.onInterceptTouchEvent(ev)) {
                final int index = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                return false;
            } else if (ev.getAction() == MotionEventCompat.ACTION_POINTER_UP && super.onInterceptTouchEvent(ev)) {
                onSecondaryPointerUp(ev);
                return false;
            } else if (ev.getAction() == MotionEvent.ACTION_DOWN && super.onInterceptTouchEvent(ev)) {
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                return false;
            }
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;

    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = 1;
                break;
            case MotionEvent.ACTION_UP:
                mode = 0;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode -= 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode += 1;
                return false;
            case MotionEvent.ACTION_MOVE:
                if (mode >= 2) {
                    return false;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public void toggleLock() {
        isLocked = !isLocked;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

}
