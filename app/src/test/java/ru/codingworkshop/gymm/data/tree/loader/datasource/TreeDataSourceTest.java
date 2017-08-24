package ru.codingworkshop.gymm.data.tree.loader.datasource;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import com.google.common.collect.Lists;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import ru.codingworkshop.gymm.data.util.LiveDataUtil;

import static org.junit.Assert.assertEquals;

/**
 * Created by Радик on 24.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class TreeDataSourceTest {

    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void test() throws Exception {
        TreeDataSource<Long, String, Boolean> dataSource = new TreeDataSource<>();
        final LiveData<List<Boolean>> live = LiveDataUtil.getLive(Lists.newArrayList(true, false));
        dataSource.setGrandchildren(live);
        assertEquals(live, dataSource.getGrandchildren());
    }
}