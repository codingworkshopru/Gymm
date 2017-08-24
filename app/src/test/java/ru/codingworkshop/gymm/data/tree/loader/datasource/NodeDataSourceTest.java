package ru.codingworkshop.gymm.data.tree.loader.datasource;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import ru.codingworkshop.gymm.data.util.LiveDataUtil;

import static org.junit.Assert.assertEquals;

/**
 * Created by Радик on 24.08.2017 as part of the Gymm project.
 */
public class NodeDataSourceTest {
    private NodeDataSource<Long, String> dataSource;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        dataSource = new NodeDataSource<Long, String>() {};
    }

    @Test
    public void parentAccessorsTest() throws Exception {
        final LiveData<Long> live = LiveDataUtil.getLive(1L);
        dataSource.setParent(live);
        assertEquals(live, dataSource.getParent());
    }

    @Test
    public void childrenAccessorsTest() throws Exception {
        final LiveData<List<String>> live = LiveDataUtil.getLive(Lists.newArrayList("foo", "bar"));
        dataSource.setChildren(live);
        assertEquals(live, dataSource.getChildren());
    }

}