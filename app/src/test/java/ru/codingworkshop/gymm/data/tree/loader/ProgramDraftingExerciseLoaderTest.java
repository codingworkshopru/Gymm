package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Objects;

import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramExerciseAdapter;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 17.11.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramDraftingExerciseLoaderTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ProgramExerciseAdapter adapter;
    @Mock private ProgramExerciseNode node;
    private ProgramDraftingExerciseLoader loader;

    @Before
    public void setUp() throws Exception {
        when(adapter.getParent(2L)).thenReturn(Models.createLiveProgramExercise(2L, 1L, true));
        when(adapter.getChildren(2L)).thenReturn(Models.createLiveProgramSets(2L, 1));
        when(adapter.getExercise(2L)).thenReturn(Models.createLiveExercise(100L, "foo"));
        loader = new ProgramDraftingExerciseLoader(adapter);
    }

    @Test
    public void loadById() throws Exception {
        when(adapter.getDrafting(1L)).thenReturn(LiveDataUtil.getAbsent());
        LiveTest.verifyLiveData(loader.loadById(node, 1L), Objects::isNull);

        node = new ImmutableProgramExerciseNode();
        when(adapter.getDrafting(1L)).thenReturn(Models.createLiveProgramExercise(2L, 1L, true));
        LiveTest.verifyLiveData(loader.loadById(node, 1L), loaded -> {
            assertEquals(2L, loaded.getParent().getId());
            assertEquals(1, loaded.getChildrenCount());
            return true;
        });

        verify(adapter, times(2)).getDrafting(1L);
    }

}