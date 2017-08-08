package ru.codingworkshop.gymm.data.wrapper;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.google.common.collect.Lists;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.stream.Collectors;

import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.ModelsFixture;
import ru.codingworkshop.gymm.util.SimpleModel;
import ru.codingworkshop.gymm.util.SimpleWrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by Радик on 22.06.2017.
 */

@RunWith(JUnit4.class)
public class LoaderTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void loadValueToWrapper() {
        Loader<SimpleWrapper> loader = new Loader<>(SimpleWrapper::new);
        MutableLiveData<Long> liveData = new MutableLiveData<>();
        liveData.setValue(1L);
        loader.addSource(liveData, SimpleWrapper::setRoot);
        LiveData<SimpleWrapper> resultWrapper = loader.load();

        Observer<SimpleWrapper> observer = mock(Observer.class);
        resultWrapper.observeForever(observer);
        verify(observer).onChanged(argThat(returned -> returned.getRoot() == 1L));

        liveData.setValue(2L);
        verify(observer, times(2)).onChanged(argThat(returned -> returned.getRoot() == 2L));
    }

    @Test
    public void runIfSourceIsNull() {
        MutableLiveData<Long> longData = new MutableLiveData<>();
        longData.setValue(null);

        MutableLiveData<String> stringData = new MutableLiveData<>();
        stringData.setValue("foo");

        Loader<SimpleWrapper> loader = new Loader<>(SimpleWrapper::new);
        Runnable runnable = mock(Runnable.class);
        loader.addSource(longData, SimpleWrapper::setRoot, runnable);
        loader.addDependentSource(longData, l -> LiveDataUtil.getLive(ModelsFixture.createSimpleModels("bar", "baz")), SimpleWrapper::setChildren);

        LiveData<SimpleWrapper> liveWrapper = loader.load();
        LiveTest.verifyLiveData(liveWrapper, w -> w.getRoot() == null);

        verify(runnable).run();
    }

    @Test
    public void loadValuesToWrapperConsistently() {
        MutableLiveData<Long> liveLong = new MutableLiveData<>();
        liveLong.setValue(1L);

        Loader<SimpleWrapper> loader = new Loader<>(SimpleWrapper::new);
        loader.addSource(liveLong, SimpleWrapper::setRoot);
        loader.addDependentSource(liveLong, this::getLiveChildren, SimpleWrapper::setChildren);
        loader.addDependentSource(liveLong, l -> {
            MutableLiveData<Boolean> liveBool = new MutableLiveData<>();
            liveBool.setValue(l == 1L);
            return liveBool;
        }, SimpleWrapper::setBool);

        Observer<SimpleWrapper> observer = mock(Observer.class);
        loader.load().observeForever(observer);
        verify(observer).onChanged(argThat(
                wrapper -> {
                    assertEquals(1L, wrapper.getRoot().longValue());
                    assertEquals(Lists.newArrayList("foo1", "bar1", "baz1"), wrapper.getChildren().stream().map(SimpleModel::getName).collect(Collectors.toList()));
                    assertTrue(wrapper.getBool());
                    return true;
                }
        ));

        liveLong.setValue(2L);

        verify(observer, times(4)).onChanged(argThat(
                wrapper -> {
                    assertEquals(2L, wrapper.getRoot().longValue());
                    assertEquals(Lists.newArrayList("foo2", "bar2", "baz2"), wrapper.getChildren().stream().map(SimpleModel::getName).collect(Collectors.toList()));
                    assertFalse(wrapper.getBool());
                    return true;
                }
        ));
    }

    @Test
    public void loadingNullValueFromDependentSource() {
        MutableLiveData<Long> liveLong = new MutableLiveData<>();
        liveLong.setValue(1L);

        MutableLiveData<Boolean> liveBool = new MutableLiveData<>();
        liveBool.setValue(null);

        Loader<SimpleWrapper> loader = new Loader<>(SimpleWrapper::new);
        loader.addSource(liveLong, SimpleWrapper::setRoot);
        loader.addDependentSource(liveLong, l -> liveBool, SimpleWrapper::setBool);

        LiveData<SimpleWrapper> liveWrapper = loader.load();
        LiveTest.verifyLiveData(liveWrapper, wrapper -> wrapper.getBool() == null);

        liveBool.setValue(true);

        LiveTest.verifyLiveData(liveWrapper, wrapper -> wrapper.getBool().equals(true));
    }

    private LiveData<List<SimpleModel>> getLiveChildren(@Nullable Long value) {
        return LiveDataUtil.getLive(ModelsFixture.createSimpleModels("foo" + value, "bar" + value, "baz" + value));
    }
}
