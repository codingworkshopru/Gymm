package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Objects;

import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramTrainingAdapter;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 18.11.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramDraftingTrainingTreeLoaderTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ProgramTrainingAdapter adapter;
    private ProgramDraftingTrainingTreeLoader loader;
    private ProgramTrainingTree tree;

    @Before
    public void setUp() throws Exception {
        loader = new ProgramDraftingTrainingTreeLoader(adapter);
        tree = new ImmutableProgramTrainingTree();
    }

    @Test
    public void loadWithoutDrafting() throws Exception {
        when(adapter.getDrafting()).thenReturn(LiveDataUtil.getAbsent());
        LiveTest.verifyLiveData(loader.load(tree), Objects::isNull);
        verify(adapter).getDrafting();
    }

    @Test
    public void loadWithDrafting() throws Exception {
        LiveData<ProgramTraining> liveProgramTraining = Models.createLiveProgramTraining(1L, "foo", true);

        when(adapter.getParent(1L)).thenReturn(liveProgramTraining);
        when(adapter.getChildren(1L)).thenReturn(Models.createLiveProgramExercises(1));
        when(adapter.getGrandchildren(1L)).thenReturn(Models.createLiveProgramSets(2L, 1));
        when(adapter.getExercises(1L)).thenReturn(Models.createLiveExercises("foobar"));
        when(adapter.getDrafting()).thenReturn(liveProgramTraining);

        LiveTest.verifyLiveData(loader.load(tree), Objects::nonNull);

        verify(adapter).getParent(1L);
        verify(adapter).getChildren(1L);
        verify(adapter).getGrandchildren(1L);
        verify(adapter).getExercises(1L);
        verify(adapter).getDrafting();
    }
}