package ru.codingworkshop.gymm.ui.actual;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import ru.codingworkshop.gymm.R;

/**
 * Created by Радик on 16.09.2017 as part of the Gymm project.
 */

final class Matchers {
    private Matchers() {

    }

    static Matcher<View> currentPageItem(@IdRes int itemId) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                View root = item.getRootView();
                RecyclerView stepperRecyclerView = (RecyclerView) root.findViewById(R.id.actualExerciseSteps);
                ViewGroup pagerContainer = null;
                for (int i = 0; i < stepperRecyclerView.getChildCount(); i++) {
                    View stepContainer = stepperRecyclerView.getChildAt(i);
                    pagerContainer = (ViewGroup) stepContainer.findViewById(R.id.stepperItemActualSetsContainer);
                    if (pagerContainer.getChildCount() > 0) {
                        break;
                    }
                }
                ViewPager viewPager = (ViewPager) pagerContainer.getChildAt(0);
                int currentItem = viewPager.getCurrentItem();
                View pageView = viewPager.getChildAt(currentItem);
                View desiredView = pageView.findViewById(itemId);

                return item == desiredView;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is matches to specified page on specified step");
            }
        };
    }

    static Matcher<View> hasBackground(@DrawableRes int drawableId) {
        return new BoundedMatcher<View, View>(View.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("is matches to specified drawable");
            }

            @Override
            protected boolean matchesSafely(View item) {
                Drawable drawable = ContextCompat.getDrawable(item.getContext(), drawableId);

                Bitmap expected = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(expected);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);

                Bitmap actual = Bitmap.createBitmap(expected.getWidth(), expected.getHeight(), expected.getConfig());
                canvas.setBitmap(actual);
                item.getBackground().draw(canvas);

                return expected.sameAs(actual);
            }
        };
    }
}
