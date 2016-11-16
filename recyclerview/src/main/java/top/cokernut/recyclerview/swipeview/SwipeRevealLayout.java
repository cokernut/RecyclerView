package top.cokernut.recyclerview.swipeview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import top.cokernut.recyclerview.R;


@SuppressLint("RtlHardcoded")
public class SwipeRevealLayout extends ViewGroup {
    // 用于ViewBindHelper的状态
    protected static final int STATE_CLOSE     = 0;
    protected static final int STATE_CLOSING   = 1;
    protected static final int STATE_OPEN      = 2;
    protected static final int STATE_OPENING   = 3;
    protected static final int STATE_DRAGGING  = 4;

    private static final int DEFAULT_MIN_FLING_VELOCITY = 300; // 每秒速度

    public static final int DRAG_EDGE_LEFT =   0x1;
    public static final int DRAG_EDGE_RIGHT =  0x1 << 1;
    public static final int DRAG_EDGE_TOP =    0x1 << 2;
    public static final int DRAG_EDGE_BOTTOM = 0x1 << 3;

    /**
     * secondary view在main view下面.
     */
    public static final int MODE_NORMAL = 0;

    /**
     * secondary view 在 main view边缘.
     */
    public static final int MODE_SAME_LEVEL = 1;

    /**
     * 主View
     */
    private View mMainView;

    /**
     * 辅助View
     */
    private View mSecondaryView;

    /**
     * 在main view关闭时候的矩形位置.
     */
    private Rect mRectMainClose = new Rect();

    /**
     * 在main view打开时候的矩形位置.
     */
    private Rect mRectMainOpen  = new Rect();

    /**
     * 在secondary view关闭时候的矩形位置.
     */
    private Rect mRectSecClose  = new Rect();

    /**
     * 在secondary view打开时候的矩形位置.
     */
    private Rect mRectSecOpen   = new Rect();

    private boolean mIsOpenBeforeInit = false;
    private volatile boolean mAborted = false;
    private volatile boolean mIsScrolling = false;
    private volatile boolean mLockDrag = false;

    private int mMinFlingVelocity = DEFAULT_MIN_FLING_VELOCITY;
    private int mState = STATE_CLOSE;
    private int mMode = MODE_NORMAL;

    private int mLastMainLeft = 0;
    private int mLastMainTop  = 0;

    private int mDragEdge = DRAG_EDGE_LEFT;

    private ViewDragHelper mDragHelper;
    private GestureDetectorCompat mGestureDetector;

    private DragStateChangeListener mDragStateChangeListener; // 仅用于 ViewBindHelper
    private SwipeListener mSwipeListener;

    interface DragStateChangeListener {
        void onDragStateChanged(int state);
    }

    /**
     * swipe layout事件的监听器.
     */
    public interface SwipeListener {
        /**
         * main view关闭时触发.
         */
        void onClosed(SwipeRevealLayout view);

        /**
         * main view打开时触发.
         */
        void onOpened(SwipeRevealLayout view);

        /**
         * main view 位置变化时触发.
         * @param slideOffset  main view 的偏移范围, 值为 0-1
         */
        void onSlide(SwipeRevealLayout view, float slideOffset);
    }

    /**
     * SwipeListener基础实现，你可以继承SimpleSwipeListener实现重写方法
     */
    public static class SimpleSwipeListener implements SwipeListener {
        @Override
        public void onClosed(SwipeRevealLayout view) {}

        @Override
        public void onOpened(SwipeRevealLayout view) {}

        @Override
        public void onSlide(SwipeRevealLayout view, float slideOffset) {}
    }

    public SwipeRevealLayout(Context context) {
        super(context);
        init(context, null);
    }

    public SwipeRevealLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SwipeRevealLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        mDragHelper.processTouchEvent(ev);
        mGestureDetector.onTouchEvent(ev);

        boolean settling = mDragHelper.getViewDragState() == ViewDragHelper.STATE_SETTLING;
        boolean idleAfterScrolled = mDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE
                && mIsScrolling;

        return settling || idleAfterScrolled;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // 取得View
        if (getChildCount() >= 2) {
            mSecondaryView = getChildAt(0);
            mMainView = getChildAt(1);
        }
        else if (getChildCount() == 1) {
            mMainView = getChildAt(0);
        }
    }

    /**
     * 确定位置
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAborted = false;

        for (int index = 0; index < getChildCount(); index++) {
            final View child = getChildAt(index);

            int left, right, top, bottom;
            left = right = top = bottom = 0;

            final int minLeft = getPaddingLeft();
            final int maxRight = Math.max(r - getPaddingRight() - l, 0);
            final int minTop = getPaddingTop();
            final int maxBottom = Math.max(b - getPaddingBottom() - t, 0);

            int measuredChildHeight = child.getMeasuredHeight();
            int measuredChildWidth = child.getMeasuredWidth();

            // 需要考虑孩子是否是match_parent的大小
            final LayoutParams childParams = child.getLayoutParams();
            boolean matchParentHeight = false;
            boolean matchParentWidth = false;

            if (childParams != null) {
                matchParentHeight = (childParams.height == LayoutParams.MATCH_PARENT) ||
                        (childParams.height == LayoutParams.FILL_PARENT);
                matchParentWidth = (childParams.width == LayoutParams.MATCH_PARENT) ||
                        (childParams.width == LayoutParams.FILL_PARENT);
            }

            if (matchParentHeight) {
                measuredChildHeight = maxBottom - minTop;
                childParams.height = measuredChildHeight;
            }

            if (matchParentWidth) {
                measuredChildWidth = maxRight - minLeft;
                childParams.width = measuredChildWidth;
            }

            switch (mDragEdge) {
                case DRAG_EDGE_RIGHT:
                    left    = Math.max(r - measuredChildWidth - getPaddingRight() - l, minLeft);
                    top     = Math.min(getPaddingTop(), maxBottom);
                    right   = Math.max(r - getPaddingRight() - l, minLeft);
                    bottom  = Math.min(measuredChildHeight + getPaddingTop(), maxBottom);
                    break;

                case DRAG_EDGE_LEFT:
                    left    = Math.min(getPaddingLeft(), maxRight);
                    top     = Math.min(getPaddingTop(), maxBottom);
                    right   = Math.min(measuredChildWidth + getPaddingLeft(), maxRight);
                    bottom  = Math.min(measuredChildHeight + getPaddingTop(), maxBottom);
                    break;

                case DRAG_EDGE_TOP:
                    left    = Math.min(getPaddingLeft(), maxRight);
                    top     = Math.min(getPaddingTop(), maxBottom);
                    right   = Math.min(measuredChildWidth + getPaddingLeft(), maxRight);
                    bottom  = Math.min(measuredChildHeight + getPaddingTop(), maxBottom);
                    break;

                case DRAG_EDGE_BOTTOM:
                    left    = Math.min(getPaddingLeft(), maxRight);
                    top     = Math.max(b - measuredChildHeight - getPaddingBottom() - t, minTop);
                    right   = Math.min(measuredChildWidth + getPaddingLeft(), maxRight);
                    bottom  = Math.max(b - getPaddingBottom() - t, minTop);
                    break;
            }

            child.layout(left, top, right, bottom);
        }

        // 考虑是SAME_LEVEL模式时
        if (mMode == MODE_SAME_LEVEL) {
            switch (mDragEdge) {
                case DRAG_EDGE_LEFT:
                    mSecondaryView.offsetLeftAndRight(-mSecondaryView.getWidth());
                    break;

                case DRAG_EDGE_RIGHT:
                    mSecondaryView.offsetLeftAndRight(mSecondaryView.getWidth());
                    break;

                case DRAG_EDGE_TOP:
                    mSecondaryView.offsetTopAndBottom(-mSecondaryView.getHeight());
                    break;

                case DRAG_EDGE_BOTTOM:
                    mSecondaryView.offsetTopAndBottom(mSecondaryView.getHeight());
            }
        }

        initRects();

        if (mIsOpenBeforeInit) {
            open(false);
        } else {
            close(false);
        }

        mLastMainLeft = mMainView.getLeft();
        mLastMainTop = mMainView.getTop();
    }

    /**
     * 测量大小
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() < 2) {
            throw new RuntimeException("Layout must have two children");
        }

        final LayoutParams params = getLayoutParams();

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        final int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

        int desiredWidth = 0;
        int desiredHeight = 0;

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            final LayoutParams childParams = child.getLayoutParams();

            if (childParams != null) {
                if (childParams.height == LayoutParams.MATCH_PARENT) {
                    child.setMinimumHeight(measuredHeight);
                }

                if (childParams.width == LayoutParams.MATCH_PARENT) {
                    child.setMinimumWidth(measuredWidth);
                }
            }

            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            desiredWidth = Math.max(child.getMeasuredWidth(), desiredWidth);
            desiredHeight = Math.max(child.getMeasuredHeight(), desiredHeight);
        }

        //  padding
        desiredWidth += getPaddingLeft() + getPaddingRight();
        desiredHeight += getPaddingTop() + getPaddingBottom();

        // 调整所需的宽度
        if (widthMode == MeasureSpec.EXACTLY) {
            desiredWidth = measuredWidth;
        } else {
            if (params.width == LayoutParams.MATCH_PARENT) {
                desiredWidth = measuredWidth;
            }

            if (widthMode == MeasureSpec.AT_MOST) {
                desiredWidth = (desiredWidth > measuredWidth)? measuredWidth : desiredWidth;
            }
        }

        // 调整所需的高度
        if (heightMode == MeasureSpec.EXACTLY) {
            desiredHeight = measuredHeight;
        } else {
            if (params.height == LayoutParams.MATCH_PARENT) {
                desiredHeight = measuredHeight;
            }

            if (heightMode == MeasureSpec.AT_MOST) {
                desiredHeight = (desiredHeight > measuredHeight)? measuredHeight : desiredHeight;
            }
        }

        setMeasuredDimension(desiredWidth, desiredHeight);
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * 打开 secondary view
     * @param animation 要开启动画是设置为true，否则为false.
     */
    public void open(boolean animation) {
        mIsOpenBeforeInit = true;
        mAborted = false;

        if (animation) {
            mState = STATE_OPENING;
            mDragHelper.smoothSlideViewTo(mMainView, mRectMainOpen.left, mRectMainOpen.top);

            if (mDragStateChangeListener != null) {
                mDragStateChangeListener.onDragStateChanged(mState);
            }
        } else {
            mState = STATE_OPEN;
            mDragHelper.abort();

            mMainView.layout(
                    mRectMainOpen.left,
                    mRectMainOpen.top,
                    mRectMainOpen.right,
                    mRectMainOpen.bottom
            );

            mSecondaryView.layout(
                    mRectSecOpen.left,
                    mRectSecOpen.top,
                    mRectSecOpen.right,
                    mRectSecOpen.bottom
            );
        }

        ViewCompat.postInvalidateOnAnimation(SwipeRevealLayout.this);
    }

    /**
     *  打开 secondary view
     * @param animation 要开启动画是设置为true，否则为false.
     */
    public void close(boolean animation) {
        mIsOpenBeforeInit = false;
        mAborted = false;

        if (animation) {
            mState = STATE_CLOSING;
            mDragHelper.smoothSlideViewTo(mMainView, mRectMainClose.left, mRectMainClose.top);

            if (mDragStateChangeListener != null) {
                mDragStateChangeListener.onDragStateChanged(mState);
            }

        } else {
            mState = STATE_CLOSE;
            mDragHelper.abort();

            mMainView.layout(
                    mRectMainClose.left,
                    mRectMainClose.top,
                    mRectMainClose.right,
                    mRectMainClose.bottom
            );

            mSecondaryView.layout(
                    mRectSecClose.left,
                    mRectSecClose.top,
                    mRectSecClose.right,
                    mRectSecClose.bottom
            );
        }

        ViewCompat.postInvalidateOnAnimation(SwipeRevealLayout.this);
    }

    /**
     * 设置最低的布局打开/关闭速度。
     * @param velocity dp每秒
     */
    public void setMinFlingVelocity(int velocity) {
        mMinFlingVelocity = velocity;
    }

    /**
     * 得到最低的布局打开/关闭速度。
     * @return dp每秒
     */
    public int getMinFlingVelocity() {
        return mMinFlingVelocity;
    }

    /**
     * 设置边缘布局的方向。
     * @param dragEdge 可以是其中一个：{@link #DRAG_EDGE_LEFT},{@link #DRAG_EDGE_TOP},
     * {@link #DRAG_EDGE_RIGHT},{@link #DRAG_EDGE_BOTTOM}
     */
    public void setDragEdge(int dragEdge) {
        mDragEdge = dragEdge;
    }

    /**
     * 取得边缘布局的方向。
     * @return 可以是其中一个：{@link #DRAG_EDGE_LEFT},{@link #DRAG_EDGE_TOP},
     * {@link #DRAG_EDGE_RIGHT},{@link #DRAG_EDGE_BOTTOM}
     */
    public int getDragEdge() {
        return mDragEdge;
    }

    public void setSwipeListener(SwipeListener listener) {
        mSwipeListener = listener;
    }

    /**
     * @param lock 设置为true, 则用户不能 拖动/滑动 布局.
     */
    public void setLockDrag(boolean lock) {
        mLockDrag = lock;
    }

    /**
     * @return 如果 拖动/滑动 布局被锁定则返回true
     */
    public boolean isDragLocked() {
        return mLockDrag;
    }

    /**
     * @return 布局被打开返回true 否则返回 false
     */
    public boolean isOpened() {
        return (mState == STATE_OPEN);
    }

    /**
     * @return 布局被全部关闭返回true 否则返回false
     */
    public boolean isClosed() {
        return (mState == STATE_CLOSE);
    }

    /** 仅用于 ViewBinderHelper*/
    void setDragStateChangeListener(DragStateChangeListener listener) {
        mDragStateChangeListener = listener;
    }

    /** 终止当前活动. 仅用于 ViewBinderHelper {@link ViewBinderHelper} */
    protected void abort() {
        mAborted = true;
        mDragHelper.abort();
    }


    private int getMainOpenLeft() {
        switch (mDragEdge) {
            case DRAG_EDGE_LEFT:
                return mRectMainClose.left + mSecondaryView.getWidth();

            case DRAG_EDGE_RIGHT:
                return mRectMainClose.left - mSecondaryView.getWidth();

            case DRAG_EDGE_TOP:
                return mRectMainClose.left;

            case DRAG_EDGE_BOTTOM:
                return mRectMainClose.left;

            default:
                return 0;
        }
    }

    private int getMainOpenTop() {
        switch (mDragEdge) {
            case DRAG_EDGE_LEFT:
                return mRectMainClose.top;

            case DRAG_EDGE_RIGHT:
                return mRectMainClose.top;

            case DRAG_EDGE_TOP:
                return mRectMainClose.top + mSecondaryView.getHeight();

            case DRAG_EDGE_BOTTOM:
                return mRectMainClose.top - mSecondaryView.getHeight();

            default:
                return 0;
        }
    }

    private int getSecOpenLeft() {
        if (mMode == MODE_NORMAL || mDragEdge == DRAG_EDGE_BOTTOM || mDragEdge == DRAG_EDGE_TOP) {
            return mRectSecClose.left;
        }

        if (mDragEdge == DRAG_EDGE_LEFT) {
            return mRectSecClose.left + mSecondaryView.getWidth();
        } else {
            return mRectSecClose.left - mSecondaryView.getWidth();
        }
    }

    private int getSecOpenTop() {
        if (mMode == MODE_NORMAL || mDragEdge == DRAG_EDGE_LEFT || mDragEdge == DRAG_EDGE_RIGHT) {
            return mRectSecClose.top;
        }

        if (mDragEdge == DRAG_EDGE_TOP) {
            return mRectSecClose.top + mSecondaryView.getHeight();
        } else {
            return mRectSecClose.top - mSecondaryView.getHeight();
        }
    }

    private void initRects() {
        // 设置关闭 Main View 的位置
        mRectMainClose.set(
                mMainView.getLeft(),
                mMainView.getTop(),
                mMainView.getRight(),
                mMainView.getBottom()
        );

        // 设置关闭 secondary view 的位置
        mRectSecClose.set(
                mSecondaryView.getLeft(),
                mSecondaryView.getTop(),
                mSecondaryView.getRight(),
                mSecondaryView.getBottom()
        );

        // 设置打开 Main View 的位置
        mRectMainOpen.set(
                getMainOpenLeft(),
                getMainOpenTop(),
                getMainOpenLeft() + mMainView.getWidth(),
                getMainOpenTop() + mMainView.getHeight()
        );

        // 设置打开 secondary view 的位置
        mRectSecOpen.set(
                getSecOpenLeft(),
                getSecOpenTop(),
                getSecOpenLeft() + mSecondaryView.getWidth(),
                getSecOpenTop() + mSecondaryView.getHeight()
        );
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null && context != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.SwipeRevealLayout,
                    0, 0
            );

            mDragEdge = a.getInteger(R.styleable.SwipeRevealLayout_dragEdge, DRAG_EDGE_LEFT);
            mMinFlingVelocity = a.getInteger(R.styleable.SwipeRevealLayout_flingVelocity, DEFAULT_MIN_FLING_VELOCITY);
            mMode = a.getInteger(R.styleable.SwipeRevealLayout_mode, MODE_NORMAL);
        }

        mDragHelper = ViewDragHelper.create(this, 1.0f, mDragHelperCallback);
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_ALL);

        mGestureDetector = new GestureDetectorCompat(context, mGestureListener);
    }

    private final GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            mIsScrolling = false;
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mIsScrolling = true;
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mIsScrolling = true;

            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }

            return false;
        }
    };

    private int getHalfwayPivotHorizontal() {
        if (mDragEdge == DRAG_EDGE_LEFT) {
            return mRectMainClose.left + mSecondaryView.getWidth() / 2;
        } else {
            return mRectMainClose.right - mSecondaryView.getWidth() / 2;
        }
    }

    private int getHalfwayPivotVertical() {
        if (mDragEdge == DRAG_EDGE_TOP) {
            return mRectMainClose.top + mSecondaryView.getHeight() / 2;
        } else {
            return mRectMainClose.bottom - mSecondaryView.getHeight() / 2;
        }
    }

    private final ViewDragHelper.Callback mDragHelperCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            mAborted = false;

            if (mLockDrag)
                return false;

            mDragHelper.captureChildView(mMainView, pointerId);
            return false;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            switch (mDragEdge) {
                case DRAG_EDGE_TOP:
                    return Math.max(
                            Math.min(top, mRectMainClose.top + mSecondaryView.getHeight()),
                            mRectMainClose.top
                    );

                case DRAG_EDGE_BOTTOM:
                    return Math.max(
                            Math.min(top, mRectMainClose.top),
                            mRectMainClose.top - mSecondaryView.getHeight()
                    );

                default:
                    return child.getTop();
            }
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            switch (mDragEdge) {
                case DRAG_EDGE_RIGHT:
                    return Math.max(
                            Math.min(left, mRectMainClose.left),
                            mRectMainClose.left - mSecondaryView.getWidth()
                    );

                case DRAG_EDGE_LEFT:
                    return Math.max(
                            Math.min(left, mRectMainClose.left + mSecondaryView.getWidth()),
                            mRectMainClose.left
                    );

                default:
                    return child.getLeft();
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final boolean velRightExceeded =  pxToDp((int) xvel) >= mMinFlingVelocity;
            final boolean velLeftExceeded =   pxToDp((int) xvel) <= -mMinFlingVelocity;
            final boolean velUpExceeded =     pxToDp((int) yvel) <= -mMinFlingVelocity;
            final boolean velDownExceeded =   pxToDp((int) yvel) >= mMinFlingVelocity;

            final int pivotHorizontal = getHalfwayPivotHorizontal();
            final int pivotVertical = getHalfwayPivotVertical();

            switch (mDragEdge) {
                case DRAG_EDGE_RIGHT:
                    if (velRightExceeded) {
                        close(true);
                    } else if (velLeftExceeded) {
                        open(true);
                    } else {
                        if (mMainView.getRight() < pivotHorizontal) {
                            open(true);
                        } else {
                            close(true);
                        }
                    }
                    break;

                case DRAG_EDGE_LEFT:
                    if (velRightExceeded) {
                        open(true);
                    } else if (velLeftExceeded) {
                        close(true);
                    } else {
                        if (mMainView.getLeft() < pivotHorizontal) {
                            close(true);
                        } else {
                            open(true);
                        }
                    }
                    break;

                case DRAG_EDGE_TOP:
                    if (velUpExceeded) {
                        close(true);
                    } else if (velDownExceeded) {
                        open(true);
                    } else {
                        if (mMainView.getTop() < pivotVertical) {
                            close(true);
                        } else {
                            open(true);
                        }
                    }
                    break;

                case DRAG_EDGE_BOTTOM:
                    if (velUpExceeded) {
                        open(true);
                    } else if (velDownExceeded) {
                        close(true);
                    } else {
                        if (mMainView.getBottom() < pivotVertical) {
                            open(true);
                        } else {
                            close(true);
                        }
                    }
                    break;
            }
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            super.onEdgeDragStarted(edgeFlags, pointerId);

            if (mLockDrag) {
                return;
            }

            boolean edgeStartLeft = (mDragEdge == DRAG_EDGE_RIGHT)
                    && edgeFlags == ViewDragHelper.EDGE_LEFT;

            boolean edgeStartRight = (mDragEdge == DRAG_EDGE_LEFT)
                    && edgeFlags == ViewDragHelper.EDGE_RIGHT;

            boolean edgeStartTop = (mDragEdge == DRAG_EDGE_BOTTOM)
                    && edgeFlags == ViewDragHelper.EDGE_TOP;

            boolean edgeStartBottom = (mDragEdge == DRAG_EDGE_TOP)
                    && edgeFlags == ViewDragHelper.EDGE_BOTTOM;

            if (edgeStartLeft || edgeStartRight || edgeStartTop || edgeStartBottom) {
                mDragHelper.captureChildView(mMainView, pointerId);
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (mMode == MODE_SAME_LEVEL) {
                if (mDragEdge == DRAG_EDGE_LEFT || mDragEdge == DRAG_EDGE_RIGHT) {
                    mSecondaryView.offsetLeftAndRight(dx);
                } else {
                    mSecondaryView.offsetTopAndBottom(dy);
                }
            }

            boolean isMoved = (mMainView.getLeft() != mLastMainLeft) || (mMainView.getTop() != mLastMainTop);
            if (mSwipeListener != null && isMoved) {
                if (mMainView.getLeft() == mRectMainClose.left && mMainView.getTop() == mRectMainClose.top) {
                    mSwipeListener.onClosed(SwipeRevealLayout.this);
                }
                else if (mMainView.getLeft() == mRectMainOpen.left && mMainView.getTop() == mRectMainOpen.top) {
                    mSwipeListener.onOpened(SwipeRevealLayout.this);
                }
                else {
                    mSwipeListener.onSlide(SwipeRevealLayout.this, getSlideOffset());
                }
            }

            mLastMainLeft = mMainView.getLeft();
            mLastMainTop = mMainView.getTop();
            ViewCompat.postInvalidateOnAnimation(SwipeRevealLayout.this);
        }

        private float getSlideOffset() {
            switch (mDragEdge) {
                case DRAG_EDGE_LEFT:
                    return (float) (mMainView.getLeft() - mRectMainClose.left) / mSecondaryView.getWidth();

                case DRAG_EDGE_RIGHT:
                    return (float) (mRectMainClose.left - mMainView.getLeft()) / mSecondaryView.getWidth();

                case DRAG_EDGE_TOP:
                    return (float) (mMainView.getTop() - mRectMainClose.top) / mSecondaryView.getHeight();

                case DRAG_EDGE_BOTTOM:
                    return (float) (mRectMainClose.top - mMainView.getTop()) / mSecondaryView.getHeight();

                default:
                    return 0;
            }
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            final int prevState = mState;

            switch (state) {
                case ViewDragHelper.STATE_DRAGGING:
                    mState = STATE_DRAGGING;
                    break;

                case ViewDragHelper.STATE_IDLE:

                    // 在边缘向左或者向右拖动
                    if (mDragEdge == DRAG_EDGE_LEFT || mDragEdge == DRAG_EDGE_RIGHT) {
                        if (mMainView.getLeft() == mRectMainClose.left) {
                            mState = STATE_CLOSE;
                        } else {
                            mState = STATE_OPEN;
                        }
                    }

                    // 在边缘向上或者向下拖动
                    else {
                        if (mMainView.getTop() == mRectMainClose.top) {
                            mState = STATE_CLOSE;
                        } else {
                            mState = STATE_OPEN;
                        }
                    }
                    break;
            }

            if (mDragStateChangeListener != null && !mAborted && prevState != mState) {
                mDragStateChangeListener.onDragStateChanged(mState);
            }
        }
    };

    public static String getStateString(int state) {
        switch (state) {
            case STATE_CLOSE:
                return "state_close";

            case STATE_CLOSING:
                return "state_closing";

            case STATE_OPEN:
                return "state_open";

            case STATE_OPENING:
                return "state_opening";

            case STATE_DRAGGING:
                return "state_dragging";

            default:
                return "undefined";
        }
    }

    private int pxToDp(int px) {
        Resources resources = getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) (px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
