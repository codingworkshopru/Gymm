package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;

import ru.codingworkshop.gymm.data.tree.loader.datasource.ExerciseDataSource;
import ru.codingworkshop.gymm.data.tree.node.ExerciseNode;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */

public class ExerciseLoaderTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void load() throws Exception {
        ExerciseDataSource dataSource = mock(ExerciseDataSource.class);
        when(dataSource.getParent()).thenReturn(Models.createLiveExercise(100L, "foo"));
        when(dataSource.getChildren()).thenReturn(Models.createLiveMuscleGroups(200L));
        when(dataSource.getPrimaryMuscleGroup()).thenReturn(Models.createLiveMuscleGroup(201L, "bar"));

        ExerciseNode node = new ExerciseNode();
        ExerciseLoader loader = new ExerciseLoader(node, dataSource);

        LiveTest.verifyLiveData(loader.loadIt(), n -> {

            assertEquals(100L, n.getParent().getId());
            assertEquals(200L, n.getChildren().get(0).getId());
            assertEquals(201L, n.getPrimaryMuscleGroup().getId());
            return true;
        });
    }
}