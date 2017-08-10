package ru.codingworkshop.gymm.data.wrapper;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.repository.ActualTrainingRepository;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.ModelsFixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 06.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ActualTrainingWrapperTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ActualTrainingWrapper wrapper;

    @Mock private ActualTrainingRepository actualTrainingRepository;
    @Mock private ProgramTrainingRepository programTrainingRepository;
    @Mock private ExercisesRepository exercisesRepository;

    @Before
    public void init() {
        wrapper = new ActualTrainingWrapper(actualTrainingRepository, programTrainingRepository, exercisesRepository);
    }

    @Test
    public void programTrainingGetAndSet() {
        ProgramTraining training = ModelsFixture.createProgramTraining(1L, "foo");
        wrapper.setProgramTraining(training);
        assertEquals(training, wrapper.getProgramTraining());
    }

    @Test(expected = NullPointerException.class)
    public void setNullProgramTraining() {
        wrapper.setProgramTraining(null);
    }

    @Test
    public void programExercisesSetAndGet() {
        List<ProgramExercise> programExercises = ModelsFixture.createProgramExercises(10);
        wrapper.setProgramExercises(programExercises);
        assertEquals(programExercises, wrapper.getProgramExercises());
    }

    @Test(expected = NullPointerException.class)
    public void setNullProgramExercises() {
        wrapper.setProgramExercises(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setEmptyProgramExercises() {
        wrapper.setProgramExercises(Lists.newArrayList());
    }

    @Test
    public void exercisesSetAndGet() {
        List<Exercise> exercises = ModelsFixture.createExercises("foo", "bar", "baz");
        wrapper.setExercises(exercises);
        assertEquals(exercises, wrapper.getExercises());
    }

    @Test(expected = NullPointerException.class)
    public void setNullExercises() {
        wrapper.setExercises(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setEmptyExercises() {
        wrapper.setExercises(Lists.newArrayList());
    }

    @Test
    public void programSetsSetAndGet() {
        List<ProgramSet> sets = ModelsFixture.createProgramSets(10);
        wrapper.setProgramSets(sets);
        assertEquals(sets, wrapper.getProgramSets());
    }

    @Test(expected = NullPointerException.class)
    public void setNullProgramSets() {
        wrapper.setProgramSets(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setEmptyProgramSets() {
        wrapper.setProgramSets(Lists.newArrayList());
    }

    @Test
    public void getProgramSetsForProgramExercise() {
        ProgramExercise exercise = ModelsFixture.createProgramExercise(2L, 1L, 100L, false);
        List<ProgramSet> sets = ModelsFixture.createProgramSets(9);
        wrapper.setProgramExercises(Lists.newArrayList(exercise));
        wrapper.setProgramSets(sets);
        assertEquals(sets, wrapper.getProgramSetsForExercise(exercise));
        assertEquals(sets, wrapper.getProgramSetsForExercise(exercise.getId()));
    }

    @Test(expected = NullPointerException.class)
    public void getProgramSetsForNull() {
        wrapper.getProgramSetsForExercise(null);
    }

    @Test
    public void getExerciseForProgramExercise() {
        ProgramExercise programExercise = ModelsFixture.createProgramExercise(2L, 1L, 100L, false);
        Exercise exercise = ModelsFixture.createExercise(100L, "foo");
        wrapper.setProgramExercises(Lists.newArrayList(programExercise));
        wrapper.setExercises(Lists.newArrayList(exercise));
        assertEquals(exercise, wrapper.getExerciseForProgramExercise(programExercise));
    }

    @Test(expected = NullPointerException.class)
    public void getExerciseForNull() {
        wrapper.getExerciseForProgramExercise(null);
    }

    @Test
    public void loadTrainingProgram() {
        final LiveData<ProgramTraining> liveProgramTraining = ModelsFixture.createLiveProgramTraining(1L, "foo", false);
        final LiveData<List<ProgramExercise>> liveProgramExercises = ModelsFixture.createLiveProgramExercises(10);
        final LiveData<List<ProgramSet>> liveProgramSets = ModelsFixture.createLiveProgramSets(10);
        when(programTrainingRepository.getProgramTrainingById(1L)).thenReturn(liveProgramTraining);
        when(programTrainingRepository.getProgramExercisesForTraining(1L)).thenReturn(liveProgramExercises);
        when(programTrainingRepository.getProgramSetsForTraining(1L)).thenReturn(liveProgramSets);

        final LiveData<List<Exercise>> liveExercises = ModelsFixture.createLiveExercises("foo", "bar", "baz");
        when(exercisesRepository.getExercisesForProgramTraining(1L)).thenReturn(liveExercises);

        LiveTest.verifyLiveData(wrapper.load(1L), w -> {
            assertEquals(liveProgramTraining.getValue(), w.getProgramTraining());
            assertEquals(liveProgramExercises.getValue(), w.getProgramExercises());
            assertEquals(liveProgramSets.getValue(), w.getProgramSetsForExercise(liveProgramExercises.getValue().get(0)));
            assertEquals(liveExercises.getValue(), w.getExercises());
            return true;
        });

        verify(programTrainingRepository).getProgramTrainingById(1L);
        verify(programTrainingRepository).getProgramExercisesForTraining(1L);
        verify(programTrainingRepository).getProgramSetsForTraining(1L);
        verify(exercisesRepository).getExercisesForProgramTraining(1L);
    }

    @Test
    public void actualTrainingGetAndSet() {
        ActualTraining actualTraining = ModelsFixture.createActualTraining(10L, 1L);
        wrapper.setActualTraining(actualTraining);
        assertEquals(actualTraining, wrapper.getActualTraining());
    }

    @Test(expected = NullPointerException.class)
    public void setNullActualTraining() {
        wrapper.setActualTraining(null);
    }

    @Test
    public void actualTrainingCreation() {
        doAnswer(invocation -> {
            ActualTraining training = invocation.getArgument(0);
            training.setId(1000L);
            return null;
        }).when(actualTrainingRepository).insertActualTraining(any(ActualTraining.class));

        final LiveData<ProgramTraining> liveProgramTraining = ModelsFixture.createLiveProgramTraining(1L, "foo", false);
        final LiveData<List<ProgramExercise>> liveProgramExercises = ModelsFixture.createLiveProgramExercises(10);
        final LiveData<List<ProgramSet>> liveProgramSets = ModelsFixture.createLiveProgramSets(10);
        when(programTrainingRepository.getProgramTrainingById(1L)).thenReturn(liveProgramTraining);
        when(programTrainingRepository.getProgramExercisesForTraining(1L)).thenReturn(liveProgramExercises);
        when(programTrainingRepository.getProgramSetsForTraining(1L)).thenReturn(liveProgramSets);

        final LiveData<List<Exercise>> liveExercises = ModelsFixture.createLiveExercises("foo", "bar", "baz");
        when(exercisesRepository.getExercisesForProgramTraining(1L)).thenReturn(liveExercises);

        LiveTest.verifyLiveData(wrapper.create(1L), w -> {
            assertEquals(liveProgramTraining.getValue(), w.getProgramTraining());
            assertEquals(liveProgramExercises.getValue(), w.getProgramExercises());
            assertEquals(liveProgramSets.getValue(), w.getProgramSetsForExercise(liveProgramExercises.getValue().get(0)));
            assertEquals(liveExercises.getValue(), w.getExercises());

            final ActualTraining actualTraining = w.getActualTraining();
            assertEquals(1000L, actualTraining.getId());
            assertEquals(1L, actualTraining.getProgramTrainingId().longValue());
            assertNotNull(actualTraining.getStartTime());
            assertNull(actualTraining.getFinishTime());
            return true;
        });

        verify(actualTrainingRepository).insertActualTraining(any());

        verify(programTrainingRepository).getProgramTrainingById(1L);
        verify(programTrainingRepository).getProgramExercisesForTraining(1L);
        verify(programTrainingRepository).getProgramSetsForTraining(1L);
        verify(exercisesRepository).getExercisesForProgramTraining(1L);
    }


}
