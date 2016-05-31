package top.cokernut.recyclerview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import top.cokernut.recyclerview.R;
import top.cokernut.recyclerview.base.BaseRecyclerAdapter;
import top.cokernut.recyclerview.viewholder.SwipeAndDragItem;

public class SwipeAndDragAdapter extends BaseRecyclerAdapter<String, SwipeAndDragItem> {

    public SwipeAndDragAdapter(Context context, List<String> data) {
        super(context, data);
    }

    @Override
    public SwipeAndDragItem createHolder(ViewGroup parent, int viewType) {
        return new SwipeAndDragItem(mInflater.inflate(R.layout.item_swipe_and_drag, parent, false));
    }

    @Override
    public void bindHolder(SwipeAndDragItem holder, int position) {
        holder.txt.setText(getItemModel(position));
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int position) {
        super.onSwiped(viewHolder, position);
        removeItem(viewHolder.getAdapterPosition());
    }
}
