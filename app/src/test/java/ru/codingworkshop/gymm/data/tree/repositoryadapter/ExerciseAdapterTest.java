package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.MuscleGroupsRepository;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 17.12.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ExerciseAdapterTest {
    @Mock private ExercisesRepository exercisesRepository;
    @Mock private MuscleGroupsRepository muscleGroupsRepository;
    @InjectMocks private ExerciseAdapter adapter;

    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void getPrimaryMuscleGroup() {
        when(muscleGroupsRepository.getPrimaryMuscleGroupForExercise(100L))
                .thenReturn(Models.createMuscleGroup(200L, "bar"));

        assertEquals(200L, adapter.getPrimaryMuscleGroup(100L).getId());
    }

    @Test
    public void getParent() {
        when(exercisesRepository.getExerciseById(100L)).thenReturn(Models.createExercise(100L, "foo"));

        assertEquals(100L, adapter.getParent(100L).getId());
    }

    @Test
    public void getChildren() {
        when(muscleGroupsRepository.getSecondaryMuscleGroupsForExercise(100L)).thenReturn(Models.createMuscleGroups(201L));

        assertFalse(adapter.getChildren(100L).isEmpty());
    }

}