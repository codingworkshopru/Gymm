package ru.codingworkshop.gymm.data.tree.saver;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.common.collect.Iterables;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.codingworkshop.gymm.data.tree.repositoryadapter.ModelQueryAdapter;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ModelsAlterAdapter;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.SimpleModel;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 29.11.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ChildrenSaverTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ModelQueryAdapter<List<SimpleModel>> queryAdapter;
    @Mock private ModelsAlterAdapter<SimpleModel> alterAdapter;

    @Test
    public void save() {
        List<SimpleModel> oldChildren = Models.createSimpleModels("foo", "bar", "baz");
        List<SimpleModel> newChildren = Models.createSimpleModels("foo", "baz", "bak");
        oldChildren.get(2).setId(10L);

        when(queryAdapter.query(1L)).thenReturn(oldChildren);

        ChildrenSaver<SimpleModel> saver = new ChildrenSaver<>(queryAdapter, alterAdapter, 1L);
        saver.save(newChildren);

        verify(alterAdapter).insert(argThat(actual -> Iterables.elementsEqual(actual, newChildren.subList(2, 3))));
        verify(alterAdapter).update(argThat(actual -> Iterables.elementsEqual(actual, newChildren.subList(1, 2))));
        verify(alterAdapter).delete(argThat(actual -> Iterables.elementsEqual(actual, oldChildren.subList(2, 3))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveWithInvalidParentId() {
        List<SimpleModel> newChildren = Models.createSimpleModels("foo");

        ChildrenSaver<SimpleModel> saver = new ChildrenSaver<>(queryAdapter, alterAdapter, 0L);
        saver.save(newChildren);
    }

    @Test
    public void containersDiffTest() {
        List<SimpleModel> models1 = Models.createSimpleModels("foo", "bar",             "baz"); // the last one has id = 10
        models1.get(2).setId(10L);

        List<SimpleModel> models2 = Models.createSimpleModels("foo", "bak", "buk", "bum");
        models2.get(2).setId(0L);
        models2.get(3).setId(0L);

        // new only
        ChildrenSaver.ContainersDiffResult<SimpleModel> result = ChildrenSaver.containersDiff(new ArrayList<>(), models2);
        assertEquals(Collections.emptyList(), result.getToDelete());
        assertEquals(Collections.emptyList(), result.getToUpdate());
        assertEquals(models2, result.getToInsert());

        // old only
        result = ChildrenSaver.containersDiff(models1, new ArrayList<>());
        assertEquals(Collections.emptyList(), result.getToInsert());
        assertEquals(Collections.emptyList(), result.getToUpdate());
        assertEquals(models1, result.getToDelete());

        // new, old and updated
        result = ChildrenSaver.containersDiff(models1, models2);
        assertEquals(models2.subList(2, 4), result.getToInsert());
        assertEquals(models1.subList(2, 3), result.getToDelete());
        assertEquals(models2.subList(1, 2), result.getToUpdate());
    }

    private <T> void assertEquals(Iterable<T> it1, Iterable<T> it2) {
        assertTrue(Iterables.elementsEqual(it1, it2));
    }
}