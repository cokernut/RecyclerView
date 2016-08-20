package top.cokernut.recyclerview.callback;

import android.support.v7.widget.RecyclerView;

import top.cokernut.library.BaseItemTouchHelperCallback;
import top.cokernut.recyclerview.adapter.SwipeAndDragAdapter;

public class ItemTouchHelperCallback extends BaseItemTouchHelperCallback {

    private SwipeAndDragAdapter mAdapter;

    public ItemTouchHelperCallback(SwipeAndDragAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();//得到拖动ViewHolder的position
        int toPosition = target.getAdapterPosition();//得到目标ViewHolder的position
        mAdapter.onMoved(fromPosition, toPosition);
        return true;
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
