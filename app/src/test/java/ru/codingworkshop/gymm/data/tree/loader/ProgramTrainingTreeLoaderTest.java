package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramTrainingAdapter;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramTrainingTreeLoaderTest {
    @Mock private ProgramTrainingAdapter adapter;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void load() {
        when(adapter.getParent(1L)).thenReturn(Models.createProgramTraining(1L, "foo"));
        when(adapter.getChildren(1L)).thenReturn(Models.createProgramExercises(1));
        when(adapter.getGrandchildren(1L)).thenReturn(Models.createProgramSets(2L, 3));
        when(adapter.getExercises(1L)).thenReturn(Models.createExercises("bar", "baz"));

        ProgramTrainingTree tree = new ImmutableProgramTrainingTree();
        ProgramTrainingTreeLoader loader = new ProgramTrainingTreeLoader(adapter);

        loader.loadById(tree, 1L).test().assertValue(loadedTree -> {
            assertEquals(1L, loadedTree.getParent().getId());
            assertEquals(2L, loadedTree.getChildren().get(0).getId());
            assertEquals(3L, loadedTree.getChildren().get(0).getChildren().get(0).getId());
            assertEquals(100L, loadedTree.getChildren().get(0).getExercise().getId());

            return true;
        });
    }
}