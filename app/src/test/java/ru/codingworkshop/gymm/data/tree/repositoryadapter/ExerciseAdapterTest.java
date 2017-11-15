package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.MuscleGroupsRepository;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 11.11.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ExerciseAdapterTest {
    @Mock private ExercisesRepository exercisesRepository;
    @Mock private MuscleGroupsRepository muscleGroupsRepository;
    @InjectMocks private ExerciseAdapter adapter;

    @Test
    public void getPrimaryMuscleGroup() throws Exception {
        adapter.getPrimaryMuscleGroup(100L);
        verify(muscleGroupsRepository).getPrimaryMuscleGroupForExercise(100L);
    }

    @Test
    public void getParent() throws Exception {
        adapter.getParent(100L);
        verify(exercisesRepository).getExerciseById(100L);
    }

    @Test
    public void getChildren() throws Exception {
        adapter.getChildren(100L);
        verify(muscleGroupsRepository).getSecondaryMuscleGroupsForExercise(100L);
    }
}