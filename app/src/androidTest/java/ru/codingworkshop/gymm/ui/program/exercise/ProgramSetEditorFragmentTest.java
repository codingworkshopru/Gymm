package ru.codingworkshop.gymm.ui.program.exercise;

import android.support.annotation.IdRes;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.NumberPicker;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

/**
 * Created by Радик on 27.10.2017 as part of the Gymm project.
 */

public class ProgramSetEditorFragmentTest {
    @Rule public ActivityTestRule<SimpleFragmentActivity> activityTestRule =
            new ActivityTestRule<>(SimpleFragmentActivity.class);

    @Mock private ProgramSetEditorFragment.OnSetReturnListener listener;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ProgramSetEditorFragment fragment = ProgramSetEditorFragment.newInstance();
        fragment.listener = listener;

        fragment.show(activityTestRule.getActivity().getSupportFragmentManager());
    }

    @Test
    public void repsPickerTest() throws Exception {
        checkPicker(R.id.programSetRepsPicker, "1");
        onView(withId(android.R.id.button1)).perform(click());
        verify(listener).onSetReturn(argThat(s -> s.getReps() != 0 && s.getReps() != 1));
    }

    @Test
    public void minutesPickerTest() throws Exception {
        checkPicker(R.id.programSetRestMinutesPicker, "0");
        onView(withId(android.R.id.button1)).perform(click());
        verify(listener).onSetReturn(argThat(s -> s.getSecondsForRest() != null && s.getSecondsForRest() > 60));
    }

    @Test
    public void secondsPickerTest() throws Exception {
        checkPicker(R.id.programSetRestSecondsPicker, "0");
        onView(withId(android.R.id.button1)).perform(click());
        verify(listener).onSetReturn(argThat(s -> s.getSecondsForRest() != null && s.getSecondsForRest() > 5));
    }

    @Test
    public void editProgramSet() throws Exception {
        ProgramSet set = new ProgramSet();
        set.setReps(10);
        set.setMinutes(1);
        set.setSeconds(30);

        onView(withId(android.R.id.button2)).perform(click());
        ProgramSetEditorFragment fragment = ProgramSetEditorFragment.newInstance(set);
        fragment.show(activityTestRule.getActivity().getSupportFragmentManager());

        onView(withId(R.id.programSetRepsPicker)).check(matches(withDisplayValue("10")));
        onView(withId(R.id.programSetRestMinutesPicker)).check(matches(withDisplayValue("1")));
        onView(withId(R.id.programSetRestSecondsPicker)).check(matches(withDisplayValue("30")));
    }

    private void checkPicker(@IdRes int picker, String minValue) {
        onView(withId(picker)).check(matches(withDisplayValue(minValue)));
        onView(withId(picker)).perform(swipeUp());
        onView(withId(picker)).check(matches(not(withDisplayValue(minValue))));
    }

    private Matcher<View> withDisplayValue(String value) {
        return new BoundedMatcher<View, NumberPicker>(NumberPicker.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("with display value ")
                        .appendValue(value);
            }

            @Override
            protected boolean matchesSafely(NumberPicker item) {
                String[] displayedValues = item.getDisplayedValues();
                if (displayedValues != null) {
                    return displayedValues[item.getValue()].equals(value);
                } else {
                    return item.getValue() == Integer.valueOf(value);
                }
            }
        };
    }
}