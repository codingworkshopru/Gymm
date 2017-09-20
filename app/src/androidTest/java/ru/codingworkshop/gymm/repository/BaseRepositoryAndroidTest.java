package ru.codingworkshop.gymm.repository;

import android.arch.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.Executors;

import ru.codingworkshop.gymm.util.DummyDao;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.SimpleModel;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 20.09.2017 as part of the Gymm project.
 */

public class BaseRepositoryAndroidTest {
    @Mock private DummyDao<SimpleModel> dao;
    private BaseRepository repository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        repository = new BaseRepository(Executors.newSingleThreadExecutor());
    }

    @Test
    public void insertWithResultTest() throws Exception {
        SimpleModel model = Models.createSimpleModels(0L).get(0);

        when(dao.insert(model)).thenReturn(1L);

        LiveData<Long> liveId = repository.insertWithResult(model, dao::insert);

        assertEquals(1L, LiveTest.getValue(liveId).longValue());
        verify(dao).insert(model);
    }
}
