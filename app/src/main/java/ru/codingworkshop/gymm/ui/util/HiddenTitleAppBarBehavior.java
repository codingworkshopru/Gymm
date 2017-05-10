package ru.codingworkshop.gymm.ui.util;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by Радик on 10.05.2017.
 */

public class HiddenTitleAppBarBehavior implements AppBarLayout.OnOffsetChangedListener {
    private final CollapsingToolbarLayout collapsingLayout;
    private final String title;
    private boolean isVisible;
    private int offsetTotal;
    private int offsetToolbarOnly;
    protected final Toolbar toolbar;
    private AppBarLayout appBarLayout;

    public HiddenTitleAppBarBehavior(Toolbar toolbar, String title) {
        this.collapsingLayout = (CollapsingToolbarLayout) toolbar.getParent();
        this.toolbar = toolbar;
        this.title = title;
        this.appBarLayout = (AppBarLayout) collapsingLayout.getParent();

        this.appBarLayout.addOnOffsetChangedListener(this);
        this.appBarLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                updateOffsetTotal();
            }
        });

        isVisible = true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (!isVisible && verticalOffset == offsetTotal)
            setVisible(true);

        if (isVisible && verticalOffset > offsetToolbarOnly)
            setVisible(false);
    }

    private void updateOffsetTotal() {
        offsetTotal = -appBarLayout.getTotalScrollRange();
        offsetToolbarOnly = offsetTotal + toolbar.getHeight();
    }

    protected void setVisible(boolean visible) {
        collapsingLayout.setTitle(visible ? title : " ");
        isVisible = visible;
    }
}
