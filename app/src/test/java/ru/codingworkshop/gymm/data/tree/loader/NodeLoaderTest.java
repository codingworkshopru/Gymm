package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.SimpleNode;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */

public class NodeLoaderTest {
    private SimpleNode node;
    private NodeLoader<Long, String> loader;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        node = new SimpleNode();
        loader = new NodeLoader<Long, String>(node) {
            @Override
            void loadAdditional(SetAndRemove setAndRemove) {
                setAndRemove.ok(LiveDataUtil.getLive(-5), node::setAdditional);
            }
        };
        loader.setParent(LiveDataUtil.getLive(1L));
        loader.setChildren(LiveDataUtil.getLive(Lists.newArrayList("foo", "bar")));
    }


    @Test
    public void load() throws Exception {
        LiveTest.verifyLiveData(loader.load(), b -> b);
        assertEquals(1L, node.getParent().longValue());
        assertEquals(Lists.newArrayList("foo", "bar"), node.getChildren());
        assertEquals(-5, node.getAdditional().intValue());
    }

    @Test
    public void getNode() throws Exception {
        assertEquals(node, loader.getNode());
    }
}
