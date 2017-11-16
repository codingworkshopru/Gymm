package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ru.codingworkshop.gymm.data.tree.node.ExerciseNode;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ExerciseAdapter;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ExerciseLoaderTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Mock private ExerciseAdapter adapter;

    @Test
    public void load() throws Exception {
        when(adapter.getParent(100L)).thenReturn(Models.createLiveExercise(100L, "foo"));
        when(adapter.getChildren(100L)).thenReturn(Models.createLiveMuscleGroups(200L));
        when(adapter.getPrimaryMuscleGroup(100L)).thenReturn(Models.createLiveMuscleGroup(201L, "bar"));

        ExerciseNode node = new ExerciseNode();
        ExerciseLoader loader = new ExerciseLoader(adapter);

        LiveTest.verifyLiveData(loader.loadById(node, 100L), n -> {

            assertEquals(100L, n.getParent().getId());
            assertEquals(200L, n.getChildren().get(0).getId());
            assertEquals(201L, n.getPrimaryMuscleGroup().getId());
            return true;
        });
    }
}