package ru.codingworkshop.gymm.data.tree.holder;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */
public class ListChildrenHolderTest {
    private ListChildrenHolder<Long> childrenHolder;

    @Before
    public void setUp() throws Exception {
        childrenHolder = new ListChildrenHolder<Long>() {
            @Override
            List<Long> createList() {
                return Lists.newLinkedList();
            }

            @Override
            List<Long> createList(List<Long> children) {
                return Lists.newLinkedList(children);
            }
        };
    }

    @Test
    public void constructing() {
        assertFalse(childrenHolder.hasChildren());
        childrenHolder = new SimpleChildrenHolder<>(Lists.newArrayList(1L,2L));
        assertTrue(childrenHolder.hasChildren());
    }

    @Test
    public void setChildren() throws Exception {
        childrenHolder.setChildren(Lists.newArrayList(1L));
        assertTrue(childrenHolder.hasChildren());
    }

    @Test
    public void getChildren() throws Exception {
        List<Long> children = Lists.newArrayList(1L);
        childrenHolder.setChildren(children);
        assertEquals(children, childrenHolder.getChildren());
    }

    @Test
    public void addChild() throws Exception {
        childrenHolder.addChild(1L);
        assertTrue(childrenHolder.hasChildren());
    }

    @Test
    public void removeChildByIndex() throws Exception {
        childrenHolder.setChildren(Lists.newArrayList(1L));
        childrenHolder.removeChild(0);
        assertFalse(childrenHolder.hasChildren());
    }

    @Test
    public void removeChildByReference() throws Exception {
        childrenHolder.setChildren(Lists.newArrayList(1L));
        childrenHolder.removeChild(1L);
    }

    @Test
    public void moveChild() throws Exception {
        childrenHolder.setChildren(Lists.newArrayList(1L, 2L));
        childrenHolder.moveChild(0, 1);
        assertEquals(Lists.newArrayList(2L,1L), childrenHolder.getChildren());
    }
}