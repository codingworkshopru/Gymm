package ru.codingworkshop.gymm.data.tree.holder;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Радик on 20.08.2017 as part of the Gymm project.
 */

public class SimpleChildrenHolderTest {
    private SimpleChildrenHolder<Long> childrenHolder;

    @Test
    public void constructing() {
        assertFalse(childrenHolder.hasChildren());
        assertEquals(0, childrenHolder.getChildrenCount());
        childrenHolder = new SimpleChildrenHolder<>(Lists.newArrayList(1L,2L));
        assertTrue(childrenHolder.hasChildren());
        assertEquals(2, childrenHolder.getChildrenCount());
    }

    @Before
    public void setUp() throws Exception {
        childrenHolder = new SimpleChildrenHolder<>();
    }

    @Test
    public void createList() throws Exception {
        assertNotNull(childrenHolder.createList());
    }

    @Test
    public void createListWithChildren() throws Exception {
        assertEquals(3, childrenHolder.createList(Lists.newArrayList(1L,2L,3L)).size());
    }
}