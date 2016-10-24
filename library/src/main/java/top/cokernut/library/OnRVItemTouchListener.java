package top.cokernut.library;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

/**
 * item点击事件处理
 */
public abstract class OnRVItemTouchListener implements RecyclerView.OnItemTouchListener {
    private GestureDetectorCompat mGestureDetector;
    private RecyclerView recyclerView;

    public OnRVItemTouchListener(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        mGestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new ItemTouchHelperGestureListener());
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}

    //sdk 提供了一个外部类SimpleOnGestureListener 这个类实现了和BaseOnGestureListener一样的功能
    private class ItemTouchHelperGestureListener extends BaseOnGestureListener {

        //点击事件
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null) {
                RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
                onItemClick(vh, vh.getAdapterPosition());
            }
            return true;
        }

        //长按事件
        @Override
        public void onLongPress(MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child!=null) {
                RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
                onItemLongClick(vh, vh.getAdapterPosition());
            }
        }
    }

    //点击事件
    public abstract void onItemClick(RecyclerView.ViewHolder vh, int position);
    public void onItemLongClick(RecyclerView.ViewHolder vh, int position){}
}
