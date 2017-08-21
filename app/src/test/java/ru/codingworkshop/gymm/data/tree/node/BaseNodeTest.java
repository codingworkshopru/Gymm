package ru.codingworkshop.gymm.data.tree.node;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import ru.codingworkshop.gymm.data.tree.holder.ChildrenHolder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class BaseNodeTest {
    @Mock private ChildrenHolder<String> childrenHolder;
    private BaseNode<Long, String> node;

    @Before
    public void setUp() throws Exception {
        node = new BaseNode<Long, String>(childrenHolder) {};
    }

    @Test
    public void getChildrenDelegate() throws Exception {
        assertEquals(childrenHolder, node.getChildrenDelegate());
    }

    @Test
    public void parentAccessors() throws Exception {
        node.setParent(1L);
        assertEquals(1L, node.getParent().longValue());
    }

    @Test
    public void getChildren() throws Exception {
        node.getChildren();
        verify(childrenHolder).getChildren();
    }

    @Test
    public void setChildren() throws Exception {
        final ArrayList<String> foo = Lists.newArrayList("foo");
        node.setChildren(foo);
        verify(childrenHolder).setChildren(foo);
    }

    @Test
    public void addChild() throws Exception {
        node.addChild("foo");
        verify(childrenHolder).addChild("foo");
    }

    @Test
    public void removeChildByIndex() throws Exception {
        node.removeChild(0);
        verify(childrenHolder).removeChild(0);
    }

    @Test
    public void removeChildByReference() throws Exception {
        node.removeChild("foo");
        verify(childrenHolder).removeChild("foo");
    }

    @Test
    public void moveChild() throws Exception {
        node.moveChild(0, 1);
        verify(childrenHolder).moveChild(0, 1);
    }

}