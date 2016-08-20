package top.cokernut.recyclerview.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import java.util.List;

import top.cokernut.library.BaseRecyclerAdapter;
import top.cokernut.library.swipeview.ViewBinderHelper;
import top.cokernut.recyclerview.R;
import top.cokernut.recyclerview.viewholder.SwipeViewItem;

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
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link android.app.Activity#onSaveInstanceState(Bundle)}
     */
    public void saveStates(Bundle outState) {
        binderHelper.saveStates(outState);
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link android.app.Activity#onRestoreInstanceState(Bundle)}
     */
    public void restoreStates(Bundle inState) {
        binderHelper.restoreStates(inState);
    }

    @Override
    public void bindView(SwipeViewItem holder, int position) {
        binderHelper.bind(holder.swipeLayout, mData.get(position));
        holder.txt.setText(getItemModel(position));
        holder.help.setTag(getItemModel(position));
    }
}
