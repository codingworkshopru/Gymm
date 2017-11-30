package ru.codingworkshop.gymm.data.tree.saver2;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.common.collect.Iterables;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.codingworkshop.gymm.data.tree.repositoryadapter.ChildrenAdapter;
import ru.codingworkshop.gymm.data.util.BiPredicate;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.SimpleModel;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 29.11.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ChildrenSaverTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ChildrenAdapter<SimpleModel> adapter;

    @Test
    public void save() throws Exception {
        List<SimpleModel> oldChildren = Models.createSimpleModels("foo", "bar", "baz");
        List<SimpleModel> newChildren = Models.createSimpleModels("foo", "baz", "bak");
        oldChildren.get(2).setId(10L);

        when(adapter.getChildren(1L)).thenReturn(LiveDataUtil.getLive(oldChildren));

        ChildrenSaver<SimpleModel> saver = new ChildrenSaver<>(
                adapter,
                1L,
                (m1, m2) -> m1.getSortOrder() == m2.getSortOrder());
        saver.save(newChildren);

        verify(adapter).insertChildren(argThat(actual -> Iterables.elementsEqual(actual, newChildren.subList(2, 3))));
        verify(adapter).updateChildren(argThat(actual -> Iterables.elementsEqual(actual, newChildren.subList(1, 1))));
        verify(adapter).deleteChildren(argThat(actual -> Iterables.elementsEqual(actual, oldChildren.subList(2, 3))));
    }

    @Test
    public void saveLists() throws Exception {
        List<SimpleModel> models1 = Models.createSimpleModels("foo", "bar",             "baz"); // the last one has id = 10
        models1.get(2).setId(10L);

        List<SimpleModel> models2 = Models.createSimpleModels("foo", "bak", "buk", "bum");

        BiPredicate<SimpleModel> contentsEqual = (a, b) -> Objects.equals(a.getName(), b.getName());

        // new only
        ChildrenSaver.ListsDiffResult<SimpleModel> result = ChildrenSaver.containersDiff(new ArrayList<>(), models2, contentsEqual);
        assertNull(result.getToDelete());
        assertNull(result.getToUpdate());
        assertEquals(models2, result.getToInsert());

        // old only
        result = ChildrenSaver.containersDiff(models1, new ArrayList<>(), contentsEqual);
        assertNull(result.getToInsert());
        assertNull(result.getToUpdate());
        assertEquals(models1, result.getToDelete());

        // new, old and updated

        result = ChildrenSaver.containersDiff(models1, models2, contentsEqual);
        assertEquals(models2.subList(2, 4), result.getToInsert());
        assertEquals(models1.subList(2, 3), result.getToDelete());
        assertEquals(models2.subList(1, 2), result.getToUpdate());
    }

    private <T> void assertEquals(Iterable<T> it1, Iterable<T> it2) {
        assertTrue(Iterables.elementsEqual(it1, it2));
    }
}