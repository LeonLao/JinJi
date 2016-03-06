package com.example.jinji;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.NumberPicker;
import android.widget.ScrollView;

/**
 * Created by jack on 2016/3/6.
 */
public class MyScrollView extends ScrollView {

    private OncrollListenter oncrollListenter;
    private int lastScrollY;


    public MyScrollView(Context context) {
        super(context,null);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs,0);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOncrollListenter(OncrollListenter oncrollListenter){
        this.oncrollListenter = oncrollListenter;
    }


    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            int scrollY = MyScrollView.this.getScrollY();

            if (lastScrollY!=scrollY){
                lastScrollY = scrollY;
                handler.sendMessageDelayed(handler.obtainMessage(),5);
            }
            if (oncrollListenter !=null){
                oncrollListenter.onScroll(scrollY);
            }
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (oncrollListenter != null){
            oncrollListenter.onScroll(lastScrollY = this.getScrollY());
        }
        switch (ev.getAction()){
            case MotionEvent.ACTION_UP:
                handler.sendMessageDelayed(handler.obtainMessage(),20);
                break;
        }


        return super.onTouchEvent(ev);
    }

    public interface OncrollListenter{
        public void onScroll(int scrollY);
    }
}
