package ru.codingworkshop.gymm.ui.program.exercise;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.IdRes;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.NumberPicker;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;
import ru.codingworkshop.gymm.ui.program.ProgramTrainingViewModel;
import ru.codingworkshop.gymm.util.Models;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 27.10.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramSetEditorFragmentTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Rule public ActivityTestRule<SimpleFragmentActivity> activityTestRule =
            new ActivityTestRule<>(SimpleFragmentActivity.class);

    @Mock private ViewModelProvider.Factory viewModelFactory;
    @Mock private ProgramTrainingViewModel vm;
    @Mock private ProgramSetEditorFragment.OnProgramSetEditedListener listener;
    @InjectMocks private ProgramSetEditorFragment fragment;
    private ProgramSet set;

    @Before
    public void setUp() throws Exception {
        when(viewModelFactory.create(any())).thenReturn(vm);
        LiveData<ProgramSet> liveProgramSet = Models.createLiveProgramSet(0L, 2L, 1);
        set = liveProgramSet.getValue();
        when(vm.getProgramSet()).thenReturn(liveProgramSet);
        fragment.show(activityTestRule.getActivity().getSupportFragmentManager());
    }

    @Test
    public void repsPickerTest() {
        checkPicker(R.id.programSetRepsPicker, "1");
        onView(withId(android.R.id.button1)).perform(click());
        assertNotEquals(1, set.getReps());
    }

    @Test
    public void minutesPickerTest() {
        checkPicker(R.id.programSetRestMinutesPicker, "0");
        onView(withId(android.R.id.button1)).perform(click());
        assertNotNull(set.getSecondsForRest());
        assertThat(set.getSecondsForRest(), greaterThan(60));
    }

    @Test
    public void secondsPickerTest() {
        checkPicker(R.id.programSetRestSecondsPicker, "0");
        onView(withId(android.R.id.button1)).perform(click());
        assertNotNull(set.getSecondsForRest());
        assertThat(set.getSecondsForRest(), greaterThan(5));
    }

    @Test
    public void saveSetTest() {
        onView(withId(android.R.id.button1)).perform(click());

        InOrder ord = inOrder(listener, vm);

        ord.verify(listener).onProgramSetEdited(any());
        ord.verify(vm).saveProgramSet();
    }

    @Test
    public void editProgramSet() {
        ProgramSet set = new ProgramSet();
        set.setReps(10);
        set.setMinutes(1);
        set.setSeconds(30);

        MutableLiveData<ProgramSet> mld = (MutableLiveData<ProgramSet>) vm.getProgramSet();
        mld.postValue(set);

        onView(withId(R.id.programSetRepsPicker)).check(matches(withDisplayValue("10")));
        onView(withId(R.id.programSetRestMinutesPicker)).check(matches(withDisplayValue("1")));
        onView(withId(R.id.programSetRestSecondsPicker)).check(matches(withDisplayValue("30")));
    }

    @Test
    public void onCancelTest() {
        onView(withId(android.R.id.button2)).perform(click());
        verify(vm).setProgramSet(null);
    }

    @Test
    public void onBackPressTest() {
        Espresso.pressBack();
        verify(vm).setProgramSet(null);
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