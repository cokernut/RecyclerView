package top.cokernut.sample.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import top.cokernut.sample.R;
import top.cokernut.sample.adapter.SimpleAdapter;
import top.cokernut.recyclerview.enumeration.LayoutManagerType;

public class TypeFragment extends Fragment {
    public static final String TYPE = "type";
    private LayoutManagerType type = LayoutManagerType.LINEAR;

    private RecyclerView mRecyclerView;
    private SimpleAdapter mAdapter;
    private ArrayList<String> mDatas = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_type, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_view);
       // mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        if (type == LayoutManagerType.LINEAR) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else if (type == LayoutManagerType.GRID) {
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            mRecyclerView.setLayoutManager(gridLayoutManager);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (mAdapter.isHeaderView(position) ? gridLayoutManager.getSpanCount() : 1);
                }
            });
        } else {
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        initData();
        mAdapter = new SimpleAdapter(getActivity(), mDatas, type);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickLitener(new SimpleAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity(), "卡片" + position, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void initData() {
        mDatas.clear();
        for (int i = 0; i < 20; i++) {
            mDatas.add("卡片：" + i);
        }
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        if (args.containsKey(TYPE)) {
            int typeValue = args.getInt(TYPE, LayoutManagerType.LINEAR.getValue());
            if (LayoutManagerType.LINEAR.getValue() == typeValue) {
                type = LayoutManagerType.LINEAR;
            } else if (LayoutManagerType.GRID.getValue() == typeValue) {
                type = LayoutManagerType.GRID;
            } else if (LayoutManagerType.STAGGERED.getValue() == typeValue) {
                type = LayoutManagerType.STAGGERED;
            }
        }
    }
}
