package ru.codingworkshop.gymm.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.db.dao.ActualTrainingDao;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 29.07.2017.
 */

@RunWith(JUnitParamsRunner.class)
public class ActualTrainingRepositoryTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ActualTrainingDao dao;
    private ActualTrainingRepository repository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        repository = new ActualTrainingRepository(dao, new InsertDelegate());
    }

    @Test
    public void insertActualTraining() {
        ActualTraining training = Models.createActualTraining(0L, 1L);
        when(dao.insertActualTraining(training)).thenReturn(11L);
        assertEquals(11L, repository.insertActualTraining(training));
        verify(dao).insertActualTraining(training);
    }

    @Test
    public void updateActualTraining() {
        ActualTraining training = Models.createActualTraining(11L, 1L);
        when(dao.updateActualTraining(training)).thenReturn(1);
        repository.updateActualTraining(training);
        verify(dao).updateActualTraining(training);
    }

    @Test
    public void deleteActualTraining() {
        ActualTraining training = Models.createActualTraining(11L, 1L);
        when(dao.deleteActualTraining(training)).thenReturn(1);
        repository.deleteActualTraining(training);
        verify(dao).deleteActualTraining(training);
    }

    @Test
    public void getActualTrainingById() {
        repository.getActualTrainingById(1L);
        verify(dao).getActualTrainingById(1L);
    }

    @Test
    public void insertActualExercises() {
        List<Long> ids = Lists.newArrayList(1002L, 1003L, 1004L);
        Collection<ActualExercise> actualExercises = Models.createActualExercises(ids.toArray(new Long[]{}));
        when(dao.insertActualExercises(actualExercises)).thenReturn(ids);
        repository.insertActualExercises(actualExercises);
        assertEquals(ids, actualExercises.stream().map(Model::getId).collect(Collectors.toList()));
        verify(dao).insertActualExercises(actualExercises);
    }

    @Test
    public void insertActualExercise() {
        ActualExercise actualExercise = Models.createActualExercise(0L, "foo", 11L, 2L);
        when(dao.insertActualExercise(actualExercise)).thenReturn(12L);
        repository.insertActualExercise(actualExercise);
        assertEquals(12L, actualExercise.getId());
        verify(dao).insertActualExercise(actualExercise);
    }

    @Test
    public void deleteActualExercises() {
        List<ActualExercise> actualExercises = Models.createActualExercises(12L);
        when(dao.deleteActualExercises(actualExercises)).thenReturn(1);
        repository.deleteActualExercises(actualExercises);
        verify(dao).deleteActualExercises(actualExercises);
    }

    @Test
    public void getActualExercisesForActualTraining() {
        repository.getActualExercisesForActualTraining(1000L);
        verify(dao).getActualExercisesForActualTraining(1000L);
    }

    @Parameters(method = "weights")
    @Test
    public void insertActualSet(Double weight, Double expectedWeight) {
        ActualSet actualSet = Models.createActualSet(0L, 1002L, 7);
        actualSet.setWeight(weight);
        when(dao.insertActualSet(actualSet)).thenReturn(1003L);
        repository.insertActualSet(actualSet);
        verify(dao).insertActualSet(argThat(set ->
                set == actualSet && ((set.getWeight() == null && expectedWeight == null) || set.getWeight().equals(expectedWeight))
        ));
    }

    @Test
    public void insertActualSetWithResult() {
        ActualSet set = Models.createActualSet(0L, 12L, 10);
        set.setWeight(6.6);
        when(dao.insertActualSet(set)).thenReturn(13L);

        LiveData<Long> liveId = repository.insertActualSetWithResult(set);
        LiveTest.verifyLiveData(liveId, id -> id == 13L);
        verify(dao).insertActualSet(set);
    }

    private Object[] weights() {
        return new Object[] {
                new Object[] {0.0, null},
                new Object[] {5.5, 5.5},
                new Object[] {null, null}
        };
    }

    @Parameters(method = "invalidActualSets")
    @Test(expected = IllegalArgumentException.class)
    public void insertInvalidActualSet(ActualSet actualSet) {
        repository.insertActualSet(actualSet);
    }

    @NonNull
    private static ActualSet[] invalidActualSets() {
        return new ActualSet[] {
                Models.createActualSet(13L, 0L, 10),
                Models.createActualSet(13L, 12L, 0)
        };
    }

    @Parameters(method = "weights")
    @Test
    public void updateActualSet(Double weight, Double expectedWeight) {
        ActualSet actualSet = Models.createActualSet(13L, 12L, 7);
        actualSet.setWeight(weight);
        when(dao.updateActualSet(actualSet)).thenReturn(1);
        repository.updateActualSet(actualSet);
        verify(dao).updateActualSet(argThat(set ->
                set == actualSet && ((set.getWeight() == null && expectedWeight == null) || set.getWeight().equals(expectedWeight))
        ));
    }

    @Parameters(method = "invalidActualSetsForUpdate")
    @Test(expected = IllegalArgumentException.class)
    public void updateInvalidActualSet(ActualSet actualSet) {
        repository.updateActualSet(actualSet);
    }

    @NonNull
    private static ActualSet[] invalidActualSetsForUpdate() {
        ActualSet[] actualSets = new ActualSet[3];
        System.arraycopy(invalidActualSets(), 0, actualSets, 0, 2);
        actualSets[2] = Models.createActualSet(0L, 12L, 10);
        return actualSets;
    }

    @Test
    public void deleteActualSet() {
        ActualSet actualSet = Models.createActualSet(13L, 12L, 7);
        when(dao.deleteActualSet(actualSet)).thenReturn(1);
        repository.deleteActualSet(actualSet);
        verify(dao).deleteActualSet(actualSet);
    }

    @Test
    public void getActualSetsForActualTraining() {
        repository.getActualSetsForActualTraining(1000L);
        verify(dao).getActualSetsForActualTraining(1000L);
    }

    @Test
    public void getActualExerciseNames() {
        repository.getActualExerciseNames();
        verify(dao).getActualExerciseNames();
    }

    @Test
    public void getStatisticsForExercise() {
        repository.getStatisticsForExercise("foo", new Date());
        verify(dao).getStatisticsForExercise(eq("foo"), any(Date.class));
    }

    @Test
    public void getActualTrainings() {
        repository.getActualTrainings();
        verify(dao).getActualTrainings();
    }
}
