package com.example.chl.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 */

public class InterceptRecycleView extends RecyclerView {
    public InterceptRecycleView(Context context) {
        super(context);
    }

    public InterceptRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return false;
    }
}
