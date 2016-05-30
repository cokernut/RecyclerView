package top.cokernut.recyclerview.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import top.cokernut.recyclerview.R;
import top.cokernut.recyclerview.enumeration.LayoutManagerType;

public class SimpleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private LayoutInflater mInflater;
    private ArrayList<String> mDatas = new ArrayList<>();
    private static final int HEAD = 0;
    private static final int BIG = 1;
    private static final int SMALL = 2;
    private LayoutManagerType type = LayoutManagerType.LINEAR;
    private int headerViewCount = 1; //头部数量
    private Activity mContext;
    private OnItemClickLitener mOnItemClickLitener;

    public SimpleAdapter(Activity context, ArrayList<String> datas, LayoutManagerType type) {
        this.mContext = context;
        this.mDatas = datas;
        this.type = type;
        this.mInflater = LayoutInflater.from(context);
    }

    public boolean isHeaderView(int position) {
        return headerViewCount != 0 && position < headerViewCount;
    }

    //添加数据
    public void addItem(String model, int position) {
        mDatas.add(position, model);
        notifyItemInserted(position);
    }

    //添加数据集
    public void setData(ArrayList<String> data) {
        this.mDatas= data;
        notifyDataSetChanged();
    }

    //删除数据
    public void removeItem(String model) {
        int position = mDatas.indexOf(model);
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    //删除数据
    public void removeItem(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickLitener != null) {
            mOnItemClickLitener.onItemClick(view, (int)view.getTag());
        }
    }

    public void setType(LayoutManagerType type) {
        this.type = type;
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEAD) {
            return new HeadViewHolder(mInflater.inflate(R.layout.item_head, parent, false));
        } else if (viewType == SMALL) {
            return new SmallViewHolder(mInflater.inflate(R.layout.item_0, parent, false));
        } else {
            return new BigViewHolder(mInflater.inflate(R.layout.item_1, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeadViewHolder) {
            HeadViewHolder holder1 = (HeadViewHolder) holder;
            holder1.txt.setText(mDatas.get(position));
        } else if (holder instanceof SmallViewHolder) {
            SmallViewHolder holder1 = (SmallViewHolder) holder;
            holder1.txt.setText(mDatas.get(position));
        } else {
            BigViewHolder holder1 = (BigViewHolder) holder;
            holder1.txt.setText(mDatas.get(position));
        }
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(this);
            holder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (type == LayoutManagerType.GRID) {
            if (position == 0) {
                return HEAD;
            } else if (position % 3 == 0) {
                return SMALL;
            } else {
                return BIG;
            }
        } else if (position % 2 == 0) {
            return SMALL;
        } else {
            return BIG;
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    class HeadViewHolder extends RecyclerView.ViewHolder{
        TextView txt;

        public HeadViewHolder(View itemView) {
            super(itemView);
            txt = (TextView) itemView.findViewById(R.id.tv_txt);
        }
    }

    class BigViewHolder extends RecyclerView.ViewHolder{
        TextView txt;

        public BigViewHolder(View itemView) {
            super(itemView);
            txt = (TextView) itemView.findViewById(R.id.tv_txt);
        }
    }

    class SmallViewHolder extends RecyclerView.ViewHolder{
        TextView txt;

        public SmallViewHolder(View itemView) {
            super(itemView);
            txt = (TextView) itemView.findViewById(R.id.tv_txt);
        }
    }
}
