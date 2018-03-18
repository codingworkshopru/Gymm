package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.app.Dialog;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.ViewModelProvider;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.Exercise;
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
import static org.hamcrest.Matchers.both;
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
    @Mock private ExercisePickerViewModel vm;
    private ExerciseListDialogFragment exerciseListDialogFragment;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        RecyclerViewItemMatcher.setRecyclerViewId(R.id.fragment_exercise_picker_exercises);

        when(vm.getExercisesForMuscleGroup()).thenReturn(Models.createLiveExercises(30, 200L));
        when(viewModelFactory.create(any())).thenReturn(vm);

        exerciseListDialogFragment = ExerciseListDialogFragment.newInstance(200L, "muscle group");
        exerciseListDialogFragment.viewModelFactory = viewModelFactory;
        exerciseListDialogFragment.show(activityTestRule.getActivity().getSupportFragmentManager(), "tag");
    }

    @Test
    public void showTitleTest() {
        onView(both(withParent(withId(R.id.fragment_exercise_picker_toolbar))).and(withText("muscle group"))).check(matches(isDisplayed()));
    }

    @Test
    public void showExercisesTest() {
        onView(elementAt(R.id.exerciseListItemTitle, 0)).check(matches(withText("exercise100")));
        verify(vm).getExercisesForMuscleGroup();
    }

    @Test
    public void exerciseClickTest() {
        onView(withId(R.id.fragment_exercise_picker_exercises)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        verify(vm).setExercise(any(Exercise.class));
    }

    @Test
    public void clearMuscleGroupOnCancel() {
        exerciseListDialogFragment.getFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
                super.onFragmentViewCreated(fm, f, v, savedInstanceState);
                Dialog dialog = exerciseListDialogFragment.getDialog();
                dialog.setOnShowListener(DialogInterface::cancel);
                dialog.setOnCancelListener(dialog1 -> verify(vm).clearMuscleGroup());
            }
        }, false);
    }

    private Matcher<View> elementAt(@IdRes int id, int position) {
        return RecyclerViewItemMatcher.itemAtPosition(id, position);
    }
}