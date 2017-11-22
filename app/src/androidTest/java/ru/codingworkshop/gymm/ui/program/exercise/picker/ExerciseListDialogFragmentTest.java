package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.IdRes;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.RecyclerViewItemMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 21.11.2017.
 */

public class ExerciseListDialogFragmentTest {
    @Rule public ActivityTestRule<SimpleFragmentActivity> activityTestRule =
            new ActivityTestRule<>(SimpleFragmentActivity.class);

    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ViewModelProvider.Factory viewModelFactory;
    @Mock private ExerciseListDialogFragmentViewModel vm;
    @Mock private ExerciseListDialogFragment.OnExerciseClickListener listener;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        RecyclerViewItemMatcher.setRecyclerViewId(R.id.fragment_exercise_picker_exercises);

        when(vm.load(200L)).thenReturn(Models.createLiveExercises(30, 200L));
        when(viewModelFactory.create(any())).thenReturn(vm);

        ExerciseListDialogFragment exerciseListDialogFragment = ExerciseListDialogFragment.newInstance(200L, "muscle group");
        exerciseListDialogFragment.viewModelFactory = viewModelFactory;
        exerciseListDialogFragment.exerciseClickListener = listener;
        exerciseListDialogFragment.show(activityTestRule.getActivity().getSupportFragmentManager(), "tag");
    }

    @Test
    public void showTitleTest() throws Exception {
        onView(both(withParent(withId(R.id.fragment_exercise_picker_toolbar))).and(withText("muscle group"))).check(matches(isDisplayed()));
    }

    @Test
    public void showExercisesTest() throws Exception {
        onView(elementAt(R.id.exerciseListItemTitle, 0)).check(matches(withText("exercise100")));
        verify(vm).load(200L);
    }

    @Test
    public void exerciseClickTest() throws Exception {
        onView(withId(R.id.fragment_exercise_picker_exercises)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        verify(listener).onExerciseClick(any());
    }

    private Matcher<View> elementAt(@IdRes int id, int position) {
        return RecyclerViewItemMatcher.itemAtPosition(id, position);
    }
}