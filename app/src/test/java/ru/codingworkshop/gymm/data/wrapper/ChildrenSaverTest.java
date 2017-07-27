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

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.entity.common.Sortable;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;

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
        List<SimpleSortable> oldChildren = Lists.newArrayList(1L, 2L, 3L).stream().map(id -> new SimpleSortable(id, id.intValue())).collect(Collectors.toList());
        List<SimpleSortable> newChildren = Lists.newArrayList(2L, 3L, 4L).stream().map(id -> new SimpleSortable(id, id.intValue())).collect(Collectors.toList());
        newChildren.get(1).setSortOrder(10);

        LiveData<List<SimpleSortable>> oldChildrenLive = LiveDataUtil.getLive(oldChildren);

        ChildrenSaver<SimpleSortable> saver = new ChildrenSaver<SimpleSortable>(oldChildrenLive, newChildren) {
            @Override
            public void update(List<SimpleSortable> toUpdate) {
                assertEquals(1, toUpdate.size());
                assertEquals(3L, toUpdate.get(0).getId());
            }

            @Override
            public void delete(List<SimpleSortable> toDelete) {
                assertEquals(1, toDelete.size());
                assertEquals(1L, toDelete.get(0).getId());
            }

            @Override
            public void insert(List<SimpleSortable> toInsert) {
                assertEquals(1, toInsert.size());
                assertEquals(4L, toInsert.get(0).getId());
            }
        };

        saver.save();

        assertFalse("Observer hasn't been removed", oldChildrenLive.hasObservers());
    }

    @Test
    public void saveWhenOnlyNew() {
        LiveData<List<SimpleSortable>> oldChildrenLive = LiveDataUtil.getLive(Lists.newArrayList());
        List<SimpleSortable> newChildren = Lists.newArrayList(1L, 2L, 3L).stream().map(id -> new SimpleSortable(id, id.intValue())).collect(Collectors.toList());

        ChildrenSaver<SimpleSortable> saver = new ChildrenSaver<SimpleSortable>(oldChildrenLive, newChildren) {
            @Override
            public void update(List<SimpleSortable> toUpdate) {
                assertTrue(toUpdate.isEmpty());
            }

            @Override
            public void delete(List<SimpleSortable> toDelete) {
                assertTrue(toDelete.isEmpty());
            }

            @Override
            public void insert(List<SimpleSortable> toInsert) {
                assertEquals(newChildren, toInsert);
            }
        };

        saver.save();
    }

    @Test
    public void saveWhenOnlyOld() {
        List<SimpleSortable> oldChildren = Lists.newArrayList(1L, 2L, 3L).stream().map(id -> new SimpleSortable(id, id.intValue())).collect(Collectors.toList());
        LiveData<List<SimpleSortable>> oldChildrenLive = LiveDataUtil.getLive(oldChildren);
        List<SimpleSortable> newChildren = Lists.newArrayList();

        ChildrenSaver<SimpleSortable> saver = new ChildrenSaver<SimpleSortable>(oldChildrenLive, newChildren) {
            @Override
            public void update(List<SimpleSortable> toUpdate) {
                assertTrue(toUpdate.isEmpty());
            }

            @Override
            public void delete(List<SimpleSortable> toDelete) {
                assertEquals(oldChildren, toDelete);
            }

            @Override
            public void insert(List<SimpleSortable> toInsert) {
                assertTrue(toInsert.isEmpty());
            }
        };

        saver.save();
    }

    @Test
    public void saveWhenOnlyUpdated() {
        List<SimpleSortable> oldChildren = Lists.newArrayList(1L, 2L, 3L).stream().map(id -> new SimpleSortable(id, id.intValue())).collect(Collectors.toList());
        LiveData<List<SimpleSortable>> oldChildrenLive = LiveDataUtil.getLive(oldChildren);
        List<SimpleSortable> newChildren = Lists.newArrayList(1L, 2L, 3L).stream().map(id -> new SimpleSortable(id, id.intValue() + 1)).collect(Collectors.toList());

        ChildrenSaver<SimpleSortable> saver = new ChildrenSaver<SimpleSortable>(oldChildrenLive, newChildren) {
            @Override
            public void update(List<SimpleSortable> toUpdate) {
                assertEquals(newChildren, toUpdate);
            }

            @Override
            public void delete(List<SimpleSortable> toDelete) {
                assertTrue(toDelete.isEmpty());
            }

            @Override
            public void insert(List<SimpleSortable> toInsert) {
                assertTrue(toInsert.isEmpty());
            }
        };

        saver.save();
    }

    private static final class SimpleSortable implements Model, Sortable {
        private long id;
        private int sortOrder;

        private SimpleSortable(long id, int sortOrder) {
            this.id = id;
            this.sortOrder = sortOrder;
        }

        @Override
        public long getId() {
            return id;
        }

        @Override
        public void setId(long id) {
            this.id = id;
        }

        @Override
        public int getSortOrder() {
            return sortOrder;
        }

        @Override
        public void setSortOrder(int sortOrder) {
            this.sortOrder = sortOrder;
        }
    }
}
