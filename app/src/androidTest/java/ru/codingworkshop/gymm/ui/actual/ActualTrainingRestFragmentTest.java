package ru.codingworkshop.gymm.ui.actual;

import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Радик on 23.09.2017 as part of the Gymm project.
 */
public class ActualTrainingRestFragmentTest {
    @Rule
    public ActivityTestRule<SimpleFragmentActivity> activityActivityTestRule =
            new ActivityTestRule<>(SimpleFragmentActivity.class);

    @Before
    public void setUp() throws Exception {
        activityActivityTestRule.getActivity().setFragment(ActualTrainingRestFragment.newInstance(30));
    }

    @Test
    public void initializationTest() throws Exception {
        onView(withId(R.id.restTimeLeft)).check(matches(withText("0:30")));
        Thread.sleep(1100);
    }
}
