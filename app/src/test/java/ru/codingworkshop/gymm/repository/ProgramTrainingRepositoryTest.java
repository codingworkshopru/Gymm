package ru.codingworkshop.gymm.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.Flowable;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.db.dao.ProgramTrainingDao;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 21.06.2017.
 */

@RunWith(JUnitParamsRunner.class)
public class ProgramTrainingRepositoryTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ProgramTrainingDao dao;
    private ProgramTrainingRepository repository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        repository = new ProgramTrainingRepository(dao, new InsertDelegate());
    }

    @Test
    public void getProgramTrainings() {
        repository.getProgramTrainings();
        verify(dao).getProgramTrainings();
    }

    @Test
    public void getProgramTrainingById() {
        repository.getProgramTrainingById(1);
        verify(dao).getProgramTrainingById(1);
    }

    @Test
    public void getProgramTrainingByName() {
        repository.getProgramTrainingByName("foo");
        verify(dao).getProgramTrainingByName("foo");
    }

    @Test
    public void insertProgramTraining() {
        ProgramTraining trainingEntity = Models.createProgramTraining(0L, "foo");
        when(dao.insertProgramTraining(trainingEntity)).thenReturn(1L);
        assertEquals(1L, repository.insertProgramTraining(trainingEntity));

        verify(dao).insertProgramTraining(trainingEntity);
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "getInvalidNames")
    public void insertProgramTrainingWithInvalidName(String name) {
        ProgramTraining training = Models.createProgramTraining(1, name);
        repository.insertProgramTraining(training);
    }

    private String[] getInvalidNames() {
        return new String[] {"", null};
    }

    @Test
    public void deleteProgramTraining() {
        ProgramTraining trainingEntity = Models.createProgramTraining(1, "foo");
        when(dao.deleteProgramTraining(trainingEntity)).thenReturn(1);

        repository.deleteProgramTraining(trainingEntity);

        verify(dao).deleteProgramTraining(trainingEntity);
    }

    @Test
    public void updateProgramTraining() {
        ProgramTraining trainingEntity = Models.createProgramTraining(1, "foo");
        when(dao.updateProgramTraining(trainingEntity)).thenReturn(1);

        trainingEntity.setName("bar");

        repository.updateProgramTraining(trainingEntity);

        verify(dao).updateProgramTraining(trainingEntity);
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "getInvalidNames")
    public void updateProgramTrainingWithEmptyAndNullName(String name) {
        ProgramTraining training = Models.createProgramTraining(1, name);
        repository.updateProgramTraining(training);
    }

    @Test
    public void getProgramExercisesForTraining() {
        repository.getProgramExercisesForTraining(1L);
        verify(dao).getProgramExercisesForTraining(1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertProgramExercisesWithoutParentId() {
        ProgramExercise exercise = Models.createProgramExercise(0L, 0L, 100L);
        ArrayList<ProgramExercise> programExercises = Lists.newArrayList(exercise);
        repository.insertProgramExercises(programExercises);
        verify(dao, never()).insertProgramExercises(programExercises);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertExercisesWithoutExerciseId() {
        ProgramExercise exercise = Models.createProgramExercise(0L, 2L, 0L);
        ArrayList<ProgramExercise> programExercises = Lists.newArrayList(exercise);
        repository.insertProgramExercises(programExercises);
        verify(dao, never()).insertProgramExercises(programExercises);
    }

    @Test
    public void insertExercises() {
        ArrayList<Long> ids = Lists.newArrayList(2L);
        when(dao.insertProgramExercises(anyCollection())).thenReturn(ids);
        List<ProgramExercise> programExercises = Models.createProgramExercises(1);
        programExercises.get(0).setId(0L);
        assertEquals(ids, repository.insertProgramExercises(programExercises));
        assertEquals(ids, Lists.transform(programExercises, Model::getId));
        verify(dao).insertProgramExercises(anyCollection());
    }

    private ProgramExercise[] invalidProgramExercises() {
        ProgramExercise exercise = new ProgramExercise();
        exercise.setExerciseId(10L);
        ProgramExercise exerciseWithoutExercise = new ProgramExercise();
        exerciseWithoutExercise.setProgramTrainingId(1);
        return new ProgramExercise[] {
                exercise,
                exerciseWithoutExercise
        };
    }

    @Test
    public void updateProgramExercises() {
        List<ProgramExercise> exercisesToUpdate = Lists.newArrayList(2,3,4,5).stream().map(id -> {
            ProgramExercise exercise = Models.createLiveProgramExercise(id, 1).getValue();
            exercise.setExerciseId(10L);
            return exercise;
        }).collect(Collectors.toList());

        when(dao.updateProgramExercises(exercisesToUpdate)).thenReturn(exercisesToUpdate.size());

        repository.updateProgramExercises(exercisesToUpdate);

        verify(dao).updateProgramExercises(exercisesToUpdate);
    }

    @Test
    public void deleteProgramExercises() {
        List<ProgramExercise> exercisesToDelete = Lists.newArrayList(2,3,4,5).stream().map(id -> {
            ProgramExercise exercise = Models.createLiveProgramExercise(id, 1).getValue();
            exercise.setExerciseId(10L);
            return exercise;
        }).collect(Collectors.toList());

        when(dao.deleteProgramExercises(exercisesToDelete)).thenReturn(exercisesToDelete.size());

        repository.deleteProgramExercises(exercisesToDelete);

        verify(dao).deleteProgramExercises(exercisesToDelete);
    }

    @Test
    public void getProgramSetsForTraining() {
        List<Long> ids = Lists.newArrayList(23L, 24L, 25L, 26L, 27L, 38L, 39L);
        List<ProgramSet> sets = ids.stream().map(id -> Models.createLiveProgramSet(id, id / 10, id.intValue()).getValue()).collect(Collectors.toList());

        when(dao.getProgramSetsForTraining(1L)).thenReturn(sets);

        assertEquals(sets, repository.getProgramSetsForTraining(1L));

        verify(dao).getProgramSetsForTraining(1L);
    }

    @Test
    public void insertProgramSets() {
        List<Long> setIds = Lists.newArrayList(1L);
        List<ProgramSet> sets = Models.createProgramSets(2L, 1);
        when(dao.insertProgramSets(sets)).thenReturn(setIds);

        assertEquals(setIds, repository.insertProgramSets(sets));

        verify(dao).insertProgramSets(sets);
    }

    private ProgramSet[] invalidProgramSets() {
        ProgramSet setWithZeroReps = new ProgramSet();
        setWithZeroReps.setProgramExerciseId(2);
        ProgramSet setWithoutExercise = new ProgramSet();
        setWithoutExercise.setReps(1);
        return new ProgramSet[] {
                setWithoutExercise,
                setWithZeroReps
        };
    }

    @Test
    public void updateProgramSets() {
        List<Long> setIds = Lists.newArrayList(1L, 2L, 3L);
        List<ProgramSet> sets = setIds.stream().map(
                id -> Models.createLiveProgramSet(id, 2L, id.intValue() * 10).getValue()
        ).collect(Collectors.toList());
        when(dao.updateProgramSets(sets)).thenReturn(3);

        repository.updateProgramSets(sets);

        verify(dao).updateProgramSets(sets);
    }

    @Test
    public void deleteProgramSets() {
        List<Long> setIds = Lists.newArrayList(1L, 2L, 3L);
        List<ProgramSet> sets = setIds.stream().map(
                id -> Models.createLiveProgramSet(id, 2L, id.intValue() * 10).getValue()
        ).collect(Collectors.toList());
        when(dao.deleteProgramSets(sets)).thenReturn(3);

        repository.deleteProgramSets(sets);

        verify(dao).deleteProgramSets(sets);
    }
}
