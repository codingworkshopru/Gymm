package ru.codingworkshop.gymm.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import ru.codingworkshop.gymm.data.entity.ProgramExerciseEntity;
import ru.codingworkshop.gymm.data.entity.ProgramSetEntity;
import ru.codingworkshop.gymm.data.entity.ProgramTrainingEntity;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.db.dao.ProgramTrainingDao;
import ru.codingworkshop.gymm.util.LiveTest;

import static org.mockito.Mockito.mock;
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
        ProgramTrainingEntity training = new ProgramTrainingEntity();
        List<ProgramTrainingEntity> trainings = Lists.newArrayList(training);
        when(dao.getProgramTrainings()).thenReturn(LiveDataUtil.getLive(trainings));

        LiveTest.verifyLiveData(repository.getProgramTrainings(), result -> result != null && !result.isEmpty());

        verify(dao).getProgramTrainings();
    }

    @Test
    public void queryProgramTrainingById() {
        when(dao.getProgramTrainingById(1)).thenReturn(getProgramTraining(1, "foo", false));
        LiveTest.verifyLiveData(repository.getProgramTrainingById(1), result -> result.getId() == 1 && result.getName().equals("foo"));
        verify(dao).getProgramTrainingById(1);
    }

    @Test
    public void queryDraftingProgramTraining() {
        when(dao.getDraftingProgramTraining()).thenReturn(getProgramTraining(1, "foo", true));
        LiveTest.verifyLiveData(repository.getDraftingProgramTraining(), result -> result.getId() == 1 && result.isDrafting());
        verify(dao).getDraftingProgramTraining();
    }

    @Test
    public void insertProgramTraining() {
        ProgramTrainingEntity trainingEntity = getProgramTraining(1, "foo", false).getValue();
        when(dao.insertProgramTraining(trainingEntity)).thenReturn(1L);

        repository.insertProgramTraining(trainingEntity);

        verify(dao).insertProgramTraining(trainingEntity);
    }

    @Test(expected = IllegalStateException.class)
    public void insertionFailTest() {
        ProgramTrainingEntity training = getProgramTraining(1, "foo", false).getValue();
        when(dao.insertProgramTraining(training)).thenReturn(-1L);
        repository.insertProgramTraining(training);
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "getEmptyStringAndNull")
    public void insertProgramTrainingWithEmptyAndNullName(String name) {
        ProgramTrainingEntity training = getProgramTraining(1, name, false).getValue();
        repository.insertProgramTraining(training);
    }

    private String[] getEmptyStringAndNull() {
        return new String[] {"", null};
    }

    @Test
    public void deleteProgramTraining() {
        ProgramTrainingEntity trainingEntity = getProgramTraining(1, "foo", false).getValue();
        when(dao.deleteProgramTraining(trainingEntity)).thenReturn(1);

        repository.deleteProgramTraining(trainingEntity);

        verify(dao).deleteProgramTraining(trainingEntity);
    }

    @Test
    public void updateProgramTraining() {
        ProgramTrainingEntity trainingEntity = getProgramTraining(1, "foo", false).getValue();
        when(dao.updateProgramTraining(trainingEntity)).thenReturn(1);

        trainingEntity.setName("bar");

        repository.updateProgramTraining(trainingEntity);

        verify(dao).updateProgramTraining(trainingEntity);
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "getEmptyStringAndNull")
    public void updateProgramTrainingWithEmptyAndNullName(String name) {
        ProgramTrainingEntity training = getProgramTraining(1, name, false).getValue();
        repository.updateProgramTraining(training);
    }

    @Test
    public void queryProgramExercisesForTraining() {
        ProgramTrainingEntity programTraining = getProgramTraining(1, "foo", false).getValue();
        LiveData<List<ProgramExerciseEntity>> list = LiveDataUtil.getLive(Lists.newArrayList(getProgramExercise(2, programTraining.getId(), false).getValue()));
        when(dao.getProgramExercisesForTraining(1)).thenReturn(list);

        LiveTest.verifyLiveData(
                repository.getProgramExercisesForTraining(programTraining),
                exerciseList -> exerciseList.get(0).getProgramTrainingId() == 1
                        && exerciseList.get(0).getId() == 2
        );
        verify(dao).getProgramExercisesForTraining(1);
    }

    @Test
    public void queryProgramExercisesForTrainingId() {
        ProgramTrainingEntity programTraining = getProgramTraining(1, "foo", false).getValue();
        LiveData<List<ProgramExerciseEntity>> list = LiveDataUtil.getLive(Lists.newArrayList(getProgramExercise(2, programTraining.getId(), false).getValue()));
        when(dao.getProgramExercisesForTraining(1)).thenReturn(list);

        LiveTest.verifyLiveData(
                repository.getProgramExercisesForTraining(1),
                exerciseList -> exerciseList.get(0).getProgramTrainingId() == 1
                        && exerciseList.get(0).getId() == 2
        );
        verify(dao).getProgramExercisesForTraining(1);
    }

    @Test
    public void queryProgramExerciseById() {
        when(dao.getProgramExerciseById(2)).thenReturn(getProgramExercise(2, 1, false));

        LiveTest.verifyLiveData(repository.getProgramExerciseById(2), returned -> returned.getId() == 2 && returned.getProgramTrainingId() == 1);
        verify(dao).getProgramExerciseById(2);
    }

    @Test
    public void queryDraftingProgramExercise() {
        ProgramTrainingEntity training = getProgramTraining(1, "foo", false).getValue();
        when(dao.getDraftingProgramExercise(1)).thenReturn(getProgramExercise(2, training.getId(), true));

        LiveTest.verifyLiveData(repository.getDraftingProgramExercise(training), returned -> returned.getId() == 2 && returned.isDrafting());
        verify(dao).getDraftingProgramExercise(1);
    }

    @Test
    public void insertProgramExercise() {
        ProgramExerciseEntity exercise = getProgramExercise(2, 1, false).getValue();
        exercise.setExerciseId(10);
        when(dao.insertProgramExercise(exercise)).thenReturn(1L);

        repository.insertProgramExercise(exercise);

        verify(dao).insertProgramExercise(exercise);
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "invalidProgramExercises")
    public void insertProgramExerciseWithoutProgramTrainingAndExercise(ProgramExerciseEntity exercise) {
        repository.insertProgramExercise(exercise);
    }

    private ProgramExerciseEntity[] invalidProgramExercises() {
        ProgramExerciseEntity exercise = new ProgramExerciseEntity();
        exercise.setExerciseId(10);
        ProgramExerciseEntity exerciseWithoutExercise = new ProgramExerciseEntity();
        exerciseWithoutExercise.setProgramTrainingId(1);
        return new ProgramExerciseEntity[] {
                exercise,
                exerciseWithoutExercise
        };
    }

    @Test
    public void updateProgramExercise() {
        ProgramExerciseEntity exercise = getProgramExercise(2, 1, false).getValue();
        exercise.setExerciseId(10);
        when(dao.updateProgramExercise(exercise)).thenReturn(1);

        repository.updateProgramExercise(exercise);

        verify(dao).updateProgramExercise(exercise);
    }

    @Test
    public void updateProgramExercises() {
        List<ProgramExerciseEntity> exercisesToUpdate = Lists.newArrayList();
        for (long i = 2; i < 6; i++) {
            ProgramExerciseEntity exercise = getProgramExercise(i, 1, false).getValue();
            exercise.setExerciseId(10);
            exercisesToUpdate.add(exercise);
        }

        when(dao.updateProgramExercises(exercisesToUpdate)).thenReturn(exercisesToUpdate.size());

        repository.updateProgramExercises(exercisesToUpdate);

        verify(dao).updateProgramExercises(exercisesToUpdate);
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "invalidProgramExercises")
    public void updateProgramExerciseWithoutProgramTrainingAndExercise(ProgramExerciseEntity exercise) {
        repository.updateProgramExercise(exercise);
    }

    @Test
    public void deleteProgramExercise() {
        ProgramExerciseEntity exercise = getProgramExercise(2, 1, false).getValue();
        when(dao.deleteProgramExercise(exercise)).thenReturn(1);

        repository.deleteProgramExercise(exercise);

        verify(dao).deleteProgramExercise(exercise);
    }

    @Test
    public void deleteProgramExercises() {
        List<ProgramExerciseEntity> exercisesToDelete = Lists.newArrayList();
        for (long i = 2; i < 6; i++) {
            ProgramExerciseEntity exercise = getProgramExercise(i, 1, false).getValue();
            exercise.setExerciseId(10);
            exercisesToDelete.add(exercise);
        }

        when(dao.deleteProgramExercises(exercisesToDelete)).thenReturn(exercisesToDelete.size());

        repository.deleteProgramExercises(exercisesToDelete);

        verify(dao).deleteProgramExercises(exercisesToDelete);
    }

    @Test
    public void queryProgramSetsForExercise() {
        ProgramExerciseEntity exercise = getProgramExercise(2, 1, false).getValue();
        ProgramSetEntity set = getProgramSet(3, 2, 1).getValue();
        LiveData<List<ProgramSetEntity>> list = LiveDataUtil.getLive(Lists.newArrayList(set));

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
    public void queryProgramSetById() {
        when(dao.getProgramSetById(3)).thenReturn(getProgramSet(3, 2, 1));
        LiveTest.verifyLiveData(repository.getProgramSetById(3), returned -> returned.getId() == 3 && returned.getReps() == 1);
        verify(dao).getProgramSetById(3);
    }

    @Test
    public void insertProgramSet() {
        ProgramSetEntity set = getProgramSet(3, 2, 1).getValue();
        when(dao.insertProgramSet(set)).thenReturn(set.getId());

        repository.insertProgramSet(set);

        verify(dao).insertProgramSet(set);
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "invalidProgramSets")
    public void insertProgramSetWithZeroRepsAndWithoutExercise(ProgramSetEntity set) {
        repository.insertProgramSet(set);
    }

    private ProgramSetEntity[] invalidProgramSets() {
        ProgramSetEntity setWithZeroReps = new ProgramSetEntity();
        setWithZeroReps.setProgramExerciseId(2);
        ProgramSetEntity setWithoutExercise = new ProgramSetEntity();
        setWithoutExercise.setReps(1);
        return new ProgramSetEntity[] {
                setWithoutExercise,
                setWithZeroReps
        };
    }

    @Test
    public void updateProgramSet() {
        ProgramSetEntity set = getProgramSet(3, 2, 1).getValue();
        when(dao.updateProgramSet(set)).thenReturn(1);

        repository.updateProgramSet(set);

        verify(dao).updateProgramSet(set);
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "invalidProgramSets")
    public void updateProgramSetWithZeroRepsAndWithoutExercise(ProgramSetEntity set) {
        repository.updateProgramSet(set);
    }

    @Test
    public void deleteProgramSet() {
        ProgramSetEntity set = getProgramSet(3, 2, 1).getValue();
        when(dao.deleteProgramSet(set)).thenReturn(1);

        repository.deleteProgramSet(set);

        verify(dao).deleteProgramSet(set);
    }

    private static LiveData<ProgramTrainingEntity> getProgramTraining(long id, String name, boolean drafting) {
        ProgramTrainingEntity entity = new ProgramTrainingEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setDrafting(drafting);
        return LiveDataUtil.getLive(entity);
    }

    private static LiveData<ProgramExerciseEntity> getProgramExercise(long id, long programTrainingId, boolean drafting) {
        ProgramExerciseEntity entity = new ProgramExerciseEntity();
        entity.setId(id);
        entity.setProgramTrainingId(programTrainingId);
        entity.setDrafting(drafting);
        return LiveDataUtil.getLive(entity);
    }

    private static LiveData<ProgramSetEntity> getProgramSet(long id, long programExerciseId, int reps) {
        ProgramSetEntity set = new ProgramSetEntity();
        set.setId(3);
        set.setProgramExerciseId(2);
        set.setReps(1);
        return LiveDataUtil.getLive(set);
    }
}
