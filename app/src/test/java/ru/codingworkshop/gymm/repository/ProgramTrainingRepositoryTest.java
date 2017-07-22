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
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
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
        ProgramTraining training = new ProgramTraining();
        List<ProgramTraining> trainings = Lists.newArrayList(training);
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
        ProgramTraining trainingEntity = getProgramTraining(1, "foo", false).getValue();
        when(dao.insertProgramTraining(trainingEntity)).thenReturn(1L);

        repository.insertProgramTraining(trainingEntity);

        verify(dao).insertProgramTraining(trainingEntity);
    }

    @Test(expected = IllegalStateException.class)
    public void insertionFailTest() {
        ProgramTraining training = getProgramTraining(1, "foo", false).getValue();
        when(dao.insertProgramTraining(training)).thenReturn(-1L);
        repository.insertProgramTraining(training);
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "getEmptyStringAndNull")
    public void insertProgramTrainingWithEmptyAndNullName(String name) {
        ProgramTraining training = getProgramTraining(1, name, false).getValue();
        repository.insertProgramTraining(training);
    }

    private String[] getEmptyStringAndNull() {
        return new String[] {"", null};
    }

    @Test
    public void deleteProgramTraining() {
        ProgramTraining trainingEntity = getProgramTraining(1, "foo", false).getValue();
        when(dao.deleteProgramTraining(trainingEntity)).thenReturn(1);

        repository.deleteProgramTraining(trainingEntity);

        verify(dao).deleteProgramTraining(trainingEntity);
    }

    @Test
    public void updateProgramTraining() {
        ProgramTraining trainingEntity = getProgramTraining(1, "foo", false).getValue();
        when(dao.updateProgramTraining(trainingEntity)).thenReturn(1);

        trainingEntity.setName("bar");

        repository.updateProgramTraining(trainingEntity);

        verify(dao).updateProgramTraining(trainingEntity);
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "getEmptyStringAndNull")
    public void updateProgramTrainingWithEmptyAndNullName(String name) {
        ProgramTraining training = getProgramTraining(1, name, false).getValue();
        repository.updateProgramTraining(training);
    }

    @Test
    public void queryProgramExercisesForTraining() {
        ProgramTraining programTraining = getProgramTraining(1, "foo", false).getValue();
        LiveData<List<ProgramExercise>> list = LiveDataUtil.getLive(Lists.newArrayList(getProgramExercise(2, programTraining.getId(), false).getValue()));
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
        ProgramTraining programTraining = getProgramTraining(1, "foo", false).getValue();
        LiveData<List<ProgramExercise>> list = LiveDataUtil.getLive(Lists.newArrayList(getProgramExercise(2, programTraining.getId(), false).getValue()));
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
        ProgramTraining training = getProgramTraining(1, "foo", false).getValue();
        when(dao.getDraftingProgramExercise(1)).thenReturn(getProgramExercise(2, training.getId(), true));

        LiveTest.verifyLiveData(repository.getDraftingProgramExercise(training), returned -> returned.getId() == 2 && returned.isDrafting());
        verify(dao).getDraftingProgramExercise(1);
    }

    @Test
    public void insertProgramExercise() {
        ProgramExercise exercise = getProgramExercise(2, 1, false).getValue();
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
        ProgramExercise exercise = getProgramExercise(2, 1, false).getValue();
        exercise.setExerciseId(10);
        when(dao.updateProgramExercise(exercise)).thenReturn(1);

        repository.updateProgramExercise(exercise);

        verify(dao).updateProgramExercise(exercise);
    }

    @Test
    public void updateProgramExercises() {
        List<ProgramExercise> exercisesToUpdate = Lists.newArrayList();
        for (long i = 2; i < 6; i++) {
            ProgramExercise exercise = getProgramExercise(i, 1, false).getValue();
            exercise.setExerciseId(10);
            exercisesToUpdate.add(exercise);
        }

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
        ProgramExercise exercise = getProgramExercise(2, 1, false).getValue();
        when(dao.deleteProgramExercise(exercise)).thenReturn(1);

        repository.deleteProgramExercise(exercise);

        verify(dao).deleteProgramExercise(exercise);
    }

    @Test
    public void deleteProgramExercises() {
        List<ProgramExercise> exercisesToDelete = Lists.newArrayList();
        for (long i = 2; i < 6; i++) {
            ProgramExercise exercise = getProgramExercise(i, 1, false).getValue();
            exercise.setExerciseId(10);
            exercisesToDelete.add(exercise);
        }

        when(dao.deleteProgramExercises(exercisesToDelete)).thenReturn(exercisesToDelete.size());

        repository.deleteProgramExercises(exercisesToDelete);

        verify(dao).deleteProgramExercises(exercisesToDelete);
    }

    @Test
    public void queryProgramSetsForExercise() {
        ProgramExercise exercise = getProgramExercise(2, 1, false).getValue();
        ProgramSet set = getProgramSet(3, 2, 1).getValue();
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
    public void queryProgramSetById() {
        when(dao.getProgramSetById(3)).thenReturn(getProgramSet(3, 2, 1));
        LiveTest.verifyLiveData(repository.getProgramSetById(3), returned -> returned.getId() == 3 && returned.getReps() == 1);
        verify(dao).getProgramSetById(3);
    }

    @Test
    public void insertProgramSet() {
        ProgramSet set = getProgramSet(3, 2, 1).getValue();
        when(dao.insertProgramSet(set)).thenReturn(set.getId());

        repository.insertProgramSet(set);

        verify(dao).insertProgramSet(set);
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
        ProgramSet set = getProgramSet(3, 2, 1).getValue();
        when(dao.updateProgramSet(set)).thenReturn(1);

        repository.updateProgramSet(set);

        verify(dao).updateProgramSet(set);
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "invalidProgramSets")
    public void updateProgramSetWithZeroRepsAndWithoutExercise(ProgramSet set) {
        repository.updateProgramSet(set);
    }

    @Test
    public void deleteProgramSet() {
        ProgramSet set = getProgramSet(3, 2, 1).getValue();
        when(dao.deleteProgramSet(set)).thenReturn(1);

        repository.deleteProgramSet(set);

        verify(dao).deleteProgramSet(set);
    }

    private static LiveData<ProgramTraining> getProgramTraining(long id, String name, boolean drafting) {
        ProgramTraining entity = new ProgramTraining();
        entity.setId(id);
        entity.setName(name);
        entity.setDrafting(drafting);
        return LiveDataUtil.getLive(entity);
    }

    private static LiveData<ProgramExercise> getProgramExercise(long id, long programTrainingId, boolean drafting) {
        ProgramExercise entity = new ProgramExercise();
        entity.setId(id);
        entity.setProgramTrainingId(programTrainingId);
        entity.setDrafting(drafting);
        return LiveDataUtil.getLive(entity);
    }

    private static LiveData<ProgramSet> getProgramSet(long id, long programExerciseId, int reps) {
        ProgramSet set = new ProgramSet();
        set.setId(3);
        set.setProgramExerciseId(2);
        set.setReps(1);
        return LiveDataUtil.getLive(set);
    }
}
