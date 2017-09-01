package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;

import ru.codingworkshop.gymm.data.tree.loader.datasource.ProgramExerciseDataSource;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */
public class ProgramExerciseLoaderTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void load() throws Exception {
        ProgramExerciseDataSource dataSource = mock(ProgramExerciseDataSource.class);
        when(dataSource.getParent()).thenReturn(Models.createLiveProgramExercise(2L, 1L, false));
        when(dataSource.getChildren()).thenReturn(Models.createLiveProgramSets(2L, 1));
        when(dataSource.getExercise()).thenReturn(Models.createLiveExercise(100L, "foo"));

        ProgramExerciseNode node = new ImmutableProgramExerciseNode();
        ProgramExerciseLoader loader = new ProgramExerciseLoader(node, dataSource);

        LiveTest.verifyLiveData(loader.load(), b -> b);

        assertEquals(2L, node.getParent().getId());
        assertEquals(1, node.getChildren().size());
        assertEquals(100L, node.getExercise().getId());
    }

}