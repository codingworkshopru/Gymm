package ru.codingworkshop.gymm.data.wrapper;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import com.google.common.collect.Lists;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.stream.Collectors;

import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.SimpleModel;
import ru.codingworkshop.gymm.util.SimpleWrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by Радик on 06.08.2017 as part of the Gymm project.
 */

@RunWith(JUnit4.class)
public class BaseLoaderTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private static abstract class SimpleLoader extends BaseLoader<SimpleWrapper, Long, SimpleModel> {
        SimpleLoader(SimpleWrapper wrapper) {
            super(wrapper);
        }

        @Override
        protected void addSources() {
            super.addSources();
            addBool();
        }

        void addBool() {
            addSource(LiveDataUtil.getLive(true), SimpleWrapper::setBool);
        }
    }

    @Test
    public void test() {
        SimpleLoader loader = new SimpleLoader(new SimpleWrapper()) {

            @Override
            protected LiveData<Long> getLiveRoot() {
                return LiveDataUtil.getLive(1L);
            }

            @Override
            protected LiveData<List<SimpleModel>> getLiveChildren() {
                return LiveDataUtil.getLive(Models.createSimpleModels("foo", "bar", "baz"));
            }
        };

        LiveData<SimpleWrapper> liveWrapper = loader.load();
        LiveTest.verifyLiveData(liveWrapper, w -> {
            assertEquals(1L, w.getRoot().longValue());
            assertEquals(Lists.newArrayList("foo", "bar", "baz"), w.getChildren().stream().map(SimpleModel::getName).collect(Collectors.toList()));
            assertTrue(w.getBool());
            return true;
        });
    }

    @Test
    public void test2() {
        SimpleLoader loader = new SimpleLoader(new SimpleWrapper()) {

            @Override
            protected LiveData<Long> getLiveRoot() {
                return LiveDataUtil.getLive(2L);
            }

            @Override
            protected LiveData<List<SimpleModel>> getLiveChildren(Long root) {
                return LiveDataUtil.getLive(Models.createSimpleModels("foo" + root, "bar" + root, "baz" + root));
            }
        };

        LiveData<SimpleWrapper> liveWrapper = loader.load();
        LiveTest.verifyLiveData(liveWrapper, w -> {
            assertEquals(2L, w.getRoot().longValue());
            assertEquals(Lists.newArrayList("foo2", "bar2", "baz2"), w.getChildren().stream().map(SimpleModel::getName).collect(Collectors.toList()));
            assertTrue(w.getBool());
            return true;
        });
    }

    @Test
    public void runIfRootNull() {
        Runnable runnable = mock(Runnable.class);
        SimpleLoader loader = new SimpleLoader(new SimpleWrapper()) {
            @Override
            protected void runIfRootAbsent() {
                runnable.run();
            }

            @Override
            protected LiveData<Long> getLiveRoot() {
                return LiveDataUtil.getLive(null);
            }

            @Override
            protected LiveData<List<SimpleModel>> getLiveChildren() {
                return LiveDataUtil.getLive(Models.createSimpleModels("foo", "bar"));
            }
        };

        LiveTest.verifyLiveData(loader.load(), w -> w.getRoot() == null);

        verify(runnable).run();
    }
}