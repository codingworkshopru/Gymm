package ru.codingworkshop.gymm.data.tree.holder;

import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.common.Sortable;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


/**
 * Created by Радик on 20.08.2017 as part of the Gymm project.
 */
public class SortableRestoreChildrenHolderTest {
    private static final class SimpleSortable implements Sortable {
        private int sortOrder;

        public SimpleSortable() {
        }

        public SimpleSortable(int sortOrder) {
            this.sortOrder = sortOrder;
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

    private SortableRestoreChildrenHolder<SimpleSortable> childrenHolder;

    @Before
    public void setUp() throws Exception {
        childrenHolder = new SortableRestoreChildrenHolder<>();
    }

    @Test
    public void getChildren() throws Exception {
        assertTrue(childrenHolder.getChildren().isEmpty());
    }

    @Test
    public void setChildren() throws Exception {
        List<SimpleSortable> sortables = Lists.newArrayList(new SimpleSortable());
        childrenHolder.setChildren(sortables);
    }

    @Test
    public void addChild() throws Exception {
        childrenHolder.addChild(new SimpleSortable());
        childrenHolder.addChild(new SimpleSortable());

        assertEquals(2, childrenHolder.getChildren().size());
    }

    @Test
    public void removeChildByIndex() throws Exception {
        childrenHolder.addChild(new SimpleSortable());
        childrenHolder.addChild(new SimpleSortable());

        childrenHolder.removeChild(0);
        assertEquals(1, childrenHolder.getChildren().size());
    }

    @Test
    public void removeChildByReference() throws Exception {
        final SimpleSortable child = new SimpleSortable();
        childrenHolder.addChild(child);
        childrenHolder.addChild(new SimpleSortable());

        childrenHolder.removeChild(child);
        assertEquals(1, childrenHolder.getChildren().size());
    }

    @Test
    public void moveChild() throws Exception {
        for (int i = 0; i < 3; i++) {
            childrenHolder.addChild(new SimpleSortable());
        }

        childrenHolder.moveChild(0, 2);
    }

    @Test
    public void restoreLastRemoved() throws Exception {
        childrenHolder.addChild(new SimpleSortable());
        childrenHolder.removeChild(0);
        childrenHolder.restoreLastRemoved();
        assertTrue(childrenHolder.hasChildren());
    }

    @After
    public void checkSortOrder() {
        for (SimpleSortable s : childrenHolder.getChildren()) {
            assertEquals(childrenHolder.getChildren().indexOf(s), s.getSortOrder());
        }
    }
}