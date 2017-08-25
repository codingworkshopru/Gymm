package ru.codingworkshop.gymm.ui.actual;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ru.codingworkshop.gymm.repository.ActualTrainingRepository;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 25.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ActualTrainingViewControllerTest {
    @Mock private ProgramTrainingRepository programRepository;
    @Mock private ActualTrainingRepository actualRepository;
    @Mock private ExercisesRepository exercisesRepository;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Test
    public void startTraining() throws Exception {
        when(programRepository.getProgramTrainingById(1L)).thenReturn(Models.createLiveProgramTraining(1L, "foo", false));
        when(programRepository.getProgramExercisesForTraining(1L)).thenReturn(Models.createLiveProgramExercises(1));
        when(programRepository.getProgramSetsForTraining(1L)).thenReturn(Models.createLiveProgramSets(1));

        when(exercisesRepository.getExercisesForProgramTraining(1L)).thenReturn(Models.createLiveExercises("bar"));

        ActualTrainingViewController vc = new ActualTrainingViewController(actualRepository, programRepository, exercisesRepository);
        LiveTest.verifyLiveData(vc.startTraining(1L), loaded -> loaded);
        verify(actualRepository).insertActualTraining(any());
    }
}
