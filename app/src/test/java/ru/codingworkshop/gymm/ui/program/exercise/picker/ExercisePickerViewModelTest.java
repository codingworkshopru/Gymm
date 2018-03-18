package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.MuscleGroupsRepository;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExercisePickerViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ExercisesRepository exercisesRepository;
    @Mock private MuscleGroupsRepository muscleGroupsRepository;
    @InjectMocks private ExercisePickerViewModel vm;

    @Test
    public void getExercisesForMuscleGroup() throws Exception {
        LiveData<List<Exercise>> liveExercises = Models.createLiveExercises();
        when(exercisesRepository.getExercisesForMuscleGroup(200L))
                .thenReturn(liveExercises);

        MuscleGroup muscleGroup = Models.createMuscleGroup(200L, "foo");
        vm.setMuscleGroup(muscleGroup);
        assertSame(LiveTest.getValue(liveExercises),
                LiveTest.getValue(vm.getExercisesForMuscleGroup()));

        verify(exercisesRepository).getExercisesForMuscleGroup(200L);
    }

    @Test
    public void setAndGetExercise() throws Exception {
        Exercise exercise = Models.createExercise(100L, "foo");
        vm.setExercise(exercise);
        assertSame(LiveTest.getValue(vm.getExercise()), exercise);
    }

    @Test
    public void clearMuscleGroup() {
        vm.setMuscleGroup(Models.createMuscleGroup(201L, "foo"));
        vm.clearMuscleGroup();
        assertNull(vm.getMuscleGroup().getValue());
        assertNull(vm.getExercisesForMuscleGroup().getValue());
    }

    @Test
    public void getMuscleGroups() {
        LiveData<List<MuscleGroup>> liveMuscleGroups = Models.createLiveMuscleGroups(101L);
        when(muscleGroupsRepository.getMuscleGroups(true))
                .thenReturn(liveMuscleGroups)
                .thenReturn(Models.createLiveMuscleGroups(102L));

        assertSame(liveMuscleGroups, vm.getMuscleGroups(true));
        assertSame(liveMuscleGroups, vm.getMuscleGroups(true));

        verify(muscleGroupsRepository).getMuscleGroups(true);
    }
}