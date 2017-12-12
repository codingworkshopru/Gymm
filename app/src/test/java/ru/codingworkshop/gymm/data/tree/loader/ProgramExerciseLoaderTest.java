package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;

import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramExerciseAdapter;
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
        ProgramExerciseAdapter dataSource = mock(ProgramExerciseAdapter.class);
        when(dataSource.getParent(2L)).thenReturn(Models.createLiveProgramExercise(2L, 1L));
        when(dataSource.getChildren(2L)).thenReturn(Models.createLiveProgramSets(2L, 1));
        when(dataSource.getExercise(2L)).thenReturn(Models.createLiveExercise(100L, "foo"));

        ProgramExerciseNode node = new ImmutableProgramExerciseNode();
        ProgramExerciseLoader loader = new ProgramExerciseLoader(dataSource);

        LiveTest.verifyLiveData(loader.loadById(node, 2L), loadedNode -> {
            assertEquals(2L, loadedNode.getParent().getId());
            assertEquals(1, loadedNode.getChildren().size());
            assertEquals(100L, loadedNode.getExercise().getId());

            return true;
        });
    }
}