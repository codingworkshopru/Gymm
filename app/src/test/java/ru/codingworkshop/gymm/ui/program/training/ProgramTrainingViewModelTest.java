package ru.codingworkshop.gymm.ui.program.training;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.ModelsFixture;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 20.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramTrainingViewModelTest {
    @Mock private ProgramTrainingRepository repository;
    @Mock private ExercisesRepository exercisesRepository;
    private ProgramTrainingViewModel viewModel;

    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        viewModel = new ProgramTrainingViewModel(repository, exercisesRepository);
    }

    @Test
    public void createViewModelFromProgramTrainingId() {
        when(repository.getProgramTrainingById(1L)).thenReturn(ModelsFixture.createLiveProgramTraining(1L, "foo", false));
        when(repository.getProgramExercisesForTraining(1L)).thenReturn(ModelsFixture.createLiveProgramExercises(3));
        when(repository.getProgramSetsForTraining(1L)).thenReturn(ModelsFixture.createLiveProgramSets(3));
        when(exercisesRepository.getExercisesForProgramTraining(1L)).thenReturn(ModelsFixture.createLiveExercises("foobar", "bar", "baz"));

        viewModel = new ProgramTrainingViewModel(repository, exercisesRepository);
        LiveTest.verifyLiveData(viewModel.init(1L), b -> b);

        verify(repository).getProgramTrainingById(1L);
        verify(repository).getProgramExerciseById(1L);
        verify(repository).getProgramSetsForTraining(1L);
        verify(exercisesRepository).getExercisesForProgramTraining(1L);
    }
}