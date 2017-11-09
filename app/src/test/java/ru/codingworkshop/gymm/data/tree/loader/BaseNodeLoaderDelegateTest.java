package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ru.codingworkshop.gymm.data.tree.loader.common.BaseNodeLoaderDelegate;
import ru.codingworkshop.gymm.data.tree.loader.datasource.NodeDataSource;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.SimpleNode;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 09.11.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class BaseNodeLoaderDelegateTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Mock private NodeDataSource<Long, String> dataSource;

    private SimpleNode node;
    private MutableLiveData<Long> liveParent;

    @Before
    public void setUp() throws Exception {
        node = new SimpleNode();
        liveParent = new MutableLiveData<Long>() {{setValue(1L);}};

        when(dataSource.getParent()).thenReturn(liveParent);
        when(dataSource.getChildren()).thenReturn(LiveDataUtil.getLive(Lists.newArrayList("foo", "bar")));
    }

    @Test
    public void loadTest() throws Exception {
        BaseNodeLoaderDelegate<Long, String> nodeLoader = new BaseNodeLoaderDelegate<>(node, dataSource);
        nodeLoader.addSource(LiveDataUtil.getLive(10), node::setAdditional);

        LiveData<SimpleNode> loaded = nodeLoader.mapLoaded(node);
        LiveTest.verifyLiveData(loaded, n -> {
            assertSame(node, n);
            return true;
        });

        assertEquals(1L, node.getParent().longValue());
        assertEquals(Lists.newArrayList("foo", "bar"), node.getChildren());
        assertEquals(10, node.getAdditional().intValue());

        liveParent.setValue(2L);
        LiveTest.verifyLiveData(loaded, n -> {
            assertEquals(2L, n.getParent().longValue());
            return true;
        });

        verify(dataSource).getParent();
        verify(dataSource).getChildren();
    }
}