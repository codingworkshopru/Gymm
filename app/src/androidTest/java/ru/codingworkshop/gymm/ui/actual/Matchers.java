package ru.codingworkshop.gymm.ui.actual;

import android.content.Context;
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
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import ru.codingworkshop.gymm.R;

/**
 * Created by Радик on 16.09.2017 as part of the Gymm project.
 */

public final class Matchers {
    private Matchers() {

    }

    public static Matcher<View> currentPageItem(@IdRes int itemId) {
        return new TypeSafeMatcher<View>() {
            private View desiredView;

            private void findDesiredView(View rootView) {
                RecyclerView stepperRecyclerView = (RecyclerView) rootView.findViewById(R.id.actualExerciseList);
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
                desiredView = pageView.findViewById(itemId);
            }

            @Override
            protected boolean matchesSafely(View item) {
                if (desiredView == null) {
                    findDesiredView(item.getRootView());
                }

                return item == desiredView;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is matches to specified page on specified step");
            }
        };
    }

    public static Matcher<View> hasBackground(@DrawableRes int drawableId) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("is matches to specified drawable");
            }

            @Override
            protected boolean matchesSafely(View item) {
                return drawablesAreEqual(item.getBackground(), drawableId, item.getContext());
            }
        };
    }

    public static Matcher<View> hasImage(@DrawableRes int drawableId) {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
            @Override
            protected boolean matchesSafely(ImageView item) {
                return drawablesAreEqual(item.getDrawable(), drawableId, item.getContext());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is matches to specified drawable");
            }
        };
    }

    static private boolean drawablesAreEqual(Drawable actual, @DrawableRes int expectedDrawableId, Context context) {
        Bitmap actualBitmap = Bitmap.createBitmap(actual.getBounds().width(), actual.getBounds().height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(actualBitmap);
        actual.draw(canvas);

        Drawable expected = ContextCompat.getDrawable(context, expectedDrawableId);
        Bitmap expectedBitmap = Bitmap.createBitmap(expected.getIntrinsicWidth(), expected.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        expected.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas = new Canvas(expectedBitmap);
        expected.draw(canvas);

        return expectedBitmap.sameAs(actualBitmap);
    }
}
