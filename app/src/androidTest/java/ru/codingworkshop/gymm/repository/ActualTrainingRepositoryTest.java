package ru.codingworkshop.gymm.repository;

import android.arch.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.db.dao.ActualTrainingDao;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 20.09.2017 as part of the Gymm project.
 */

public class ActualTrainingRepositoryTest {
    @Mock private ActualTrainingDao dao;
    private ActualTrainingRepository repository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        repository = new ActualTrainingRepository(Runnable::run, dao);
    }

    @Test
    public void insertActualTrainingWithResult() throws Exception {
        ActualTraining actualTraining = Models.createActualTraining(0L, 1L);
        when(dao.insertActualTraining(actualTraining)).thenReturn(11L);

        LiveData<Long> liveId = repository.insertActualTrainingWithResult(actualTraining);
        assertEquals(11L, LiveTest.getValue(liveId).longValue());
        verify(dao).insertActualTraining(actualTraining);
    }

    @Test
    public void insertActualSetWithResult() throws Exception {
        ActualSet set = Models.createActualSet(0L, 12L, 10);
        set.setWeight(6.6);
        when(dao.insertActualSet(set)).thenReturn(13L);

        LiveData<Long> liveId = repository.insertActualSetWithResult(set);
        assertEquals(13L, LiveTest.getValue(liveId).longValue());
        verify(dao).insertActualSet(set);
    }
}
