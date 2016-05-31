package top.cokernut.recyclerview.callback;

import android.support.v7.widget.RecyclerView;

import top.cokernut.recyclerview.adapter.SwipeAndDragAdapter;
import top.cokernut.recyclerview.base.BaseItemTouchHelperCallback;

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
}
