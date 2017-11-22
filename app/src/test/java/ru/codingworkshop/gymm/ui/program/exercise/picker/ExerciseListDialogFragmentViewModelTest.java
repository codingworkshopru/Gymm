package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 21.11.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ExerciseListDialogFragmentViewModelTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ExercisesRepository repository;
    private ExerciseListDialogFragmentViewModel vm;

    @Before
    public void setUp() throws Exception {
        when(repository.getExercisesForMuscleGroup(200L))
                .thenReturn(Models.createLiveExercises("foo"))
                .thenReturn(Models.createLiveExercises("bar"));
        vm = new ExerciseListDialogFragmentViewModel(repository);
    }

    @Test
    public void load() throws Exception {
        LiveData<List<Exercise>> liveExercises = vm.load(200L);
        assertNotNull(liveExercises);
        assertSame(liveExercises, vm.load(200L));

        verify(repository).getExercisesForMuscleGroup(200L);
    }
}