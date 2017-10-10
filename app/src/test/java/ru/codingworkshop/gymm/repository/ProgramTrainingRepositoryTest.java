package ru.codingworkshop.gymm.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.stream.Collectors;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.db.dao.ProgramTrainingDao;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 21.06.2017.
 */

@RunWith(JUnitParamsRunner.class)
public class ProgramTrainingRepositoryTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ProgramTrainingDao dao;
    private ProgramTrainingRepository repository;

    @Before
    public void init() {
        dao = mock(ProgramTrainingDao.class);
        repository = new ProgramTrainingRepository(dao, Runnable::run);
    }

    @Test
    public void queryProgramTrainings() {
        repository.getProgramTrainings();
        verify(dao).getProgramTrainings();
    }

    @Test
    public void queryProgramTrainingById() {
        repository.getProgramTrainingById(1);
        verify(dao).getProgramTrainingById(1);
    }

    @Test
    public void queryProgramTrainingByName() throws Exception {
        repository.getProgramTrainingByName("foo");
        verify(dao).getProgramTrainingByName("foo");
    }

    @Test
    public void queryDraftingProgramTraining() {
        repository.getDraftingProgramTraining();
        verify(dao).getDraftingProgramTraining();
    }

    @Test
    public void insertProgramTraining() {
        ProgramTraining trainingEntity = Models.createProgramTraining(0L, "foo");
        trainingEntity.setDrafting(true);
        when(dao.insertProgramTraining(trainingEntity)).thenReturn(1L);
        repository.insertProgramTraining(trainingEntity);

        verify(dao).insertProgramTraining(trainingEntity);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertionFailTest() {
        ProgramTraining training = Models.createProgramTraining(0L, "foo");
        when(dao.insertProgramTraining(training)).thenReturn(-1L);
        repository.insertProgramTraining(training);
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
    public void queryProgramExercisesForTraining() {
        ProgramTraining programTraining = Models.createProgramTraining(1L, "foo");
        repository.getProgramExercisesForTraining(programTraining);
        verify(dao).getProgramExercisesForTraining(1L);
    }

    @Test
    public void queryProgramExercisesForTrainingId() {
        repository.getProgramExercisesForTraining(1L);
        verify(dao).getProgramExercisesForTraining(1L);
    }

    @Test
    public void queryProgramExerciseById() {
        repository.getProgramExerciseById(2);
        verify(dao).getProgramExerciseById(2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void queryDraftingProgramExercise() {
        ProgramTraining training = Models.createProgramTraining(1L, "foo");
        repository.getDraftingProgramExercise(training);
        repository.getDraftingProgramExercise(1L);
        verify(dao, times(2)).getDraftingProgramExercise(1L);
        training.setId(0L);
        repository.getDraftingProgramExercise(training);
    }

    @Test
    public void insertProgramExercise() {
        ProgramExercise exercise = Models.createProgramExercise(2L, 1L, 100L, true);
        exercise.setExerciseId(10);
        when(dao.insertProgramExercise(exercise)).thenReturn(1L);

        repository.insertProgramExercise(exercise);

        verify(dao).insertProgramExercise(exercise);
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "invalidProgramExercises")
    public void insertProgramExerciseWithoutProgramTrainingAndExercise(ProgramExercise exercise) {
        repository.insertProgramExercise(exercise);
    }

    private ProgramExercise[] invalidProgramExercises() {
        ProgramExercise exercise = new ProgramExercise();
        exercise.setExerciseId(10);
        ProgramExercise exerciseWithoutExercise = new ProgramExercise();
        exerciseWithoutExercise.setProgramTrainingId(1);
        return new ProgramExercise[] {
                exercise,
                exerciseWithoutExercise
        };
    }

    @Test
    public void updateProgramExercise() {
        ProgramExercise exercise = Models.createLiveProgramExercise(2, 1, false).getValue();
        exercise.setExerciseId(10);
        when(dao.updateProgramExercise(exercise)).thenReturn(1);

        repository.updateProgramExercise(exercise);

        verify(dao).updateProgramExercise(exercise);
    }

    @Test
    public void updateProgramExercises() {
        List<ProgramExercise> exercisesToUpdate = Lists.newArrayList(2,3,4,5).stream().map(id -> {
            ProgramExercise exercise = Models.createLiveProgramExercise(id, 1, false).getValue();
            exercise.setExerciseId(10);
            return exercise;
        }).collect(Collectors.toList());

        when(dao.updateProgramExercises(exercisesToUpdate)).thenReturn(exercisesToUpdate.size());

        repository.updateProgramExercises(exercisesToUpdate);

        verify(dao).updateProgramExercises(exercisesToUpdate);
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "invalidProgramExercises")
    public void updateProgramExerciseWithoutProgramTrainingAndExercise(ProgramExercise exercise) {
        repository.updateProgramExercise(exercise);
    }

    @Test
    public void deleteProgramExercise() {
        ProgramExercise exercise = Models.createLiveProgramExercise(2, 1, false).getValue();
        when(dao.deleteProgramExercise(exercise)).thenReturn(1);

        repository.deleteProgramExercise(exercise);

        verify(dao).deleteProgramExercise(exercise);
    }

    @Test
    public void deleteProgramExercises() {
        List<ProgramExercise> exercisesToDelete = Lists.newArrayList(2,3,4,5).stream().map(id -> {
            ProgramExercise exercise = Models.createLiveProgramExercise(id, 1, false).getValue();
            exercise.setExerciseId(10);
            return exercise;
        }).collect(Collectors.toList());

        when(dao.deleteProgramExercises(exercisesToDelete)).thenReturn(exercisesToDelete.size());

        repository.deleteProgramExercises(exercisesToDelete);

        verify(dao).deleteProgramExercises(exercisesToDelete);
    }

    @Test
    public void queryProgramSetsForExercise() {
        ProgramExercise exercise = Models.createLiveProgramExercise(2, 1, false).getValue();
        ProgramSet set = Models.createLiveProgramSet(3, 2, 1).getValue();
        LiveData<List<ProgramSet>> list = LiveDataUtil.getLive(Lists.newArrayList(set));

        when(dao.getProgramSetsForExercise(exercise.getId())).thenReturn(list);
        LiveTest.verifyLiveData(
                repository.getProgramSetsForExercise(exercise),
                returned -> !returned.isEmpty()
                        && returned.get(0).getId() == 3
                        && returned.get(0).getProgramExerciseId() == 2
                        && returned.get(0).getReps() == 1
        );

        verify(dao).getProgramSetsForExercise(2);
    }

    @Test
    public void queryProgramSetsForExerciseId() {
        ProgramExercise exercise = Models.createLiveProgramExercise(2, 1, false).getValue();
        ProgramSet set = Models.createLiveProgramSet(3, 2, 1).getValue();
        LiveData<List<ProgramSet>> list = LiveDataUtil.getLive(Lists.newArrayList(set));

        when(dao.getProgramSetsForExercise(exercise.getId())).thenReturn(list);
        LiveTest.verifyLiveData(
                repository.getProgramSetsForExercise(exercise.getId()),
                returned -> !returned.isEmpty()
                        && returned.get(0).getId() == 3
                        && returned.get(0).getProgramExerciseId() == 2
                        && returned.get(0).getReps() == 1
        );

        verify(dao).getProgramSetsForExercise(2);
    }

    @Test
    public void queryProgramSetsForTrainingId() {
        List<Long> ids = Lists.newArrayList(23L, 24L, 25L, 26L, 27L, 38L, 39L);
        List<ProgramSet> sets = ids.stream().map(id -> Models.createLiveProgramSet(id, id / 10, id.intValue()).getValue()).collect(Collectors.toList());

        when(dao.getProgramSetsForTraining(1L)).thenReturn(LiveDataUtil.getLive(sets));

        LiveData<List<ProgramSet>> liveSets = repository.getProgramSetsForTraining(1L);
        LiveTest.verifyLiveData(liveSets, returnedSets -> sets == returnedSets);

        verify(dao).getProgramSetsForTraining(1L);
    }

    @Test
    public void queryProgramSetById() {
        when(dao.getProgramSetById(3)).thenReturn(Models.createLiveProgramSet(3, 2, 1));
        LiveTest.verifyLiveData(repository.getProgramSetById(3), returned -> returned.getId() == 3 && returned.getReps() == 1);
        verify(dao).getProgramSetById(3);
    }

    @Test
    public void insertProgramSet() {
        ProgramSet set = Models.createLiveProgramSet(3L, 2L, 1).getValue();
        when(dao.insertProgramSet(set)).thenReturn(set.getId());

        repository.insertProgramSet(set);

        verify(dao).insertProgramSet(set);
    }

    @Test
    public void insertProgramSets() {
        List<Long> setIds = Lists.newArrayList(1L, 2L, 3L);
        List<ProgramSet> sets = setIds.stream().map(
                id -> Models.createLiveProgramSet(id, 2L, id.intValue() * 10).getValue()
        ).collect(Collectors.toList());
        when(dao.insertProgramSets(sets)).thenReturn(setIds);

        repository.insertProgramSets(sets);

        verify(dao).insertProgramSets(sets);
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "invalidProgramSets")
    public void insertProgramSetWithZeroRepsAndWithoutExercise(ProgramSet set) {
        repository.insertProgramSet(set);
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
    public void updateProgramSet() {
        ProgramSet set = Models.createLiveProgramSet(3, 2, 1).getValue();
        when(dao.updateProgramSet(set)).thenReturn(1);

        repository.updateProgramSet(set);

        verify(dao).updateProgramSet(set);
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

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "invalidProgramSets")
    public void updateProgramSetWithZeroRepsAndWithoutExercise(ProgramSet set) {
        repository.updateProgramSet(set);
    }

    @Test
    public void deleteProgramSet() {
        ProgramSet set = Models.createLiveProgramSet(3, 2, 1).getValue();
        when(dao.deleteProgramSet(set)).thenReturn(1);

        repository.deleteProgramSet(set);

        verify(dao).deleteProgramSet(set);
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
