package top.cokernut.recyclerview.base;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import top.cokernut.recyclerview.enumeration.LayoutManagerType;

public abstract class OnRVScrollListener extends RecyclerView.OnScrollListener {

    /**
     * layoutManager的类型（枚举）
     */
    protected LayoutManagerType layoutManagerType;

    /**
     * 最后一个的位置
     */
    private int[] firstPositions;

    /**
     * 第一个的位置
     */
    private int[] lastPositions;

    /**
     * 最后一个可见的item的位置
     */
    private int lastVisibleItemPosition;

    /**
     * 第一个可见的item的位置
     */
    private int firstVisibleItemPosition;

    /**
     * 当前滑动的状态
     */
    private int currentScrollState = 0;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();


        //第一种方式
        if (layoutManager instanceof LinearLayoutManager) {
            layoutManagerType = LayoutManagerType.LINEAR;
        } else if (layoutManager instanceof GridLayoutManager) {
            layoutManagerType = LayoutManagerType.GRID;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            layoutManagerType = LayoutManagerType.STAGGERED;
        } else {
            throw new RuntimeException(
                    "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
        }

        switch (layoutManagerType) {
            case LINEAR:
                firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case GRID:
                firstVisibleItemPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case STAGGERED:
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                if (firstPositions == null) {
                    firstPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                }
                staggeredGridLayoutManager.findFirstVisibleItemPositions(firstPositions);
                firstVisibleItemPosition = firstPositions[0];
                if (lastPositions == null) {
                    lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                }
                staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                lastVisibleItemPosition = findMax(lastPositions);
                break;
            default:
                break;
        }
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        if (firstVisibleItemPosition == 0) {
            onTop();
        } else if (visibleItemCount > 0 && lastVisibleItemPosition + 1 >= totalItemCount) {
            onBottom();
        } else {
            onCenter();
        }

/*
        //第二种方法

        if (layoutManager instanceof LinearLayoutManager) {
            if (OrientationHelper.HORIZONTAL == ((LinearLayoutManager)layoutManager).getOrientation()) {
                horizontalScroll(recyclerView);
            } else {
                verticalScroll(recyclerView);
            }
        } else if (layoutManager instanceof GridLayoutManager) {
            if (OrientationHelper.VERTICAL == ((GridLayoutManager)layoutManager).getOrientation()) {
                verticalScroll(recyclerView);
            } else {
                horizontalScroll(recyclerView);
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            if (OrientationHelper.VERTICAL == ((StaggeredGridLayoutManager)layoutManager).getOrientation()) {
                verticalScroll(recyclerView);
            } else {
                horizontalScroll(recyclerView);
            }
        } else {
            throw new RuntimeException(
                    "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
        }


        //第三种方法
        if (layoutManager instanceof LinearLayoutManager) {
            if (OrientationHelper.VERTICAL == ((LinearLayoutManager)layoutManager).getOrientation()) {
                canVerticalScroll(recyclerView);
            } else {
                canHorizontalScroll(recyclerView);
            }
        } else if (layoutManager instanceof GridLayoutManager) {
            if (OrientationHelper.VERTICAL == ((GridLayoutManager)layoutManager).getOrientation()) {
                canVerticalScroll(recyclerView);
            } else {
                canHorizontalScroll(recyclerView);
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            if (OrientationHelper.VERTICAL == ((StaggeredGridLayoutManager)layoutManager).getOrientation()) {
                canVerticalScroll(recyclerView);
            } else {
                canHorizontalScroll(recyclerView);
            }
        } else {
            throw new RuntimeException(
                    "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
        }*/
    }

    /**
     * Extent：显示区域的长度
     * Offset：已经滑出屏幕外的长度
     * Range：总长度，包括：滑出屏幕外的长度+显示区域的长度+还未显示屏幕外的长度
     * @param recyclerView
     */
    private void horizontalScroll(RecyclerView recyclerView) {
        if (recyclerView.computeHorizontalScrollExtent() + recyclerView.computeHorizontalScrollOffset() >= recyclerView.computeHorizontalScrollRange()) {
            onBottom();
        } else if (recyclerView.computeHorizontalScrollOffset() <= 0) {
            onTop();
        } else {
            onCenter();
        }
    }

    private void verticalScroll(RecyclerView recyclerView) {
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange()) {
            onBottom();
        } else if (recyclerView.computeVerticalScrollOffset() <= 0) {
            onTop();
        } else {
            onCenter();
        }
    }

    private void canHorizontalScroll(RecyclerView recyclerView) {
        if (!recyclerView.canScrollHorizontally(1)) {
            onBottom();
        } else if (!recyclerView.canScrollHorizontally(-1)) {
            onTop();
        } else {
            onCenter();
        }
    }
    /**
     * RecyclerView.canScrollVertically(1)的值表示是否能向上滚动，false表示已经滚动到底部
     * RecyclerView.canScrollVertically(-1)的值表示是否能向下滚动，false表示已经滚动到顶部
     * @param recyclerView
     */
    private void canVerticalScroll(RecyclerView recyclerView) {
        if (!recyclerView.canScrollVertically(1)) {
            onBottom();
        } else if (!recyclerView.canScrollVertically(-1)) {
            onTop();
        } else {
            onCenter();
        }
    }


    public abstract void onBottom();

    public abstract void onTop();

    public void onCenter(){}

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        currentScrollState = newState;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        if ((visibleItemCount > 0 && currentScrollState == RecyclerView.SCROLL_STATE_IDLE &&
                (lastVisibleItemPosition) >= totalItemCount - 1)) {
           // onBottom();
        }
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}