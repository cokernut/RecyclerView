package top.cokernut.recyclerview.impl;

import android.support.v7.widget.helper.ItemTouchHelper;

public interface ItemTouchHelperViewHolderImpl {
    /**
     * Called when the {@link ItemTouchHelper} first registers an item as being moved or swiped.
     * Implementations should update the item view to indicate it's active state.
     */
    void onItemSelected(int actionState);


    /**
     * Called when the {@link ItemTouchHelper} has completed the move or swipe, and the active item
     * state should be cleared.
     */
    void onItemClear();
}
