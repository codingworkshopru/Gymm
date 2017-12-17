package ru.codingworkshop.gymm.data.tree.loader2;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Maybe;
import io.reactivex.Single;
import ru.codingworkshop.gymm.data.tree.node.ExerciseNode;
import ru.codingworkshop.gymm.data.tree.repositoryadapter2.ExerciseAdapter;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.assertEquals;
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
        when(exerciseAdapter.getParent(100L)).thenReturn(Single.just(Models.createExercise(100L, "foo")));
        when(exerciseAdapter.getPrimaryMuscleGroup(100L)).thenReturn(Single.just(Models.createMuscleGroup(200L, "bar")));

        node = new ExerciseNode();
    }

    @Test
    public void loadById() throws Exception {
        when(exerciseAdapter.getChildren(100L)).thenReturn(Maybe.just(Models.createMuscleGroups(1L)));
        node = LiveTest.getValue(loader.loadById(node, 100L));
        assertEquals(100L, node.getParent().getId());
        assertEquals(200L, node.getPrimaryMuscleGroup().getId());
        assertTrue(node.hasChildren());

        verify(exerciseAdapter).getParent(100L);
        verify(exerciseAdapter).getChildren(100L);
        verify(exerciseAdapter).getPrimaryMuscleGroup(100L);
    }

    @Test
    public void loadByIdWithoutSecondaryMuscleGroups() throws Exception {
        when(exerciseAdapter.getChildren(100L)).thenReturn(Maybe.empty());

        node = LiveTest.getValue(loader.loadById(node, 100L));
        assertEquals(100L, node.getParent().getId());
        assertEquals(200L, node.getPrimaryMuscleGroup().getId());
        assertFalse(node.hasChildren());

        verify(exerciseAdapter).getParent(100L);
        verify(exerciseAdapter).getChildren(100L);
        verify(exerciseAdapter).getPrimaryMuscleGroup(100L);
    }
}
