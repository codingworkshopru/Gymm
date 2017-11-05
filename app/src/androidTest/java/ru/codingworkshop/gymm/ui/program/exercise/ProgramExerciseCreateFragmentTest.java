package ru.codingworkshop.gymm.ui.program.exercise;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;
import ru.codingworkshop.gymm.util.Models;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasBackground;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 04.11.2017.
 */

public class ProgramExerciseCreateFragmentTest {
    @Rule public ActivityTestRule<SimpleFragmentActivity> activityTestRule =
            new ActivityTestRule<>(SimpleFragmentActivity.class);

    @Mock private ViewModelProvider.Factory viewModelFactory;
    @Mock private ProgramExerciseViewModel vm;
    private ProgramExerciseFragment fragment;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(viewModelFactory.create(any())).thenReturn(vm);
        when(vm.create()).thenAnswer(invocation -> {
            ProgramExerciseNode node = new MutableProgramExerciseNode();
            node.setParent(Models.createLiveProgramExercise(0L, 1L, true).getValue());
            when(vm.getProgramExerciseNode()).thenReturn(node);
            return new LiveData<Boolean>() {{postValue(true);}};
        });

        fragment = ProgramExerciseFragment.newInstanceForNew(1L);
        fragment.viewModelFactory = viewModelFactory;
        activityTestRule.getActivity().setFragment(fragment);
    }

    @Test
    public void creationTest() throws Exception {
        onView(withId(R.id.programExerciseName)).check(matches(withText(R.string.program_exercise_activity_exercise_empty_text)));
        onView(withId(R.id.programExerciseBackground)).check(matches(isDisplayed()));
        verify(vm).setProgramTrainingId(1L);
        verify(vm).create();
    }

    @Test
    public void saveWithoutExerciseTest() throws Exception {
        onView(withId(R.id.actionSaveExercise)).perform(click());
        onView(withText(R.string.program_exercise_activity_exercise_empty_text)).check(matches(isDisplayed()));
        verify(vm, never()).save();
    }

    @Test
    public void saveWithoutSetsTest() throws Exception {

    }
}
