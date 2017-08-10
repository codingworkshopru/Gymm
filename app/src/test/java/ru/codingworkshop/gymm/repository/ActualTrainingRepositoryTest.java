package ru.codingworkshop.gymm.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.Executor;

import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.db.dao.ActualTrainingDao;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.ModelsFixture;

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

    @Test
    public void insertActualTraining() {
        when(asyncTask.getLiveResult()).thenReturn(LiveDataUtil.getLive(1L));
        ActualTraining training = ModelsFixture.createActualTraining(0L, 1L);
        LiveData<Long> liveId = repository.insertActualTraining(training);
        LiveTest.verifyLiveData(liveId, id -> id == 1L);
    }

    @Test
    public void getActualTrainingById() {
        repository.getActualTrainingById(1L);
        verify(dao).getActualTrainingById(1L);
    }
}
