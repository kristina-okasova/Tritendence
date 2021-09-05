package com.example.tritendence.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

@SuppressLint("ViewConstructor")
public class ExpandableListScrollable extends ExpandableListView {

    public ExpandableListScrollable(Context context) {
        super(context);
    }

    public ExpandableListScrollable(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableListScrollable(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}
