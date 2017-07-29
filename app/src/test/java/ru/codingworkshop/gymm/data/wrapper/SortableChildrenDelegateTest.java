package ru.codingworkshop.gymm.data.wrapper;

import com.google.common.collect.Lists;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import ru.codingworkshop.gymm.data.entity.common.Sortable;
import ru.codingworkshop.gymm.util.SimpleModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 13.07.2017.
 */

@RunWith(JUnitParamsRunner.class)
public class SortableChildrenDelegateTest {

    @Test
    public void additionTest() {
        SortableChildrenDelegate<Sortable> delegate = new SortableChildrenDelegate<>();

        assertFalse(delegate.hasChildren());

        Sortable s = mock(Sortable.class);
        when(s.getSortOrder()).thenReturn(0);
        delegate.add(s);

        assertTrue(delegate.hasChildren());

        assertSortOrder(delegate);
    }

    @Test
    public void setChildrenTest() {
        SortableChildrenDelegate<Sortable> delegate = new SortableChildrenDelegate<>();
        Collection<Sortable> children = fill(Lists.newArrayList(3, 2, 1, 0));
        delegate.setChildren(children);
        assertSortOrder(delegate);
    }

    @Test
    @Parameters({"2", "0", "4"})
    public void removeTest(int index) {
        SortableChildrenDelegate<Sortable> delegate = new SortableChildrenDelegate<>(fill(Lists.newArrayList(2,0,3,4,1)));

        delegate.remove(index);

        assertSortOrder(delegate);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testImmutability() {
        SortableChildrenDelegate<Sortable> delegate = new SortableChildrenDelegate<>();
        delegate.getChildren().add(mock(Sortable.class));
    }

    @Test
    public void changeDelegateAndCheckChildrenList() {
        SortableChildrenDelegate<Sortable> delegate = new SortableChildrenDelegate<>();
        List<Sortable> children = delegate.getChildren();

        delegate.add(mock(Sortable.class));
        assertEquals(1, children.size());
        delegate.remove(0);
        assertEquals(0, children.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void absentDeletionTest() {
        SortableChildrenDelegate<Sortable> delegate = new SortableChildrenDelegate<>(fill(Lists.newArrayList(3,2,1,0)));
        delegate.remove(mock(Sortable.class));
    }

    @Test
    public void restoreLastRemovedTest() {
        List<Sortable> sortables = fill(Lists.newArrayList(0,1,2,3));
        SortableChildrenDelegate<Sortable> delegate = new SortableChildrenDelegate<>(sortables);

        Sortable toRemove = sortables.get(2);
        delegate.remove(toRemove);
        delegate.restoreLastRemoved();
        assertEquals(toRemove, delegate.getChildren().get(2));
        assertSortOrder(delegate);
    }

    @Test
    public void moveTest() {
        SortableChildrenDelegate<Sortable> delegate = new SortableChildrenDelegate<>(fill(Lists.newArrayList(3,1,4,0,2)));
        Sortable first = delegate.getChildren().get(0);
        Sortable middle = delegate.getChildren().get(2);
        Sortable last = delegate.getChildren().get(4);
        delegate.move(0, 2);
        assertEquals(first, delegate.getChildren().get(2));
        delegate.move(1, 4);
        assertEquals(middle, delegate.getChildren().get(4));
        delegate.move(3, 0);
        assertEquals(last, delegate.getChildren().get(0));
    }

    private void assertSortOrder(SortableChildrenDelegate<Sortable> delegate) {
        for (int i = 0; i < delegate.getChildren().size(); i++)
            assertEquals(i, delegate.getChildren().get(i).getSortOrder());
    }

    private List<Sortable> fill(List<Integer> orders) {
        List<Sortable> children = Lists.newLinkedList();
        orders.forEach(n -> {
            SimpleModel s = new SimpleModel(n, n);
            children.add(s);
        });
        return children;
    }
}
