package top.cokernut.recyclerview.callback;

import android.support.v7.widget.RecyclerView;

import top.cokernut.recyclerview.adapter.SwipeAndDragAdapter;

public class ItemTouchHelperCallback extends BaseItemTouchHelperCallback<SwipeAndDragAdapter> {

    public ItemTouchHelperCallback(SwipeAndDragAdapter adapter) {
        super(adapter);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.removeItem(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    //而要在view任意位置触摸事件发生时启用滑动操作，则直接在sItemViewSwipeEnabled()中返回true就可以了。
    //或者，你也主动调用ItemTouchHelper.startSwipe(RecyclerView.ViewHolder) 来开始滑动操作。
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
}
