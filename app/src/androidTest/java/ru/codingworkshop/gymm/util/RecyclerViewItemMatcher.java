package ru.codingworkshop.gymm.util;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by Радик on 01.09.2017 as part of the Gymm project.
 */
public final class RecyclerViewItemMatcher {
    private static @IdRes int recyclerViewId;

    public static void setRecyclerViewId(@LayoutRes int recyclerViewId) {
        RecyclerViewItemMatcher.recyclerViewId = recyclerViewId;
    }

    public static Matcher<View> itemAtPosition(@IdRes int targetViewId, int position) {
        if (recyclerViewId == 0) {
            throw new IllegalStateException("You should set recycler view's id or use another method");
        }

        return itemAtPosition(recyclerViewId, targetViewId, position);
    }

    public static Matcher<View> itemAtPosition(@IdRes int recyclerViewId, @IdRes int targetViewId, int position) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                View targetView = findItemView(item, recyclerViewId, targetViewId, position);
                return targetView != null && item == targetView;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is matches to specified item of specified recycler view");
            }
        };
    }

    private static View findItemView(View item, @IdRes int recyclerViewId, @IdRes int targetViewId, int position) {
        RecyclerView rv = (RecyclerView) item.getRootView().findViewById(recyclerViewId);
        if (rv == null) {
            return null;
        }

        RecyclerView.ViewHolder viewHolder = rv.findViewHolderForAdapterPosition(position);
        if (viewHolder == null) {
            return null;
        }

        View child = viewHolder.itemView;
        if (child == null) {
            return null;
        }

        return child.findViewById(targetViewId);
    }
}
