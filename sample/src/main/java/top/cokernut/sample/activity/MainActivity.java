package top.cokernut.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import top.cokernut.sample.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView mSimpleCV;
    private CardView mTypeCV;
    private CardView mSwipeDragCV;
    private CardView mSwipeViewCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSimpleCV   = (CardView) findViewById(R.id.cv_simple);
        mTypeCV     = (CardView) findViewById(R.id.cv_type);
        mSwipeDragCV= (CardView) findViewById(R.id.cv_swipe_drag);
        mSwipeViewCV= (CardView) findViewById(R.id.cv_swipeview);

        mSimpleCV   .setOnClickListener(this);
        mTypeCV     .setOnClickListener(this);
        mSwipeDragCV.setOnClickListener(this);
        mSwipeViewCV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_simple:
                startActivity(new Intent(MainActivity.this, SimpleActivity.class));
                break;
            case R.id.cv_type:
                startActivity(new Intent(MainActivity.this, TypeActivity.class));
                break;
            case R.id.cv_swipe_drag:
                startActivity(new Intent(MainActivity.this, SwipeAndDragActivity.class));
                break;
            case R.id.cv_swipeview:
                startActivity(new Intent(MainActivity.this, SwipeViewActivity.class));
                break;
        }
    }
}
