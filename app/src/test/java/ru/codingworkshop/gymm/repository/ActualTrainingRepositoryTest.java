package ru.codingworkshop.gymm.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.Executor;
import java.util.function.LongSupplier;

import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.db.dao.ActualTrainingDao;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.ModelsFixture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
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
    private Executor executor = Runnable::run;
    private ActualTrainingRepository repository;

    @Before
    public void init() {
        repository = new ActualTrainingRepository(executor, dao);
    }

    @Test
    public void insertActualTraining() {
        ActualTraining training = ModelsFixture.createActualTraining(0L, 1L);
        when(dao.insertActualTraining(training)).thenReturn(11L);
        repository.asyncTask = mock(BaseRepository.InsertWithIdAsyncTask.class);
        when(repository.asyncTask.execute(any(LongSupplier.class))).then(invocation -> {
            LongSupplier insert = (LongSupplier) invocation.getArguments()[0];
            Long resultId = insert.getAsLong();
            when(repository.asyncTask.getLiveId()).thenReturn(LiveDataUtil.getLive(resultId));
            return null;
        });
        LiveTest.verifyLiveData(repository.insertActualTraining(training), id -> id == 11L);
        verify(dao).insertActualTraining(argThat(tr -> tr == training && tr.getStartTime() != null));
    }

    @Test
    public void getActualTrainingById() {
        repository.getActualTrainingById(1L);
        verify(dao).getActualTrainingById(1L);
    }
}
