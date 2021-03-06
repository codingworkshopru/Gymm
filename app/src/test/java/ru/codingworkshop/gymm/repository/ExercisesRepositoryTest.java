package ru.codingworkshop.gymm.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.db.dao.ExerciseDao;
import ru.codingworkshop.gymm.db.dao.MuscleGroupDao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 27.07.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ExercisesRepositoryTest {
    @Mock private ExerciseDao exerciseDao;
    @Mock private MuscleGroupDao muscleGroupDao;

    private ExercisesRepository repository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        repository = new ExercisesRepository(exerciseDao, muscleGroupDao);
    }

    @Test
    public void createWithSecondaryMuscleGroupsTest() {
        List<Exercise> exercises = Lists.newArrayList();

        repository.createWithSecondaryMuscleGroups(exercises);

        verify(exerciseDao).insertExercises(exercises);
        verify(exerciseDao).createLinks(anyList());
        verify(muscleGroupDao).getAllMuscleGroupsSync();
    }

    @Test
    public void emptinessTest() {
        when(exerciseDao.getExercisesCount()).thenReturn(0);
        assertTrue(repository.isEmpty());

        when(exerciseDao.getExercisesCount()).thenReturn(10);
        assertFalse(repository.isEmpty());

        verify(exerciseDao, times(2)).getExercisesCount();
    }

    @Test
    public void gettingDataTest() {
        repository.getExerciseById(1L);
        verify(exerciseDao).getExerciseById(1L);

        repository.getExercisesForMuscleGroup(2L);
        verify(exerciseDao).getExercisesForPrimaryMuscleGroup(2L);

        repository.getExercisesForProgramTraining(1L);
        verify(exerciseDao).getExercisesForProgramTraining(1L);
    }
}
