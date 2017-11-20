package ru.codingworkshop.gymm.ui.info.exercise;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ru.codingworkshop.gymm.data.tree.loader.ExerciseLoader;
import ru.codingworkshop.gymm.data.tree.node.ExerciseNode;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 20.11.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ExerciseInfoFragmentViewModelTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ExerciseLoader loader;
    private ExerciseInfoFragmentViewModel vm;

    @Before
    public void setUp() throws Exception {
        when(loader.loadById(any(), eq(100L))).thenAnswer(invocation -> {
            ExerciseNode node = invocation.getArgument(0);
            return LiveDataUtil.getLive(node);
        });
        vm = new ExerciseInfoFragmentViewModel(loader);
    }

    @Test
    public void load() throws Exception {
        LiveData<ExerciseNode> node = vm.load(100L);
        LiveData<ExerciseNode> sameNode = vm.load(100L);

        assertSame(node.getValue(), sameNode.getValue());
        verify(loader).loadById(any(), anyLong());
    }
}