package top.cokernut.recyclerview.viewholder;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import top.cokernut.recyclerview.R;
import top.cokernut.recyclerview.base.BaseRecyclerAdapter;
import top.cokernut.recyclerview.swipeview.SwipeRevealLayout;

public class SwipeViewItem extends BaseRecyclerAdapter.BaseViewHolder<String> {
    public TextView txt;
    public TextView help;
    public SwipeRevealLayout swipeLayout;

    public SwipeViewItem(View itemView) {
        super(itemView);
        txt = (TextView) itemView.findViewById(R.id.tv_txt);
        help = (TextView) itemView.findViewById(R.id.tv_help);
        swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
    }

    @Override
    public void bind(final String data, int position) {
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, data, Snackbar.LENGTH_SHORT).setAction("help", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "Action", Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });
        swipeLayout.setSwipeListener(new SwipeRevealLayout.SwipeListener() {
            @Override
            public void onClosed(SwipeRevealLayout view) {

            }

            @Override
            public void onOpened(SwipeRevealLayout view) {

            }

            @Override
            public void onSlide(SwipeRevealLayout view, float slideOffset) {

            }
        });
    }
}
