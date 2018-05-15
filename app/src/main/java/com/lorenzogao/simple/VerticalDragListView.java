package com.lorenzogao.simple;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ListViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

/**
 * 作者：Lorenzo Gao
 * Date: 2018/5/15
 * Time: 9:33
 * 邮箱：2508719070@qq.com
 * Description:
 */

public class VerticalDragListView extends FrameLayout {


    // 可以认为这是系统给我们写好的工具类
    private ViewDragHelper mViewDragHelper;

    private View mDragListView, mMenuView;

    // 后面菜单的高度
    private int mMenuHeight;
    //菜单是否打开
    private boolean mMenuIsOpen = false;


    public VerticalDragListView(@NonNull Context context) {
        this(context, null);
    }

    public VerticalDragListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalDragListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //创建
        mViewDragHelper = ViewDragHelper.create(this, mDragHelperCallback);


    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount != 2) {
            throw new RuntimeException("VerticalDragListView 只能包含两个子布局");
        }
        mMenuView = getChildAt(0);
        mDragListView = getChildAt(1);


    }

    // 拖动子View
    private ViewDragHelper.Callback mDragHelperCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //指定该子View是否可以拖动 就是child
            return mDragListView == child;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            //垂直拖动移动的位置
            //垂直拖动的范围 只能是后面菜单的距离

            if (top < 0) {
                top = 0;
            }
            if (top >= mMenuHeight) {
                top = mMenuHeight;
            }

            return top;

        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //水平拖动移动的位置
            return super.clampViewPositionHorizontal(child, left, dx);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            //手指松开执行这个方法
            if (releasedChild == mDragListView) {
                if (mDragListView.getTop() > mMenuHeight / 2) {
                    //滚动到菜单的高度 （打开）
                    mViewDragHelper.settleCapturedViewAt(0, mMenuHeight);
                    mMenuIsOpen=true;
                } else {
                    //滚动到0 的位置（关闭）
                    mViewDragHelper.settleCapturedViewAt(0, 0);
                    mMenuIsOpen=false;
                }
                invalidate();
            }
        }

    };


    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;

    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mMenuHeight = mMenuView.getMeasuredHeight();
        }
    }

    private float mDownY;

    // Ignoring pointerId=0 because ACTION_DOWN was not received for this pointer before ACTION_MOVE.
    // VDLV.onInterceptTouchEvent().DOWN ->LV.onTouch()->
    // VDLV.onInterceptTouchEvent().MOVE->VDLV.onTouchEvent().MOVE


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //菜单打开要拦截
        if (mMenuIsOpen) {
            return true;
        }
        //向下滑动拦截 不要给ListView做处理
        // 谁拦截谁 父View拦截子View  但是子View可以调用这个方法
        // requestDisallowInterceptTouchEvent 请求父类View 不要拦截 改变的是其实mGroupFlags 的值
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();
                //让DragHelper 拿一个完整的事件
                mViewDragHelper.processTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = ev.getY();
                if ((moveY - mDownY) > 0 && !canChildScrollUp()) {
                    //向下滑动 && 滚动到顶部，拦截不让ListView做处理
                    return true;
                }
                break;

        }
        return super.onInterceptTouchEvent(ev);

    }

    /**
     * @return Whether it is possible for the child view of this layout to
     *         scroll up. Override this if the child view is a custom view.
     *         判断View是否滚动到最顶部  还能不能向上滚动
     */
    public boolean canChildScrollUp() {

        if (mDragListView instanceof ListView) {
            return ListViewCompat.canScrollList((ListView) mDragListView, -1);
        }
        return ViewCompat.canScrollVertically(mDragListView, -1);
    }
}
