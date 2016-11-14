package top.cokernut.sample.viewholder;

import android.graphics.Color;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import top.cokernut.recyclerview.base.BaseRecyclerAdapter;
import top.cokernut.sample.R;

public class SwipeAndDragItem extends BaseRecyclerAdapter.BaseViewHolder {
    public TextView txt;

    public SwipeAndDragItem(View itemView) {
        super(itemView);
        txt = (TextView) itemView.findViewById(R.id.tv_txt);
    }

    @Override
    public void onItemSelected(int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            itemView.setBackgroundColor(Color.LTGRAY);
        }
    }

    @Override
    public void onItemClear() {
        itemView.setBackgroundColor(Color.parseColor("#A7CC36"));
    }
}
