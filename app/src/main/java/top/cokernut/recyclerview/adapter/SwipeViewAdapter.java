package top.cokernut.recyclerview.adapter;

import android.content.Context;
import android.view.ViewGroup;

import java.util.List;

import top.cokernut.recyclerview.R;
import top.cokernut.recyclerview.base.BaseRecyclerAdapter;
import top.cokernut.recyclerview.viewholder.SwipeViewItem;

public class SwipeViewAdapter extends BaseRecyclerAdapter<String, SwipeViewItem> {

    public SwipeViewAdapter(Context context, List<String> data) {
        super(context, data);
    }

    @Override
    public SwipeViewItem createHolder(ViewGroup parent, int viewType) {
        return new SwipeViewItem(mInflater.inflate(R.layout.item_swipeview, parent, false));
    }

    @Override
    public void bindHolder(SwipeViewItem holder, int position) {
        holder.txt.setText(getItemModel(position));
    }
}
