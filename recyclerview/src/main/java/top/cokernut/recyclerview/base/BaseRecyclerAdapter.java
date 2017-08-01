package top.cokernut.recyclerview.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import top.cokernut.recyclerview.impl.ItemTouchHelperCallbackImpl;
import top.cokernut.recyclerview.impl.ItemTouchHelperImpl;

public abstract class BaseRecyclerAdapter<E, VH extends BaseRecyclerAdapter.BaseViewHolder> extends RecyclerView.Adapter<VH>
    implements ItemTouchHelperCallbackImpl, View.OnClickListener {

    protected List<E> mData;
    protected LayoutInflater mInflater;
    protected Context mContext;
    private ItemTouchHelper mItemTouchHelper;
    private OnItemClickListener mOnItemClickListener;
    private int headerViewCount = 0; //头部数量
    private int footerViewCount = 0; //底部数量

    protected BaseRecyclerAdapter(Context context, List<E> data) {
        this.mContext = context;
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
    }

    public boolean isHeaderView(int position) {
        return headerViewCount != 0 && position < headerViewCount;
    }

    public boolean isFooterView(int position) {
        return false;
    }

    public void setHeaderNum(int num) {
        headerViewCount = num;
    }

    public void setFooterNum(int num) {
        footerViewCount = num;
    }

    //拖动和滑动事件
    public void setItemTouchHelper(RecyclerView recyclerView) {
        setItemTouchHelper(recyclerView, new SimpleItemTouchHelperCallback(this));
    }

    //拖动和滑动事件
    public void setItemTouchHelper(RecyclerView recyclerView, BaseItemTouchHelperCallback callback) {
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    //有作用的前提是：BaseItemTouchHelperCallback中isLongPressDragEnabled()方法中返回false，
    //即继承BaseItemTouchHelperCallback并重写isLongPressDragEnabled()方法
    //并且重写setItemTouchHelper()方法设置对应的BaseItemTouchHelperCallback
    public void startDrag(RecyclerView.ViewHolder vh) {
        mItemTouchHelper.startDrag(vh);
    }

    //有作用的前提是：BaseItemTouchHelperCallback中isItemViewSwipeEnabled()方法中返回false，
    //即继承BaseItemTouchHelperCallback并重写isItemViewSwipeEnabled()方法，
    //并且重写setItemTouchHelper()方法设置对应的BaseItemTouchHelperCallback
    public void startSwipe(RecyclerView.ViewHolder vh) {
        mItemTouchHelper.startSwipe(vh);
    }

    public void setData(List<E> datas) {
        setData(datas, true);
    }

    public void setData(List<E> datas, boolean isChanged) {
        this.mData = datas;
        if (isChanged) {
            notifyDataSetChanged();
        }
    }

    public List<E> getData() {
        return mData;
    }

    //添加数据
    public void addItem(E model) {
        mData.add(model);
        notifyItemInserted(mData.size() - 1);
    }

    //添加数据
    public void addItem(E model, int position) {
        mData.add(position, model);
        notifyItemInserted(position);
    }

    //添加数据集
    public void addItems(List<E> datas, int position) {
        mData.addAll(position, datas);
        notifyItemRangeInserted(position, datas.size());
    }

    //删除数据
    public void removeItem(E model) {
        int position = mData.indexOf(model);
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size() - position);
    }

    //删除数据
    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size() - position);
    }

    //改变数据
    public void changeItem(E model) {
        int position = mData.indexOf(model);
        mData.set(position, model);
        notifyItemChanged(position);
    }

    //改变数据
    public void changeItem(E model, int position) {
        mData.set(position, model);
        notifyItemChanged(position);
    }

    //移动数据
    public void moveItem(int fromPosition, int toPosition) {
        mData.add(toPosition, mData.get(fromPosition));
        if (fromPosition > toPosition) {
            mData.remove(fromPosition + 1);
        } else {
            mData.remove(fromPosition);
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    //其中一种item点击事件的实现
    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view, (int)view.getTag());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * 设置item监听
     * @param mOnItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    /**
     * item移动
     * @param fromPosition 初始位置
     * @param toPosition 目的位置
     */
    @Override
    public void onMoved(int fromPosition, int toPosition) {
        moveItem(fromPosition, toPosition);
    }

    /**
     * Item滑动操作
     * @param viewHolder item
     * @param direction 方向
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder instanceof BaseViewHolder) {
            ((BaseViewHolder) viewHolder).swipe(direction);
        }
    }

    @Override
    public final int getItemViewType(int position) {
        return getItemType(position);
    }

    /**
     * item Type
     * @param position
     * @return
     */
    protected int getItemType(int position) {
        return 0;
    }

    /**
     * 取得对应位置的Model
     * @param position 位置
     * @return
     */
    public E getItemModel(int position) {
        return mData.get(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return createView(parent, viewType);
    }

    protected abstract VH createView(ViewGroup parent, int viewType);

    @Override
    public final void onBindViewHolder(VH holder, int position) {
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(this);
            holder.itemView.setTag(position);
        }
        bindView(holder, position);
        holder.bind(mData.get(position), position);
    }

    protected abstract void bindView(VH holder, int position);

    public static abstract class BaseViewHolder<E> extends RecyclerView.ViewHolder implements ItemTouchHelperImpl {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onItemSelected(int actionState) {}

        //可在此方法中做缓存Items数据的操作 mData
        @Override
        public void onItemClear() {}

        public void swipe(int direction) {}

        public void bind(E data, int position) {}
    }
}