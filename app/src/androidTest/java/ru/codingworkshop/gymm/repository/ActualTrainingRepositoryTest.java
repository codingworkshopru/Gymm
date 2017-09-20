package ru.codingworkshop.gymm.repository;

import android.arch.lifecycle.LiveData;

import org.junit.Test;

import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.db.dao.ActualTrainingDao;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 20.09.2017 as part of the Gymm project.
 */

public class ActualTrainingRepositoryTest {
    @Test
    public void insertActualTrainingWithResult() throws Exception {
        ActualTraining actualTraining = Models.createActualTraining(0L, 1L);

        ActualTrainingDao dao = mock(ActualTrainingDao.class);
        when(dao.insertActualTraining(actualTraining)).thenReturn(11L);

        ActualTrainingRepository repository = new ActualTrainingRepository(Runnable::run, dao);

        LiveData<Long> liveId = repository.insertActualTrainingWithResult(actualTraining);

        assertEquals(11L, LiveTest.getValue(liveId).longValue());

        verify(dao).insertActualTraining(actualTraining);
    }
}
