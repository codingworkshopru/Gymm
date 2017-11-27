package ru.codingworkshop.gymm.ui.program.training;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;
import ru.codingworkshop.gymm.util.Models;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 11.10.2017 as part of the Gymm project.
 */

public class ProgramTrainingCreateFragmentTest {
    @Rule public ActivityTestRule<SimpleFragmentActivity> activityTestRule =
            new ActivityTestRule<>(SimpleFragmentActivity.class);

    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ViewModelProvider.Factory viewModelFactory;
    @Mock private ProgramTrainingViewModel vm;
    private ProgramTrainingFragment fragment;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(viewModelFactory.create(any())).thenReturn(vm);
        ProgramTrainingTree tree = new MutableProgramTrainingTree();
        tree.setParent(Models.createLiveProgramTraining(0L, null, true).getValue());
        when(vm.getProgramTrainingTree()).thenReturn(tree);
        LiveData<ProgramTrainingTree> liveTree = LiveDataUtil.getLive(tree);

        when(vm.create()).thenReturn(liveTree);
        when(vm.getLiveTree()).thenReturn(liveTree);

        fragment = ProgramTrainingFragment.newInstance();
        fragment.viewModelFactory = viewModelFactory;
        activityTestRule.getActivity().setFragment(fragment);
    }

    @Test
    public void creationTest() throws Exception {
        onView(withId(R.id.programTrainingName)).check(matches(withText("")));
        onView(withId(R.id.programTrainingBackground)).check(matches(isDisplayed()));
        verify(vm).create();
    }

    @Test
    public void saveWithoutExercisesTest() throws Exception {
        onView(withId(R.id.programTrainingName)).perform(typeText("foo"));
        onView(withId(R.id.actionSaveTraining)).perform(click());
        onView(withText(R.string.program_training_activity_empty_list_dialog_message)).check(matches(isDisplayed()));
        verify(vm, never()).save();
        verify(vm, never()).deleteIfDrafting();
    }
}
