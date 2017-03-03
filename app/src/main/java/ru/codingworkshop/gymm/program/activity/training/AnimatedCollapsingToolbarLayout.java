package ru.codingworkshop.gymm.program.activity.training;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import ru.codingworkshop.gymm.R;


/**
 * Created by Радик on 03.03.2017.
 */

public class AnimatedCollapsingToolbarLayout extends CollapsingToolbarLayout {
    private static final String TAG = AnimatedCollapsingToolbarLayout.class.getSimpleName();

    private AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener;
    private AppBarLayout mAppBarLayout;
    private LinearLayout mAppBarContent;

    public AnimatedCollapsingToolbarLayout(Context context) {
        this(context, null);
    }

    public AnimatedCollapsingToolbarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedCollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mOnOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int maximalOffset = -appBarLayout.getTotalScrollRange();
                AccelerateInterpolator i = new AccelerateInterpolator(3f);
                float alpha = i.getInterpolation(1 - verticalOffset/(maximalOffset * 1f));
                mAppBarContent.setAlpha(alpha);
            }
        };
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAppBarLayout = (AppBarLayout) getParent();
        mAppBarContent = (LinearLayout) mAppBarLayout.findViewById(R.id.program_training_app_bar_content);
        mAppBarLayout.addOnOffsetChangedListener(mOnOffsetChangedListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAppBarLayout.removeOnOffsetChangedListener(mOnOffsetChangedListener);
    }
}
