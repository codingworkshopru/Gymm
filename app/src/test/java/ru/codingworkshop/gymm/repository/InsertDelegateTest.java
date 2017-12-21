package ru.codingworkshop.gymm.repository;

import com.google.common.collect.Lists;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import ru.codingworkshop.gymm.util.DummyDao;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.SimpleModel;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InsertDelegateTest {
    @Mock private DummyDao<SimpleModel> dao;

    @Test
    public void insert() {
        when(dao.insert(any(SimpleModel.class))).thenReturn(1L);
        SimpleModel model = new SimpleModel(0L, "foo");
        long id = new InsertDelegate().insert(model, dao::insert);
        assertEquals(1L, id);
        assertEquals(1L, model.getId());
    }

    @Test
    public void insertCollection() {
        List<Long> expectedIds = Arrays.asList(1L, 2L);
        when(dao.insert(anyCollection())).thenReturn(expectedIds);
        List<SimpleModel> models = Models.createSimpleModels(0L, 0L);
        List<Long> ids = new InsertDelegate().insert(models, dao::insert);
        assertEquals(expectedIds, ids);
        assertEquals(expectedIds, Lists.transform(models, SimpleModel::getId));
    }
}