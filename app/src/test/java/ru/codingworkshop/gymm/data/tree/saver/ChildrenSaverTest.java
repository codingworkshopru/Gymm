package ru.codingworkshop.gymm.data.tree.saver;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.SimpleModel;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created by Радик on 24.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ChildrenSaverTest {
    @Mock private ChildrenSaver.ChildrenSaverCallback<SimpleModel> callback;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        doAnswer(invocation -> {
            SimpleModel m1 = invocation.getArgument(0);
            SimpleModel m2 = invocation.getArgument(1);

            return m1.getName().equals(m2.getName());
        }).when(callback).contentsAreEqual(any(), any());

        doAnswer(invocation -> {
            SimpleModel m1 = invocation.getArgument(0);
            SimpleModel m2 = invocation.getArgument(1);

            return m1.getId() == m2.getId();
        }).when(callback).objectsAreSame(any(), any());
    }

    @Test
    public void newlyCreatedInsertion() throws Exception {
        List<SimpleModel> children = Models.createSimpleModels(1L, 2L);
        LiveData<List<SimpleModel>> oldChildrenLive = LiveDataUtil.getLive(Lists.newArrayList());
        ChildrenSaver<SimpleModel> saver = new ChildrenSaver<>(children, oldChildrenLive, callback);
        saver.save();

        verify(callback).insertChildren(argThat(toInsert -> children == toInsert));
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void deleteAll() throws Exception {
        List<SimpleModel> children = Lists.newArrayList();
        LiveData<List<SimpleModel>> oldChildrenLive = LiveDataUtil.getLive(Models.createSimpleModels(1L, 2L));
        ChildrenSaver<SimpleModel> saver = new ChildrenSaver<>(children, oldChildrenLive, callback);
        saver.save();

        verify(callback).deleteChildren(argThat(toInsert -> oldChildrenLive.getValue() == toInsert));
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void insert() throws Exception {
        List<SimpleModel> children = Models.createSimpleModels(3L, 1L, 2L);

        LiveData<List<SimpleModel>> oldChildrenLive = LiveDataUtil.getLive(Models.createSimpleModels(1L,2L));
        ChildrenSaver<SimpleModel> saver = new ChildrenSaver<>(children, oldChildrenLive, callback);
        saver.save();

        verify(callback).insertChildren(argThat(toInsert -> toInsert.size() == 1 && toInsert.iterator().next().getId() == 3L));
        verify(callback, never()).deleteChildren(any());
        verify(callback, never()).updateChildren(any());
    }

    @Test
    public void delete() throws Exception {
        List<SimpleModel> children = Models.createSimpleModels(2L, 1L);

        LiveData<List<SimpleModel>> oldChildrenLive = LiveDataUtil.getLive(Models.createSimpleModels(3L,1L,2L));
        ChildrenSaver<SimpleModel> saver = new ChildrenSaver<>(children, oldChildrenLive, callback);
        saver.save();

        verify(callback).deleteChildren(argThat(toDelete -> toDelete.size() == 1 && toDelete.iterator().next().getId() == 3L));
        verify(callback, never()).insertChildren(any());
        verify(callback, never()).updateChildren(any());
    }

    @Test
    public void update() throws Exception {
        List<SimpleModel> children = Models.createSimpleModels(2L,3L,1L);

        children.get(0).setName("simple model 3");
        children.get(1).setName("simple model 2");

        LiveData<List<SimpleModel>> oldChildrenLive = LiveDataUtil.getLive(Models.createSimpleModels(3L,1L,2L));
        ChildrenSaver<SimpleModel> saver = new ChildrenSaver<>(children, oldChildrenLive, callback);
        saver.save();

        verify(callback).updateChildren(argThat(toUpdate -> {
            assertEquals(2, toUpdate.size());
            Map<Long, SimpleModel> m = Maps.uniqueIndex(toUpdate, Model::getId);
            assertEquals("simple model 3", m.get(2L).getName());
            assertEquals("simple model 2", m.get(3L).getName());
            return true;
        }));
        verify(callback, never()).insertChildren(any());
        verify(callback, never()).deleteChildren(any());
    }

    @Test
    public void createUpdateDelete() throws Exception {
        LiveData<List<SimpleModel>> oldChildren = LiveDataUtil.getLive(Models.createSimpleModels(1L,2L,3L,4L));
        List<SimpleModel> children = Models.createSimpleModels(6L,3L,4L);
        children.get(0).setName("foo");
        children.get(1).setName("bar");

        ChildrenSaver<SimpleModel> saver = new ChildrenSaver<>(children, oldChildren, callback);
        saver.save();

        verify(callback).insertChildren(argThat(i -> i.size() == 1 && i.iterator().next().getId() == 6L));
        verify(callback).updateChildren(argThat(u -> u.size() == 1 && u.iterator().next().getId() == 3L));
        verify(callback).deleteChildren(argThat(d -> d.size() == 2 && d.stream().map(Model::getId).collect(Collectors.toList()).equals(Lists.newArrayList(1L,2L))));
    }
}