package ru.codingworkshop.gymm.ui.actual;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Радик on 12.09.2017 as part of the Gymm project.
 */

@RunWith(JUnitParamsRunner.class)
public class ActualSetFragmentTest {
    @Rule
    public ActivityTestRule<SimpleFragmentActivity> activityTestRule =
            new ActivityTestRule<>(SimpleFragmentActivity.class);

    @Test
    public void instantiationWithRepsTest() throws Exception {
        activityTestRule.getActivity().setFragment(ActualSetFragment.newInstance(0, 5, 10));
        assertViewsTexts("1", "5", "10");
        assertViewsVisibility();
    }

    @Test
    public void instantiationWithoutRepsTest() throws Exception {
        activityTestRule.getActivity().setFragment(ActualSetFragment.newInstance(5, 5));
        assertViewsTexts("6", "5", "");
        assertViewsVisibility();
    }

    private void assertViewsVisibility() {
        onView(withId(R.id.actualSetIndex)).check(matches(isDisplayed()));
        onView(withId(R.id.actualSetProgramSetsCount)).check(matches(isDisplayed()));
        onView(withId(R.id.actualSetRepsCountEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.actualSetWeightEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.actualSetDoneButton)).check(matches(isDisplayed()));
    }

    private void assertViewsTexts(String index, String count, String repsCount) {
        onView(withId(R.id.actualSetIndex)).check(matches(withText(index)));
        onView(withId(R.id.actualSetProgramSetsCount)).check(matches(withText(count)));
        onView(withId(R.id.actualSetRepsCountEditText)).check(matches(withText(repsCount)));
    }


}
