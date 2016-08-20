package top.cokernut.library.impl;

import android.support.v7.widget.RecyclerView;

public interface ItemTouchHelperCallbackImpl {

    void onMoved(int fromPosition, int toPosition);

    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);
}
