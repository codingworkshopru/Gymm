package ru.codingworkshop.gymm.data.wrapper;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import com.google.common.collect.Lists;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.stream.Collectors;

import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.util.SimpleModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Радик on 27.07.2017.
 */

@RunWith(JUnit4.class)
public class ChildrenSaverTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void simpleSave() {
        List<SimpleModel> oldChildren = Lists.newArrayList(1L, 2L, 3L).stream().map(id -> new SimpleModel(id, id.intValue())).collect(Collectors.toList());
        List<SimpleModel> newChildren = Lists.newArrayList(2L, 3L, 4L).stream().map(id -> new SimpleModel(id, id.intValue())).collect(Collectors.toList());
        newChildren.get(1).setSortOrder(10);

        LiveData<List<SimpleModel>> oldChildrenLive = LiveDataUtil.getLive(oldChildren);

        ChildrenSaver<SimpleModel> saver = new ChildrenSaver<SimpleModel>(oldChildrenLive, newChildren) {
            @Override
            public void update(List<SimpleModel> toUpdate) {
                assertEquals(1, toUpdate.size());
                assertEquals(3L, toUpdate.get(0).getId());
            }

            @Override
            public void delete(List<SimpleModel> toDelete) {
                assertEquals(1, toDelete.size());
                assertEquals(1L, toDelete.get(0).getId());
            }

            @Override
            public void insert(List<SimpleModel> toInsert) {
                assertEquals(1, toInsert.size());
                assertEquals(4L, toInsert.get(0).getId());
            }
        };

        saver.save();

        assertFalse("Observer hasn't been removed", oldChildrenLive.hasObservers());
    }

    @Test
    public void saveWhenOnlyNew() {
        LiveData<List<SimpleModel>> oldChildrenLive = LiveDataUtil.getLive(Lists.newArrayList());
        List<SimpleModel> newChildren = Lists.newArrayList(1L, 2L, 3L).stream().map(id -> new SimpleModel(id, id.intValue())).collect(Collectors.toList());

        ChildrenSaver<SimpleModel> saver = new ChildrenSaver<SimpleModel>(oldChildrenLive, newChildren) {
            @Override
            public void update(List<SimpleModel> toUpdate) {
                assertTrue(toUpdate.isEmpty());
            }

            @Override
            public void delete(List<SimpleModel> toDelete) {
                assertTrue(toDelete.isEmpty());
            }

            @Override
            public void insert(List<SimpleModel> toInsert) {
                assertEquals(newChildren, toInsert);
            }
        };

        saver.save();
    }

    @Test
    public void saveWhenOnlyOld() {
        List<SimpleModel> oldChildren = Lists.newArrayList(1L, 2L, 3L).stream().map(id -> new SimpleModel(id, id.intValue())).collect(Collectors.toList());
        LiveData<List<SimpleModel>> oldChildrenLive = LiveDataUtil.getLive(oldChildren);
        List<SimpleModel> newChildren = Lists.newArrayList();

        ChildrenSaver<SimpleModel> saver = new ChildrenSaver<SimpleModel>(oldChildrenLive, newChildren) {
            @Override
            public void update(List<SimpleModel> toUpdate) {
                assertTrue(toUpdate.isEmpty());
            }

            @Override
            public void delete(List<SimpleModel> toDelete) {
                assertEquals(oldChildren, toDelete);
            }

            @Override
            public void insert(List<SimpleModel> toInsert) {
                assertTrue(toInsert.isEmpty());
            }
        };

        saver.save();
    }

    @Test
    public void saveWhenOnlyUpdated() {
        List<SimpleModel> oldChildren = Lists.newArrayList(1L, 2L, 3L).stream().map(id -> new SimpleModel(id, id.intValue())).collect(Collectors.toList());
        LiveData<List<SimpleModel>> oldChildrenLive = LiveDataUtil.getLive(oldChildren);
        List<SimpleModel> newChildren = Lists.newArrayList(1L, 2L, 3L).stream().map(id -> new SimpleModel(id, id.intValue() + 1)).collect(Collectors.toList());

        ChildrenSaver<SimpleModel> saver = new ChildrenSaver<SimpleModel>(oldChildrenLive, newChildren) {
            @Override
            public void update(List<SimpleModel> toUpdate) {
                assertEquals(newChildren, toUpdate);
            }

            @Override
            public void delete(List<SimpleModel> toDelete) {
                assertTrue(toDelete.isEmpty());
            }

            @Override
            public void insert(List<SimpleModel> toInsert) {
                assertTrue(toInsert.isEmpty());
            }
        };

        saver.save();
    }

}
