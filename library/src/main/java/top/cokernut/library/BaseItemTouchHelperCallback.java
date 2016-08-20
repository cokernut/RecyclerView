package top.cokernut.library;

import android.graphics.Canvas;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;

import top.cokernut.library.impl.ItemTouchHelperImpl;

public abstract class BaseItemTouchHelperCallback extends ItemTouchHelper.Callback {

    public static final float ALPHA_FULL = 1.0f;

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager ||
                recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags = 0;
            return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags);
        } else {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags);
        }
    }

    @Override
    public abstract boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target);

    @Override
    public abstract void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    //当长按选中item的时候（拖拽开始的时候）调用
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof ItemTouchHelperImpl) {
                ItemTouchHelperImpl itemViewHolder = (ItemTouchHelperImpl) viewHolder;
                itemViewHolder.onItemSelected(actionState);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    //当手指松开的时候（拖拽完成的时候）调用
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setAlpha(ALPHA_FULL);
        if (viewHolder instanceof ItemTouchHelperImpl) {
            ItemTouchHelperImpl itemViewHolder = (ItemTouchHelperImpl) viewHolder;
            itemViewHolder.onItemClear();
        }
    }

    //要支持长按RecyclerView item进入拖动操作，你必须在isLongPressDragEnabled()方法中返回true。
    //或者，也可以调用ItemTouchHelper.startDrag(RecyclerView.ViewHolder) 方法来开始一个拖动。
    //
    /**
     * 这个方法是为了告诉ItemTouchHelper是否需要RecyclerView支持长按拖拽，默认返回是ture（即支持），
     * 理所当然我们要支持，所以我们没有重写，因为默认true。但是这样做是默认全部的item都可以拖拽，怎么实现部分item拖拽呢，
     * 查阅isLongPressDragEnabled方法的源码发现，是如果你想自定义触摸view，
     * 那么就使用startDrag(ViewHolder)方法。原来如此，我们可以在item的长按事件中得到当前item的ViewHolder ，
     * 然后调用ItemTouchHelper.startDrag(ViewHolder vh)就可以实现拖拽了，那就这么办：
     * 首先我们重写isLongPressDragEnabled返回false，我们要自己调用拖拽过程：
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    //而要在view任意位置触摸事件发生时启用滑动操作，则直接在isItemViewSwipeEnabled()中返回true就可以了。
    //或者，你也主动调用ItemTouchHelper.startSwipe(RecyclerView.ViewHolder) 来开始滑动操作。
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
}
