package ru.codingworkshop.gymm.ui.actual;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Радик on 16.09.2017 as part of the Gymm project.
 */
final class ActualSetsViewPager extends ViewPager {
    private int heightMeasureSpec;

    public ActualSetsViewPager(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.heightMeasureSpec == 0) {
            this.heightMeasureSpec = getHeightMeasureSpec(widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, this.heightMeasureSpec);
    }

    private int getHeightMeasureSpec(int widthMeasureSpec, int heightMeasureSpec) {
        View child = getChildAt(0);
        if (child != null) {
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            child.measure(widthMeasureSpec, childHeightMeasureSpec);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(), MeasureSpec.EXACTLY);
        }
        return heightMeasureSpec;
    }
}
