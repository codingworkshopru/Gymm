package ru.codingworkshop.gymm.data.tree.loader.datasource;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.MuscleGroupsRepository;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 24.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ExerciseDataSourceTest {
    @Mock private ExercisesRepository repository;
    @Mock private MuscleGroupsRepository muscleGroupsRepository;

    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void test() throws Exception {
        final LiveData<Exercise> liveExercise = Models.createLiveExercise(100L, "foo");
        liveExercise.getValue().setPrimaryMuscleGroupId(200L);
        final LiveData<List<MuscleGroup>> liveMuscleGroups = Models.createLiveMuscleGroups(1L);
        final LiveData<MuscleGroup> liveMuscleGroup = Models.createLiveMuscleGroup(200L, "bar");

        when(repository.getExerciseById(100L)).thenReturn(liveExercise);
        when(muscleGroupsRepository.getSecondaryMuscleGroupsForExercise(100L)).thenReturn(liveMuscleGroups);
        when(muscleGroupsRepository.getMuscleGroupById(200L)).thenReturn(liveMuscleGroup);

        ExerciseDataSource dataSource = new ExerciseDataSource(repository, muscleGroupsRepository, 100L);

        assertEquals(liveExercise, dataSource.getParent());
        assertEquals(liveMuscleGroups, dataSource.getChildren());
        LiveTest.verifyLiveData(dataSource.getPrimaryMuscleGroup(), mg -> mg.getName().equals("bar"));

        verify(repository).getExerciseById(100L);
        verify(muscleGroupsRepository).getSecondaryMuscleGroupsForExercise(100L);
        verify(muscleGroupsRepository).getMuscleGroupById(200L);
    }
}