package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.repository.MuscleGroupsRepository;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 22.11.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class MuscleGroupPickerFragmentViewModelTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private MuscleGroupsRepository repository;
    private MuscleGroupPickerFragmentViewModel vm;

    @Before
    public void setUp() throws Exception {
        when(repository.getMuscleGroups(true))
                .thenReturn(Models.createLiveMuscleGroups(1L))
                .thenReturn(Models.createLiveMuscleGroups(2L));

        when(repository.getMuscleGroups(false))
                .thenReturn(Models.createLiveMuscleGroups(3L))
                .thenReturn(Models.createLiveMuscleGroups(4L));

        vm = new MuscleGroupPickerFragmentViewModel(repository);
    }

    @Test
    public void load() throws Exception {
        LiveData<List<MuscleGroup>> anteriorMuscles = vm.load(true);
        assertNotNull(anteriorMuscles);
        assertSame(anteriorMuscles, vm.load(true));
        verify(repository).getMuscleGroups(true);

        LiveData<List<MuscleGroup>> posteriorMuscles = vm.load(false);
        assertNotNull(posteriorMuscles);
        assertNotSame(anteriorMuscles, posteriorMuscles);
        assertSame(posteriorMuscles, vm.load(false));
        verify(repository).getMuscleGroups(false);
    }
}