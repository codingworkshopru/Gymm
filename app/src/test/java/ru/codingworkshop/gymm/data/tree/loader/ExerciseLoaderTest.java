package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.tree.node.ExerciseNode;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ExerciseAdapter;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 17.12.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ExerciseLoaderTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ExerciseAdapter exerciseAdapter;
    @InjectMocks private ExerciseLoader loader;
    private ExerciseNode node;

    @Before
    public void setUp() throws Exception {
        when(exerciseAdapter.getParent(100L)).thenReturn(Flowable.just(Models.createExercise(100L, "foo")));
        when(exerciseAdapter.getPrimaryMuscleGroup(100L)).thenReturn(Flowable.just(Models.createMuscleGroup(200L, "bar")));

        node = new ExerciseNode();
    }

    @Test
    public void loadById() throws Exception {
        when(exerciseAdapter.getChildren(100L)).thenReturn(Flowable.just(Models.createMuscleGroups(1L)));
        loader.loadById(node, 100L)
                .test()
                .assertValue(testNode -> 100L == testNode.getParent().getId() && 200L == node.getPrimaryMuscleGroup().getId());
        assertTrue(node.hasChildren());

        verify(exerciseAdapter).getParent(100L);
        verify(exerciseAdapter).getChildren(100L);
        verify(exerciseAdapter).getPrimaryMuscleGroup(100L);
    }

    @Test
    public void loadByIdWithoutSecondaryMuscleGroups() throws Exception {
        when(exerciseAdapter.getChildren(100L)).thenReturn(Flowable.just(new ArrayList<>()));

        loader.loadById(node, 100L)
                .test()
                .assertValue(testNode -> 100L == testNode.getParent().getId() && 200L == node.getPrimaryMuscleGroup().getId());
        assertFalse(node.hasChildren());

        verify(exerciseAdapter).getParent(100L);
        verify(exerciseAdapter).getChildren(100L);
        verify(exerciseAdapter).getPrimaryMuscleGroup(100L);
    }
}
