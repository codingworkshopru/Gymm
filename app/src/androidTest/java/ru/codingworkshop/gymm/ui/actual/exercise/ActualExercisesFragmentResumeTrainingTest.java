package ru.codingworkshop.gymm.ui.actual.exercise;

import android.arch.lifecycle.LiveData;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.junit.Test;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static ru.codingworkshop.gymm.ui.actual.TreeBuilders.buildHalfPopulatedTree;

/**
 * Created by Радик on 01.10.2017 as part of the Gymm project.
 */

public class ActualExercisesFragmentResumeTrainingTest extends Base {
    @Override
    void beforeFragmentSet() {
        doAnswer(invocation -> {
            setFakeTree(buildHalfPopulatedTree(2));
            return new LiveData<Boolean>() {{postValue(true);}};
        }).when(vm).loadTraining(11L);
    }

    @Override
    void setArguments(Fragment fragment) {
        final Bundle args = new Bundle();
        args.putLong(ActualExercisesFragment.EXTRA_ACTUAL_TRAINING_ID, 11L);
        fragment.setArguments(args);
    }

    @Test
    public void resumeTrainingTest() throws Exception {
        selectStepAtPosition(0);

        checkRepsCount(3);
        checkWeight(3.5);

        verify(callback).onLoadingFinished();
        verify(vm).loadTraining(11L);
        verify(vm, never()).startTraining(anyLong());
        verify(vm, atLeastOnce()).getActualTrainingTree();
    }
}
