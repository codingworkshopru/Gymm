package ru.codingworkshop.gymm.data.wrapper;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.stream.Collectors;

import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ActualTrainingRepository;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.ModelsFixture;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 29.07.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ActualTrainingWrapperTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ProgramTrainingRepository programTrainingRepository;
    @Mock private ExercisesRepository exercisesRepository;
    @Mock private ActualTrainingRepository actualTrainingRepository;

    @Test
    public void actualTrainingCreation() {
        ActualTraining training = ActualTrainingWrapper.createActualTraining(1L);
        assertEquals(1L, training.getProgramTrainingId().longValue());
    }

    @Test
    public void actualTrainingSetAndGetTest() {
        ActualTraining training = ActualTrainingWrapper.createActualTraining(1L);
        ActualTrainingWrapper wrapper = new ActualTrainingWrapper();
        wrapper.setActualTraining(training);
        assertEquals(training, wrapper.getActualTraining());
    }

    @Test
    public void programTrainingWrapperSetAndGetTest() {
        ActualTrainingWrapper wrapper = new ActualTrainingWrapper();
        ProgramTrainingWrapper programTrainingWrapper = new ProgramTrainingWrapper(new ProgramTraining());
        wrapper.setProgramTrainingWrapper(programTrainingWrapper);
        assertEquals(programTrainingWrapper, wrapper.getProgramTrainingWrapper());
    }

    @Test
    public void creationTest() {
        List<Exercise> exercises = ModelsFixture.createExercises("foo", "bar", "baz");

        ActualTraining training = ModelsFixture.createActualTraining(11L, 1L);
        LiveData<ActualTraining> liveTraining = LiveDataUtil.getLive(training);

        when(actualTrainingRepository.insertActualTraining(any(ActualTraining.class))).thenReturn(LiveDataUtil.getLive(11L));
        when(actualTrainingRepository.getActualTrainingById(11L)).thenReturn(liveTraining);
        when(exercisesRepository.getExercisesForProgramTraining(1L)).thenReturn(LiveDataUtil.getLive(exercises));
        final LiveData<ProgramTraining> programTraining = ModelsFixture.createLiveProgramTraining(1L, "foo", false);
        when(programTrainingRepository.getProgramTrainingById(1L)).thenReturn(programTraining);
        final List<ProgramExercise> programExercises = ModelsFixture.createProgramExercises(5);
        when(programTrainingRepository.getProgramExercisesForTraining(1L)).thenReturn(LiveDataUtil.getLive(programExercises));
        final LiveData<List<ProgramSet>> liveProgramSets = ModelsFixture.createLiveProgramSets(10);
        when(programTrainingRepository.getProgramSetsForTraining(1L)).thenReturn(liveProgramSets);

        LiveData<ActualTrainingWrapper> liveWrapper = ActualTrainingWrapper.create(1L, actualTrainingRepository, programTrainingRepository, exercisesRepository);
        LiveTest.verifyLiveData(liveWrapper, wrapper -> {
            assertEquals(training, wrapper.getActualTraining());

            ProgramTrainingWrapper programTrainingWrapper = wrapper.getProgramTrainingWrapper();
            assertEquals(programTraining.getValue(), programTrainingWrapper.getProgramTraining());

            List<ProgramExerciseWrapper> exerciseWrappers = programTrainingWrapper.getExerciseWrappers();
            assertEquals(programExercises, exerciseWrappers.stream().map(ProgramExerciseWrapper::getProgramExercise).collect(Collectors.toList()));
            assertEquals(liveProgramSets.getValue(), exerciseWrappers.stream().flatMap(w -> w.getProgramSets().stream()).collect(Collectors.toList()));
            return true;
        });


        verify(actualTrainingRepository).insertActualTraining(any(ActualTraining.class));
        verify(actualTrainingRepository).getActualTrainingById(11L);
        verify(exercisesRepository).getExercisesForProgramTraining(1L);
        verify(programTrainingRepository).getProgramTrainingById(1L);
        verify(programTrainingRepository).getProgramExercisesForTraining(1L);
        verify(programTrainingRepository).getProgramSetsForTraining(1L);
    }

    @Test
    public void loadingTest() {
        ActualTraining training = ModelsFixture.createActualTraining(11L, 1L);

        when(actualTrainingRepository.getActualTrainingById(11L)).thenReturn(LiveDataUtil.getLive(training));

        LiveData<ActualTrainingWrapper> liveWrapper = ActualTrainingWrapper.load(11L, actualTrainingRepository);
        LiveTest.verifyLiveData(liveWrapper, w -> {
            assertEquals(training, w.getActualTraining());
            return true;
        });

        verify(actualTrainingRepository).getActualTrainingById(11L);
    }
}
