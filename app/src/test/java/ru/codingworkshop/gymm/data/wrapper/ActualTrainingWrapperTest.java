package ru.codingworkshop.gymm.data.wrapper;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ActualTrainingRepository;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

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
        ProgramTraining training = Models.createProgramTraining(1L, "foo");
        wrapper.setProgramTraining(training);
        assertEquals(training, wrapper.getProgramTraining());
    }

    @Test(expected = NullPointerException.class)
    public void setNullProgramTraining() {
        wrapper.setProgramTraining(null);
    }

    @Test
    public void programExercisesSetAndGet() {
        List<ProgramExercise> programExercises = Models.createProgramExercises(10);
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
        List<Exercise> exercises = Models.createExercises("foo", "bar", "baz");
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
        List<ProgramSet> sets = Models.createProgramSets(2L, 10);
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
        ProgramExercise exercise = Models.createProgramExercise(2L, 1L, 100L, false);
        List<ProgramSet> sets = Models.createProgramSets(2L, 9);
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
        ProgramExercise programExercise = Models.createProgramExercise(2L, 1L, 100L, false);
        Exercise exercise = Models.createExercise(100L, "foo");
        wrapper.setProgramExercises(Lists.newArrayList(programExercise));
        wrapper.setExercises(Lists.newArrayList(exercise));
        assertEquals(exercise, wrapper.getExerciseForProgramExercise(programExercise));
    }

    @Test(expected = NullPointerException.class)
    public void getExerciseForNull() {
        wrapper.getExerciseForProgramExercise(null);
    }

    @Test
    public void actualTrainingGetAndSet() {
        ActualTraining actualTraining = Models.createActualTraining(10L, 1L);
        wrapper.setActualTraining(actualTraining);
        assertEquals(actualTraining, wrapper.getActualTraining());
    }

    @Test(expected = NullPointerException.class)
    public void setNullActualTraining() {
        wrapper.setActualTraining(null);
    }

    @Test
    public void actualExercisesSetAndGet() {
        List<ProgramExercise> programExercises = Models.createProgramExercises(3);
        programExercises.forEach(pe -> pe.setSortOrder(2 - pe.getSortOrder()));
        List<ActualExercise> exercises = Models.createActualExercises(401L, 402L, 403L);
        for (int i = 0; i < 3; i++) {
            exercises.get(i).setProgramExerciseId(programExercises.get(i).getId());
        }
        wrapper.setProgramExercises(programExercises);
        wrapper.setActualExercises(exercises);

        Map<Long, ProgramExercise> exerciseMap = Maps.uniqueIndex(programExercises, Model::getId);
        List<ActualExercise> expectedActualExercises = exercises.stream().sorted((a, b) -> exerciseMap.get(a.getProgramExerciseId()).getSortOrder() - exerciseMap.get(b.getProgramExerciseId()).getSortOrder()).collect(Collectors.toList());
        assertEquals(expectedActualExercises, wrapper.getActualExercises());
    }

    @Test(expected = NullPointerException.class)
    public void setNullActualExercises() {
        wrapper.setActualExercises(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setEmptyActualExercises() {
        wrapper.setActualExercises(Lists.newArrayList());
    }

    @Test
    public void actualTrainingCreation() {
        ProgramTraining programTraining = stubProgramTraining();
        List<ProgramExercise> programExercises = stubProgramExercises();
        List<ProgramSet> programSets = stubProgramSets();
        stubInsertActualTrainingRepo();
        List<Exercise> exercises = stubExercisesRepo();

        LiveTest.verifyLiveData(wrapper.create(1L), w -> {
            assertProgramTrainingLoaded(w, programTraining, programExercises, programSets);
            assertActualTrainingLoaded(w);
            assertExercisesLoaded(w, exercises);

            return true;
        });

        verifyProgramTrainingRepo();
        verifyInsertActualTrainingRepo();
        verifyExercisesRepo();
    }

    private ProgramTraining stubProgramTraining() {
        final LiveData<ProgramTraining> liveProgramTraining = Models.createLiveProgramTraining(1L, "foo", false);
        when(programTrainingRepository.getProgramTrainingById(1L)).thenReturn(liveProgramTraining);
        return liveProgramTraining.getValue();
    }

    private List<ProgramExercise> stubProgramExercises() {
        final LiveData<List<ProgramExercise>> liveProgramExercises = Models.createLiveProgramExercises(10);
        AtomicLong l = new AtomicLong();
        liveProgramExercises.getValue().forEach(e -> e.setExerciseId(100L + l.getAndIncrement() % 3));
        when(programTrainingRepository.getProgramExercisesForTraining(1L)).thenReturn(liveProgramExercises);
        return liveProgramExercises.getValue();
    }

    private List<ProgramSet> stubProgramSets() {
        final LiveData<List<ProgramSet>> liveProgramSets = Models.createLiveProgramSets(2L, 10);
        when(programTrainingRepository.getProgramSetsForTraining(1L)).thenReturn(liveProgramSets);
        return liveProgramSets.getValue();
    }

    private void assertProgramTrainingLoaded(ActualTrainingWrapper w, ProgramTraining programTraining, List<ProgramExercise> programExercises, List<ProgramSet> programSets) {
        assertEquals(programTraining, w.getProgramTraining());
        assertEquals(programExercises, w.getProgramExercises());
        assertEquals(programSets, w.getProgramSetsForExercise(2L));
    }

    private void verifyProgramTrainingRepo() {
        verify(programTrainingRepository).getProgramTrainingById(1L);
        verify(programTrainingRepository).getProgramExercisesForTraining(1L);
        verify(programTrainingRepository).getProgramSetsForTraining(1L);
    }

    private void stubInsertActualTrainingRepo() {
        doAnswer(invocation -> {
            ActualTraining training = invocation.getArgument(0);
            training.setId(1000L);
            return null;
        }).when(actualTrainingRepository).insertActualTraining(any(ActualTraining.class));

        doAnswer(invocation -> {
            List<ActualExercise> actualExercises = invocation.getArgument(0);
            final AtomicLong i = new AtomicLong();
            actualExercises.forEach(e -> e.setId(i.incrementAndGet()));
            return null;
        }).when(actualTrainingRepository).insertActualExercises(any());
    }

    private void stubLoadActualTrainingRepo() {
        when(actualTrainingRepository.getActualTrainingById(1000L)).thenReturn(Models.createLiveActualTraining(1000L, 1L));
        final List<ActualExercise> actualExercises = Models.createActualExercises(1L, 2L, 3L);
        actualExercises.forEach(ae -> {
            ae.setActualTrainingId(1000L);
            ae.setExerciseName(Lists.newArrayList("foo", "bar", "baz").get((int)(ae.getId()-1)));
        });
        when(actualTrainingRepository.getActualExercisesForActualTraining(1000L)).thenReturn(LiveDataUtil.getLive(actualExercises));
        when(actualTrainingRepository.getActualSetsForActualTraining(1000L)).thenReturn(Models.createLiveActualSets(1L, 1003L, 1004L, 1005L));
    }

    private void assertActualTrainingLoaded(ActualTrainingWrapper w) {
        final ActualTraining actualTraining = w.getActualTraining();
        assertEquals(1000L, actualTraining.getId());
        assertEquals(1L, actualTraining.getProgramTrainingId().longValue());
        assertNotNull(actualTraining.getStartTime());
        assertNull(actualTraining.getFinishTime());

        List<String> exerciseNames = Lists.newArrayList("foo", "bar", "baz");
        for (int i = 0; i < w.getActualExercises().size(); i++) {
            ActualExercise exercise = w.getActualExercises().get(i);
            assertEquals(i + 1, exercise.getId());
            assertEquals(exerciseNames.get(i % 3), exercise.getExerciseName());
            assertEquals(1000L, exercise.getActualTrainingId());
            assertEquals(i + 2L, exercise.getProgramExerciseId().longValue());
        }
    }

    private void verifyInsertActualTrainingRepo() {
        verify(actualTrainingRepository).insertActualTraining(any());
        verify(actualTrainingRepository).insertActualExercises(any());
    }

    private void verifyLoadActualTrainingRepo() {
        verify(actualTrainingRepository).getActualTrainingById(1000L);
        verify(actualTrainingRepository).getActualExercisesForActualTraining(1000L);
        verify(actualTrainingRepository).getActualSetsForActualTraining(1000L);
    }

    private List<Exercise> stubExercisesRepo() {
        final LiveData<List<Exercise>> liveExercises = Models.createLiveExercises("foo", "bar", "baz");
        when(exercisesRepository.getExercisesForProgramTraining(1L)).thenReturn(liveExercises);
        return liveExercises.getValue();
    }

    private void assertExercisesLoaded(ActualTrainingWrapper w, List<Exercise> exercises) {
        assertEquals(exercises, w.getExercises());
    }

    private void verifyExercisesRepo() {
        verify(exercisesRepository).getExercisesForProgramTraining(1L);
    }

    @Test
    public void createAndGetActualSet() {
        ActualExercise actualExercise = Models.createActualExercise(1002L, "foo", 1000L, 2L);
        doAnswer(invocation -> {
            ActualSet set = invocation.getArgument(0);
            set.setId(1003L);
            return null;
        }).when(actualTrainingRepository).insertActualSet(any(ActualSet.class));
        wrapper.createActualSet(actualExercise, 7, 14.5);
        assertEquals(1003L, wrapper.getActualSets(actualExercise).get(0).getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createActualSetForInvalidActualExercise() {
        wrapper.createActualSet(Models.createActualExercise(0L, "foo", 1000L, 2L), 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getActualSetsForInvalidActualExercise() {
        wrapper.getActualSets(new ActualExercise("foo", 100L, 500L));
    }

    @Test
    public void actualSetsSetAndGet() {
        ActualExercise ex1 = Models.createActualExercise(1002L, "foo", 1L, 2L);
        List<ActualSet> actualSets = Models.createActualSets(1002L, 1003L, 1004L);

        wrapper.setActualSets(actualSets);
        assertEquals(actualSets.subList(0, 2), wrapper.getActualSets(ex1));
        wrapper.createActualSet(ex1, 10, 10.2); // mutable check
    }

    @Test
    public void actualTrainingLoading() {
        ProgramTraining programTraining = stubProgramTraining();
        List<ProgramExercise> programExercises = stubProgramExercises();
        List<ProgramSet> programSets = stubProgramSets();

        stubLoadActualTrainingRepo();

        LiveTest.verifyLiveData(wrapper.load(1000L), w -> {
            assertProgramTrainingLoaded(w, programTraining, programExercises, programSets);
            assertActualTrainingLoaded(w);
            assertEquals(3, w.getActualSets(Models.createActualExercise(1L, "foo", 1000L, 2L)).size());

            return true;
        });

        verifyProgramTrainingRepo();
        verifyLoadActualTrainingRepo();
    }
}
