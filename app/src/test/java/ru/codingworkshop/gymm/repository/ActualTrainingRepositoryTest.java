package ru.codingworkshop.gymm.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.db.dao.ActualTrainingDao;
import ru.codingworkshop.gymm.util.ModelsFixture;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 29.07.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ActualTrainingRepositoryTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ActualTrainingDao dao;
    @Mock private ActualTrainingRepository.ATask asyncTask;
    private Executor executor = Runnable::run;
    private ActualTrainingRepository repository;

    @Before
    public void init() {
        repository = new ActualTrainingRepository(executor, asyncTask, dao);
    }

    // TODO add instrumented tests for async task

    @Test
    public void insertActualTraining() {
        ActualTraining training = ModelsFixture.createActualTraining(0L, 1L);
        when(dao.insertActualTraining(training)).thenReturn(11L);
        repository.insertActualTraining(training);
        verify(dao).insertActualTraining(training);
    }

    @Test
    public void getActualTrainingById() {
        repository.getActualTrainingById(1L);
        verify(dao).getActualTrainingById(1L);
    }

    @Test
    public void insertActualExercises() {
        List<Long> ids = Lists.newArrayList(1002L, 1003L, 1004L);
        Collection<ActualExercise> actualExercises = ModelsFixture.createActualExercises(ids.toArray(new Long[]{}));
        when(dao.insertActualExercises(actualExercises)).thenReturn(ids);
        repository.insertActualExercises(actualExercises);
        assertEquals(ids, actualExercises.stream().map(Model::getId).collect(Collectors.toList()));
        verify(dao).insertActualExercises(actualExercises);
    }

    @Test
    public void getActualExercisesForActualTraining() {
        repository.getActualExercisesForActualTraining(1000L);
        verify(dao).getActualExercisesForActualTraining(1000L);
    }

    @Test
    public void insertActualSet() {
        ActualSet actualSet = ModelsFixture.createActualSet(1003L, 1002L, 7);
        when(dao.insertActualSet(actualSet)).thenReturn(1003L);
        repository.insertActualSet(actualSet);
        verify(dao).insertActualSet(actualSet);
    }

    @Test
    public void getActualSetsForActualTraining() {
        repository.getActualSetsForActualTraining(1000L);
        verify(dao).getActualSetsForActualTraining(1000L);
    }
}
