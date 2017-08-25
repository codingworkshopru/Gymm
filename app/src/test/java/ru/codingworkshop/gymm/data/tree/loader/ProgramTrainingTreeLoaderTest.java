package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;

import ru.codingworkshop.gymm.data.tree.loader.datasource.ProgramTrainingDataSource;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */
public class ProgramTrainingTreeLoaderTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void load() throws Exception {
        ProgramTrainingDataSource dataSource = mock(ProgramTrainingDataSource.class);
        when(dataSource.getParent()).thenReturn(Models.createLiveProgramTraining(1L, "foo", false));
        when(dataSource.getChildren()).thenReturn(Models.createLiveProgramExercises(1));
        when(dataSource.getGrandchildren()).thenReturn(Models.createLiveProgramSets(3));
        when(dataSource.getExercises()).thenReturn(Models.createLiveExercises("bar", "baz"));

        ProgramTrainingTree tree = new ImmutableProgramTrainingTree();

        ProgramTrainingTreeLoader loader = new ProgramTrainingTreeLoader(tree, dataSource);

        LiveTest.verifyLiveData(loader.load(), b -> b);

        assertEquals(1L, tree.getParent().getId());
        assertEquals(2L, tree.getChildren().get(0).getId());
        assertEquals(3L, tree.getChildren().get(0).getChildren().get(0).getId());
        assertEquals(100L, tree.getChildren().get(0).getExercise().getId());
    }

}