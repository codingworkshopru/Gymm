package ru.codingworkshop.gymm.data.tree.node;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import ru.codingworkshop.gymm.data.tree.holder.ImmutableChildrenHolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ImmutableBaseNodeTest {
    private ImmutableBaseNode<Long, String> node;

    @Before
    public void setUp() throws Exception {
        node = new ImmutableBaseNode<>();
        node.setParent(1L);
        node.setChildren(Collections.singletonList("foo"));
    }

    @Test
    public void getChildrenDelegate() {
        assertTrue(node.getChildrenDelegate() instanceof ImmutableChildrenHolder);
    }

    @Test
    public void getParent() {
        assertEquals(1L, node.getParent().longValue());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setParent() {
        node.setParent(2L);
    }

    @Test
    public void getChildren() {
        assertEquals(Collections.singletonList("foo"), node.getChildren());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setChildren() {
        node.setChildren(new ArrayList<>());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addChild() {
        node.addChild("foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeChild() {
        node.removeChild(0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeChild1() {
        node.removeChild("foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void moveChild() {
        node.moveChild(0, 1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void replaceChild() {
        node.replaceChild(0, "foo");
    }
}