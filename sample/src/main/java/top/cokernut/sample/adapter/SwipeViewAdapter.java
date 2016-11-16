package top.cokernut.sample.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import java.util.List;

import top.cokernut.recyclerview.base.BaseRecyclerAdapter;
import top.cokernut.recyclerview.swipeview.ViewBinderHelper;
import top.cokernut.sample.R;
import top.cokernut.sample.viewholder.SwipeViewItem;

public class SwipeViewAdapter extends BaseRecyclerAdapter<String, SwipeViewItem> {
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public SwipeViewAdapter(Context context, List<String> data) {
        super(context, data);
    }

    @Override
    public SwipeViewItem createView(ViewGroup parent, int viewType) {
        return new SwipeViewItem(mInflater.inflate(R.layout.item_swipeview, parent, false));
    }

    /**
     * 保存状态
     */
    public void saveStates(Bundle outState) {
        binderHelper.saveStates(outState);
    }

    /**
     * 还原状态
     */
    public void restoreStates(Bundle inState) {
        binderHelper.restoreStates(inState);
    }

    @Override
    public void bindView(SwipeViewItem holder, int position) {
        binderHelper.bind(holder.swipeLayout, String.valueOf(position));
        holder.txt.setText(getItemModel(position));
        holder.help.setTag(getItemModel(position));
    }
}
