package ru.codingworkshop.gymm.ui.program.exercise;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;
import ru.codingworkshop.gymm.ui.program.ProgramTrainingViewModel;
import ru.codingworkshop.gymm.util.Models;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasBackground;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 04.11.2017.
 */

public class ProgramExerciseCreateFragmentTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Rule public ActivityTestRule<SimpleFragmentActivity> activityTestRule =
            new ActivityTestRule<>(SimpleFragmentActivity.class);

    @Mock private ViewModelProvider.Factory viewModelFactory;
    @Mock private ProgramTrainingViewModel vm;
    @InjectMocks private ProgramExerciseFragment fragment;
    private ProgramExerciseNode node;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        node = new MutableProgramExerciseNode();

        when(viewModelFactory.create(any())).thenReturn(vm);
        when(vm.getProgramExercise()).thenAnswer(invocation -> {
            node.setParent(Models.createLiveProgramExercise(0L, 1L).getValue());
            return new LiveData<ProgramExerciseNode>() {{postValue(node);}};
        });
        activityTestRule.getActivity().setFragment(fragment);
    }

    @Test
    public void creationTest() throws Exception {
        onView(withId(R.id.programExerciseName)).check(matches(withText(R.string.program_exercise_activity_exercise_empty_text)));
        onView(withId(R.id.programExerciseBackground)).check(matches(isDisplayed()));
    }

    @Test
    public void saveWithoutExerciseTest() throws Exception {
        onView(withId(R.id.actionSaveExercise)).perform(click());
        onView(withText(R.string.program_exercise_activity_exercise_empty_text)).check(matches(isDisplayed()));
    }

    @Test
    public void saveWithoutSetsTest() throws Exception {
        node.setExercise(Models.createExercise(100L, "foo"));
        onView(withId(R.id.actionSaveExercise)).perform(click());
        onView(withText(R.string.program_exercise_activity_empty_list_dialog_message)).check(matches(isDisplayed()));
    }

    @Test
    public void backgroundImageTest() throws Exception {
        ProgramSet set = Models.createProgramSet(0L, 2L, 1);
        set.setSortOrder(-1);
        MutableLiveData<ProgramSet> liveProgramSet = new MutableLiveData<>();
        liveProgramSet.setValue(set);
        when(vm.getProgramSet()).thenReturn(liveProgramSet);
        doAnswer(invocation -> {
            node.addChild(set);
            liveProgramSet.setValue(null);
            return null;
        }).when(vm).saveProgramSet();

        onView(withId(R.id.programExerciseBackground)).check(matches(isDisplayed()));
        onView(withId(R.id.programExerciseAddSetButton)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.programExerciseBackground)).check(matches(not(isDisplayed())));
    }
}
