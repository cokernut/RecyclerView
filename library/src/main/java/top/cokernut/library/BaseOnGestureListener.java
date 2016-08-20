package top.cokernut.library;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * OnGestureListener主要回调各种单击事件，而OnDoubleTapListener回调各种双击事件
 * sdk 提供了一个外部类SimpleOnGestureListener 这个类实现了上面两个接口的所有方法
 */
public abstract class BaseOnGestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    //一次单独的轻击抬起操作,也就是轻击一下屏幕，就是普通点击事件
    public abstract boolean onSingleTapUp(MotionEvent e);

    //长按触摸屏，超过一定时长，就会触发这个事件
    public void onLongPress(MotionEvent e) {}

    //在屏幕上滑动事件
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    //滑屏，用户按下触摸屏、快速移动后松开
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    //如果是按下的时间超过瞬间，而且在按下的时候没有松开或者是拖动的，那么onShowPress就会执行
    public void onShowPress(MotionEvent e) {}

    //用户按下屏幕就会触发
    public boolean onDown(MotionEvent e) {
        return false;
    }

    //双击事件
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    //双击间隔中发生的动作。指触发onDoubleTap以后，在双击之间发生的其它动作
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    //单击事件。用来判定该次点击是SingleTap而不是DoubleTap，
    //如果连续点击两次就是DoubleTap手势，如果只点击一次，
    //系统等待一段时间后没有收到第二次点击则判定该次点击为SingleTap而不是DoubleTap，
    //然后触发SingleTapConfirmed事件
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

}
