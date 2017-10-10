package ru.codingworkshop.gymm.ui.actual.set;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;
import ru.codingworkshop.gymm.util.Models;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static ru.codingworkshop.gymm.ui.Matchers.hasErrorText;

/**
 * Created by Радик on 12.09.2017 as part of the Gymm project.
 */

@RunWith(AndroidJUnit4.class)
public class ActualSetFragmentTest {
    @Rule
    public ActivityTestRule<SimpleFragmentActivity> activityTestRule =
            new ActivityTestRule<>(SimpleFragmentActivity.class);

    @Mock private ActualSetFragment.OnActualSetSaveListener listener;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Bundle args = new ActualSetFragment.ArgumentsBuilder()
                .setActualSet(Models.createActualSet(13L, 12L, 0))
                .setProgramSet(Models.createProgramSet(3L, 2L, 10))
                .setSetsCount(5)
                .setWithWeight(true)
                .build();

        final ActualSetFragment fragment = ActualSetFragment.newInstance(args);
        fragment.listener = listener;
        activityTestRule.getActivity().setFragment(fragment);
    }

    @Test
    public void incorrectRepsInput() throws Exception {
        testOnEmptyFieldError(R.id.actualSetRepsCountEditText, R.id.actualSetRepsCountLayout,
                R.string.actual_training_activity_stepper_item_reps_error);
    }

    @Test
    public void incorrectWeightInput() throws Exception {
        onView(withId(R.id.actualSetRepsCountEditText)).perform(clearText());
        testOnEmptyFieldError(R.id.actualSetWeightEditText, R.id.actualSetWeightLayout,
                R.string.actual_training_activity_stepper_item_weight_error);
    }

    private void testOnEmptyFieldError(@IdRes int editText, @IdRes int layout, @StringRes int errorText) {
        Espresso.closeSoftKeyboard();

        onView(withId(editText)).perform(clearText());

        onView(withId(R.id.actualSetDoneButton)).perform(click());
        onView(withId(layout)).check(matches(hasErrorText(errorText)));

        onView(withId(editText)).perform(typeText("1"));

        Espresso.closeSoftKeyboard();

        onView(withId(R.id.actualSetDoneButton)).perform(click());
        onView(withId(layout)).check(matches(not(hasErrorText())));

        verifyZeroInteractions(listener);
    }

    @Test
    public void passActualSetToActivityTest() throws Exception {
        onView(withId(R.id.actualSetWeightEditText)).perform(typeText("1.125"));
        onView(withId(R.id.actualSetDoneButton)).perform(click());
        verify(listener).onActualSetSave(eq(0), argThat(as -> as.unwrap().getReps() == 10 && as.unwrap().getWeight() == 1.125));
    }

    @Test
    public void emptyWeightAllowedInput() throws Exception {
        Bundle args = new ActualSetFragment.ArgumentsBuilder()
                .setWithWeight(false)
                .setSetsCount(5)
                .setSetIndex(6)
                .setActualSet(Models.createActualSet(13L, 12L, 0))
                .build();

        final ActualSetFragment fragment = ActualSetFragment.newInstance(args);
        fragment.listener = listener;
        activityTestRule.getActivity().replaceFragment(fragment);
        onView(withId(R.id.actualSetDoneButton)).perform(click());
        onView(withId(R.id.actualSetWeightLayout)).check(matches(not(hasErrorText())));
    }

    @Test
    public void testRepsCountEditText() throws Exception {
        onView(withId(R.id.actualSetRepsCountEditText)).perform(clearText());
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
        assertViewsTexts(1, "5", "10");
        assertViewsVisibility();
    }

    @Test
    public void instantiationWithoutRepsTest() throws Exception {
        Bundle args = new ActualSetFragment.ArgumentsBuilder()
                .setWithWeight(false)
                .setSetsCount(5)
                .setSetIndex(5)
                .setActualSet(Models.createActualSet(13L, 12L, 0))
                .build();

        final ActualSetFragment fragment = ActualSetFragment.newInstance(args);
        fragment.listener = listener;
        activityTestRule.getActivity().replaceFragment(fragment);

        assertViewsTexts(6, "5", "");
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
