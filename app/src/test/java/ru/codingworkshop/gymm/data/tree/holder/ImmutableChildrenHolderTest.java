package ru.codingworkshop.gymm.data.tree.holder;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Радик on 20.08.2017 as part of the Gymm project.
 */
public class ImmutableChildrenHolderTest {
    private ImmutableChildrenHolder<Long> childrenHolder;

    @Before
    public void init() {
        childrenHolder = new ImmutableChildrenHolder<>(Lists.newArrayList(1L,2L,3L));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setChildren() throws Exception {
        childrenHolder.setChildren(Lists.newArrayList(4L));
    }

    @Test
    public void getChildren() throws Exception {
        assertEquals(Lists.newArrayList(1L,2L,3L), childrenHolder.getChildren());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addChild() throws Exception {
        childrenHolder.addChild(4L);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeChildByIndex() throws Exception {
        childrenHolder.removeChild(0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeChildByReference() throws Exception {
        childrenHolder.removeChild(1L);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void moveChild() throws Exception {
        childrenHolder.moveChild(0, 1);
    }

}