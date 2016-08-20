package top.cokernut.recyclerview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import top.cokernut.library.BaseRecyclerAdapter;
import top.cokernut.recyclerview.R;
import top.cokernut.recyclerview.adapter.SwipeAndDragAdapter;

public class SwipeAndDragActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SwipeAndDragAdapter mAdapter;
    private List<String> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_and_drag);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDatas.clear();
        for (int i = 0; i < 20; i++) {
            mDatas.add("卡片：" + i);
        }
        mAdapter = new SwipeAndDragAdapter(this, mDatas);
        mRecyclerView.setAdapter(mAdapter);
       /* mRecyclerView.addOnItemTouchListener(new OnRVItemTouchListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh, int position) {
                Toast.makeText(SwipeAndDragActivity.this, mAdapter.getItemModel(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh, int position) {

            }
        });*/
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(SwipeAndDragActivity.this, mAdapter.getItemModel(position), Toast.LENGTH_SHORT).show();
            }
        });
        mAdapter.setItemTouchHelper(mRecyclerView);
    }
}
