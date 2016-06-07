package top.cokernut.recyclerview.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import top.cokernut.recyclerview.R;
import top.cokernut.recyclerview.enumeration.LayoutManagerType;
import top.cokernut.recyclerview.fragment.TypeFragment;
import top.cokernut.recyclerview.widget.MyTabLayout;

public class TypeActivity extends AppCompatActivity {

    private MyTabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyAdapter mAdapter;
    private List<TypeFragment> fragments = new ArrayList<>();
    private List<Model> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);
        mTabLayout = (MyTabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        initData();
    }


    private void initData(){
        Model model1 = new Model("线性布局", LayoutManagerType.LINEAR);
        Model model2 = new Model("表格布局", LayoutManagerType.GRID);
        Model model3 = new Model("瀑布布局", LayoutManagerType.STAGGERED);
        mDatas.add(model1);
        mDatas.add(model2);
        mDatas.add(model3);
        initTabLayout();
    }

    /**
     * 初始化TabLayout
     */
    private void initTabLayout(){
        for (Model model : mDatas) {
            TypeFragment fragment = new TypeFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(TypeFragment.TYPE, model.type.getValue());
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
        mAdapter = new MyAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setupWithViewPager(mViewPager);
       /* mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/
    }

    class Model {
        public String title;
        public LayoutManagerType type;
        public Model(String title, LayoutManagerType type) {
            this.title = title;
            this.type = type;
        }
    }

    private class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mDatas.get(position).title;
        }

    }
}
