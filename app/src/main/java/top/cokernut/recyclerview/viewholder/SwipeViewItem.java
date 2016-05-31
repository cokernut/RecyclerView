package top.cokernut.recyclerview.viewholder;

import android.view.View;
import android.widget.TextView;

import top.cokernut.recyclerview.R;
import top.cokernut.recyclerview.base.BaseRecyclerAdapter;

public class SwipeViewItem extends BaseRecyclerAdapter.BaseViewHolder {
    public TextView txt;

    public SwipeViewItem(View itemView) {
        super(itemView);
        txt = (TextView) itemView.findViewById(R.id.tv_txt);
    }
}
