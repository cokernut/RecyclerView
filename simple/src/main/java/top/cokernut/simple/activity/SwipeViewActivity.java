package top.cokernut.simple.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import top.cokernut.recyclerview.base.OnRVItemTouchListener;
import top.cokernut.simple.R;
import top.cokernut.simple.adapter.SwipeViewAdapter;

public class SwipeViewActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SwipeViewAdapter mAdapter;
    private List<String> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_view);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDatas.clear();
        for (int i = 0; i < 20; i++) {
            mDatas.add("卡片：" + i);
        }
        mAdapter = new SwipeViewAdapter(this, mDatas);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new OnRVItemTouchListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh, int position) {
                Toast.makeText(SwipeViewActivity.this, mAdapter.getItemModel(position), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
