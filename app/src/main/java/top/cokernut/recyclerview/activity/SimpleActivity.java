package top.cokernut.recyclerview.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import top.cokernut.library.OnRVScrollListener;
import top.cokernut.recyclerview.R;
import top.cokernut.recyclerview.adapter.SimpleAdapter;
import top.cokernut.library.dialog.CustomDialog;
import top.cokernut.library.enumeration.LayoutManagerType;

public class SimpleActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private SimpleAdapter mAdapter;
    private SwipeRefreshLayout mSRL;
    private TextView mLoadingTV;
    private ImageView mGoTopIV;
    private boolean isLoading;// 是否正在加载数据
    private boolean hasMore = true;// 是否还有更多数据
    private int mPageIndex = 1;// 当前分页索引
    private int mPageSize = 10;// 分页大小
    private LayoutManagerType type = LayoutManagerType.LINEAR;
    private int newType = 1;
    private ArrayList<String> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLoadingTV = (TextView) findViewById(R.id.tv_loading);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_view);
        mGoTopIV = (ImageView) findViewById(R.id.iv_go_top);
        mGoTopIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    ((LinearLayoutManager)mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(0, 0);
                } else if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                    ((StaggeredGridLayoutManager)mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(0, 0);
                } else {
                    ((GridLayoutManager)mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(0, 0);
                }
                mGoTopIV.setVisibility(View.GONE);
            }
        });
        mSRL = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSRL.setOnRefreshListener(this);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        getData(true);
        mRecyclerView.addOnScrollListener(new OnRVScrollListener() {
            @Override
            public void onBottom() {
                if (!isLoading) {
                    if (hasMore) {
                        mLoadingTV.setVisibility(View.VISIBLE);
                        getData(false);
                    }
                }
            }

            @Override
            public void onTop() {
                mGoTopIV.setVisibility(View.GONE);
                mSRL.setEnabled(true);
            }

            @Override
            public void onCenter() {
                mGoTopIV.setVisibility(View.VISIBLE);
                mSRL.setEnabled(false);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_simple, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change:
                if (newType > 4) {
                    newType = 1;
                } else {
                    newType++;
                }
                onRefresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void getData(boolean isRefresh) {
        if (isLoading)
            return;
        if (isRefresh) {
            mPageIndex = 1;
            hasMore = true;
            mSRL.setRefreshing(true);
        }
        isLoading = true;
        if (mPageIndex <= 1) {
            mDatas.clear();
        }
        if (mPageIndex < 5) {
            ArrayList<String> datas = new ArrayList<>();
            for (int i = 0; i < mPageSize; i++) {
                datas.add("卡片：" + ((mPageIndex - 1) * mPageSize + i));
            }
            mDatas.addAll(datas);
            initViewData();
            mPageIndex++;
        } else {
            hasMore = false;
        }
        if (isRefresh) {
            mSRL.setRefreshing(false);
        }
        mLoadingTV.setVisibility(View.GONE);
        isLoading = false;
    }

    private void initViewData() {
        if (mAdapter == null) {
            mAdapter = new SimpleAdapter(SimpleActivity.this, mDatas, type);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setData(mDatas);
            if (type.getValue() != newType) {
                if (newType == LayoutManagerType.LINEAR.getValue()) {
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                    type = LayoutManagerType.LINEAR;
                } else if (newType == LayoutManagerType.GRID.getValue()) {
                    mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                    type = LayoutManagerType.GRID;
                } else if (newType == LayoutManagerType.STAGGERED.getValue()){
                    mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    type = LayoutManagerType.STAGGERED;
                } else if (newType == LayoutManagerType.LINEAR_VERTICAL_TRUE.getValue()) {
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
                    type = LayoutManagerType.LINEAR_VERTICAL_TRUE;
                } else if (newType == LayoutManagerType.LINEAR_HORIZONTAL.getValue()){
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                    type = LayoutManagerType.LINEAR_HORIZONTAL;
                }
                mAdapter.setType(type);
            }
        }
        if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {//设置头部View占据整行空间
            ((GridLayoutManager)mRecyclerView.getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (mAdapter.isHeaderView(position) ? ((GridLayoutManager)mRecyclerView.getLayoutManager()).getSpanCount() : 1);
                }
            });
        }
        mAdapter.setOnItemClickLitener(new SimpleAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, final int position) {
                Toast.makeText(SimpleActivity.this, "卡片" + position, Toast.LENGTH_SHORT).show();
                CustomDialog.Builder builder = new CustomDialog.Builder(SimpleActivity.this);
                builder.setMessage("添加/删除Item");
                builder.setTitle("提示");
                builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.addItem("卡片" + mDatas.size(), position + 1);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.removeItem(position);
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public void onRefresh() {
        getData(true);
    }
}