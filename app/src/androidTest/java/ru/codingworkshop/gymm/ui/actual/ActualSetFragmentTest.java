package ru.codingworkshop.gymm.ui.actual;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Created by Радик on 12.09.2017 as part of the Gymm project.
 */

@RunWith(AndroidJUnit4.class)
public class ActualSetFragmentTest {
    @Rule
    public ActivityTestRule<SimpleFragmentActivity> activityTestRule =
            new ActivityTestRule<>(SimpleFragmentActivity.class);

    @Before
    public void setUp() throws Exception {
        activityTestRule.getActivity().setFragment(ActualSetFragment.newInstance(0, 5, true));
    }

    @Test
    public void incorrectRepsInput() throws Exception {
        testOnEmptyFieldError(R.id.actualSetRepsCountEditText, R.id.actualSetRepsCountLayout,
                R.string.actual_training_activity_stepper_item_reps_error);
    }

    @Test
    public void incorrectWeightInput() throws Exception {
        testOnEmptyFieldError(R.id.actualSetWeightEditText, R.id.actualSetWeightLayout,
                R.string.actual_training_activity_stepper_item_weight_error);
    }

    private void testOnEmptyFieldError(@IdRes int editText, @IdRes int layout, @StringRes int errorText) {
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.actualSetDoneButton)).perform(click());
        onView(withId(layout)).check(matches(hasErrorText(errorText)));

        onView(withId(editText)).perform(typeText("1"));

        Espresso.closeSoftKeyboard();

        onView(withId(R.id.actualSetDoneButton)).perform(click());
        onView(withId(layout)).check(matches(not(hasErrorText())));
    }

    @Test
    public void emptyWeightAllowedInput() throws Exception {
        activityTestRule.getActivity().replaceFragment(ActualSetFragment.newInstance(0, 5, false));
        onView(withId(R.id.actualSetDoneButton)).perform(click());
        onView(withId(R.id.actualSetWeightLayout)).check(matches(not(hasErrorText())));
    }

    private static Matcher<View> hasErrorText() {
        return new BaseMatcher<View>() {
            @Override
            public boolean matches(Object item) {
                return item instanceof TextInputLayout && ((TextInputLayout) item).getError() != null;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("matches has error text");
            }
        };
    }

    private static Matcher<View> hasErrorText(@StringRes int expectedErrorString) {
        return new BaseMatcher<View>() {
            @Override
            public boolean matches(Object item) {
                if (item instanceof TextInputLayout) {
                    String expectedString = InstrumentationRegistry.getTargetContext().getString(expectedErrorString);
                    return expectedString.equals(((TextInputLayout) item).getError());
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("error text matches specified string");
            }
        };
    }

    @Test
    public void testRepsCountEditText() throws Exception {
        onView(withId(R.id.actualSetRepsCountEditText)).perform(typeText("foo"));
        onView(withId(R.id.actualSetRepsCountEditText)).check(matches(withText("")));

        onView(withId(R.id.actualSetRepsCountEditText)).perform(typeText("1.1"));
        onView(withId(R.id.actualSetRepsCountEditText)).check(matches(withText("11")));
    }

    @Test
    public void testWeightEditText() throws Exception {
        onView(withId(R.id.actualSetWeightEditText)).perform(typeText("foo"));
        onView(withId(R.id.actualSetWeightEditText)).check(matches(withText("")));

        onView(withId(R.id.actualSetWeightEditText)).perform(typeText("1.2"));
        onView(withId(R.id.actualSetWeightEditText)).check(matches(withText("1.2")));
    }

    @Test
    public void instantiationWithRepsTest() throws Exception {
        activityTestRule.getActivity().replaceFragment(ActualSetFragment.newInstance(0, 5, true, 10));

        assertViewsTexts(1, "5", "10");
        assertViewsVisibility();
    }

    @Test
    public void instantiationWithoutRepsTest() throws Exception {
        assertViewsTexts(1, "5", "");
        assertViewsVisibility();
    }

    private void assertViewsVisibility() {
        onView(withId(R.id.actualSetIndex)).check(matches(isDisplayed()));
        onView(withId(R.id.actualSetProgramSetsCount)).check(matches(isDisplayed()));
        onView(withId(R.id.actualSetRepsCountEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.actualSetWeightEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.actualSetDoneButton)).check(matches(isDisplayed()));
    }

    private void assertViewsTexts(int index, String count, String repsCount) {
        String setIndex = activityTestRule.getActivity().getResources().getQuantityString(R.plurals.number_of_sets, index, index);
        onView(withId(R.id.actualSetIndex)).check(matches(withText(setIndex)));
        onView(withId(R.id.actualSetProgramSetsCount)).check(matches(withText(count)));
        onView(withId(R.id.actualSetRepsCountEditText)).check(matches(withText(repsCount)));
    }
}
