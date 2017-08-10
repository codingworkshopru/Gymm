package ru.codingworkshop.gymm.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.data.util.LongSupplier;
import ru.codingworkshop.gymm.util.DummyDao;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.ModelsFixture;
import ru.codingworkshop.gymm.util.SimpleModel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 28.07.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class BaseRepositoryTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private DummyDao<SimpleModel> dao;
    @Mock private BaseRepository.ATask asyncTask;
    private BaseRepository repository;

    @Before
    public void init() {
        doAnswer(invocation -> {
            LongSupplier supplier = invocation.getArgument(0);
            final LiveData<Long> liveId = LiveDataUtil.getLive(supplier.get());
            when(asyncTask.getLiveResult()).thenReturn(liveId);
            return null;
        }).when(asyncTask).run(any(LongSupplier.class));
        repository = new BaseRepository(Runnable::run, asyncTask);
    }

    @Test
    public void insertTest() {
        SimpleModel model = new SimpleModel(0L, "foo");
        List<SimpleModel> models = ModelsFixture.createSimpleModels(0L, 0L, 0L);

        when(dao.insert(model)).thenReturn(1L);
        final ArrayList<Long> ids = Lists.newArrayList(1L, 2L, 3L);
        when(dao.insert(models)).thenReturn(ids);

        repository.insert(model, dao::insert, BaseRepositoryTest::checkName);
        repository.insert(models, dao::insert, BaseRepositoryTest::checkName);

        assertEquals(1L, model.getId());
        assertEquals(ids, models.stream().map(Model::getId).collect(Collectors.toList()));

        verify(dao).insert(model);
        verify(dao).insert(models);
    }

    @Test
    public void asyncInsertTest() {
        SimpleModel model = new SimpleModel(0L, "foo");
        when(dao.insert(model)).thenReturn(2L);
        LiveData<Long> liveId = repository.insert(model, dao::insert, BaseRepositoryTest::checkName);
        LiveTest.verifyLiveData(liveId, id -> id == 2L);
        verify(dao).insert(model);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertWithoutNameTest() {
        SimpleModel model = new SimpleModel(1L, null);
        repository.insert(model, dao::insert, BaseRepositoryTest::checkName);
    }

    @Test(expected = IllegalStateException.class)
    public void insertFailTest() {
        SimpleModel m = new SimpleModel(1L, "foo");
        when(dao.insert(m)).thenReturn(-1L);
        repository.insert(m, dao::insert, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertCollectionWithoutNameTest() {
        List<SimpleModel> models = ModelsFixture.createSimpleModels("foo", null, "bar");
        repository.insert(Lists.newArrayList(models), dao::insert, BaseRepositoryTest::checkName);
    }

    @Test(expected = IllegalStateException.class)
    public void insertCollectionFailTest() {
        List<SimpleModel> ms = ModelsFixture.createSimpleModels("foo", "bar", "baz");
        when(dao.insert(ms)).thenReturn(Lists.newArrayList(-1L, 1L, 2L));
        repository.insert(ms, dao::insert, BaseRepositoryTest::checkName);
    }

    @Test
    public void updateTest() {
        SimpleModel model = new SimpleModel(1L, "foo");
        List<SimpleModel> models = ModelsFixture.createSimpleModels("foo", "bar", "baz");

        when(dao.update(model)).thenReturn(1);
        when(dao.update(models)).thenReturn(3);

        repository.update(model, dao::update, BaseRepositoryTest::checkName);
        repository.update(models, dao::update, BaseRepositoryTest::checkName);

        verify(dao).update(model);
        verify(dao).update(models);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateWithoutName() {
        SimpleModel model = new SimpleModel(1L, null);
        repository.update(model, dao::update, BaseRepositoryTest::checkName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateCollectionWithoutName() {
        List<SimpleModel> models = ModelsFixture.createSimpleModels("foo", "bar", null);
        repository.update(models, dao::update, BaseRepositoryTest::checkName);
    }

    @Test
    public void deleteTest() {
        SimpleModel model = new SimpleModel(1L, "foo");
        List<SimpleModel> models = ModelsFixture.createSimpleModels("foo", "bar", "baz");

        when(dao.delete(model)).thenReturn(1);
        when(dao.delete(models)).thenReturn(3);

        repository.delete(model, dao::delete);
        repository.delete(models, dao::delete);

        verify(dao).delete(model);
        verify(dao).delete(models);
    }

    private static void checkName(SimpleModel m) {
        Preconditions.checkArgument(m.getName() != null, "Model must be named");
    }
}
