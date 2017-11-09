package ru.codingworkshop.gymm.ui.actual.exercise;

import android.arch.lifecycle.LiveData;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.v4.app.Fragment;

import org.junit.Test;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.service.TrainingForegroundService;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.TreeBuilders;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by Радик on 01.10.2017 as part of the Gymm project.
 */

public class ActualExercisesFragmentStartTrainingTest extends Base {

    @Override
    void beforeFragmentSet() {
        doAnswer(invocation -> {
            ActualTrainingTree tree = TreeBuilders.buildTreeWithoutActuals(1);
            setFakeTree(tree);
            fakeTree.setParent(Models.createActualTraining(11L, invocation.getArgument(0)));
            return new LiveData<ActualTrainingTree>() {{postValue(tree);}};
        }).when(vm).startTraining(1L);
    }

    @Override
    void setArguments(Fragment fragment) {
        final Bundle args = new Bundle();
        args.putLong(ActualExercisesFragment.EXTRA_PROGRAM_TRAINING_ID, 1L);
        fragment.setArguments(args);
    }

    @Test
    public void startTrainingTest() throws Exception {
        onView(withId(R.id.actualExercisesToolbar)).check(matches(withChild(withText("foo"))));
        onView(withText("exercise100")).check(matches(isDisplayed()));

        verify(callback).onLoadingFinished();
        verify(vm).startTraining(1L);
        verify(vm, never()).loadTraining(anyLong());
        verify(vm, atLeastOnce()).getActualTrainingTree();
    }

    @Test
    public void startTrainingServiceTest() throws Exception {
        startTrainingTest();
        assertTrue(TrainingForegroundService.isRunning(InstrumentationRegistry.getTargetContext()));
    }
}
