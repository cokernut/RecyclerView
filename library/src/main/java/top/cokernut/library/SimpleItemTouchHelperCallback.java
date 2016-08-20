package top.cokernut.library;

import android.support.v7.widget.RecyclerView;

public class SimpleItemTouchHelperCallback extends BaseItemTouchHelperCallback {

    private BaseRecyclerAdapter mAdapter;

    public SimpleItemTouchHelperCallback(BaseRecyclerAdapter adapter) {
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
        mAdapter.onSwiped(viewHolder, direction);
    }
}
