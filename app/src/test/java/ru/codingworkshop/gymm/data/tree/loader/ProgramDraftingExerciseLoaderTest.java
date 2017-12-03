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
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramExerciseAdapter;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 17.11.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramDraftingExerciseLoaderTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ProgramExerciseAdapter adapter;
    private ProgramExerciseNode node;
    private ProgramDraftingExerciseLoader loader;

    @Before
    public void setUp() throws Exception {
        loader = new ProgramDraftingExerciseLoader(adapter);
        node = new ImmutableProgramExerciseNode();
    }

    @Test
    public void loadByIdWithoutDrafting() throws Exception {
        when(adapter.getDrafting(1L)).thenReturn(LiveDataUtil.getAbsent());
        LiveTest.verifyLiveData(loader.loadById(node, 1L), Objects::isNull);
        verify(adapter).getDrafting(1L);
    }

    @Test
    public void loadByIdWithDrafting() throws Exception {
        when(adapter.getParent(2L)).thenReturn(Models.createLiveProgramExercise(2L, 1L, true));
        when(adapter.getChildren(2L)).thenReturn(Models.createLiveProgramSets(2L, 1));
        when(adapter.getExercise(2L)).thenReturn(Models.createLiveExercise(100L, "foo"));

        when(adapter.getDrafting(1L)).thenReturn(Models.createLiveProgramExercise(2L, 1L, true));
        LiveTest.verifyLiveData(loader.loadById(node, 1L), Objects::nonNull);

        verify(adapter).getParent(2L);
        verify(adapter).getChildren(2L);
        verify(adapter).getExercise(2L);
        verify(adapter).getDrafting(1L);
    }
}