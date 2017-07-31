package ru.codingworkshop.gymm.data.wrapper;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.SimpleWrapper;

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
        loader.addSource(liveData, SimpleWrapper::setLong);
        LiveData<SimpleWrapper> resultWrapper = loader.load();

        Observer<SimpleWrapper> observer = mock(Observer.class);
        resultWrapper.observeForever(observer);
        verify(observer).onChanged(argThat(returned -> returned.getLong() == 1L));

        liveData.setValue(2L);
        verify(observer, times(2)).onChanged(argThat(returned -> returned.getLong() == 2L));
    }

    @Test
    public void runIfSourceIsNull() {
        MutableLiveData<Long> longData = new MutableLiveData<>();
        longData.setValue(null);

        MutableLiveData<String> stringData = new MutableLiveData<>();
        stringData.setValue("foo");

        Loader<SimpleWrapper> loader = new Loader<>(SimpleWrapper::new);
        Runnable runnable = mock(Runnable.class);
        loader.addSource(longData, SimpleWrapper::setLong, runnable);
        loader.addDependentSource(longData, l -> getLiveString("foo" + l), SimpleWrapper::setString);
        loader.addDependentSource(longData, l -> {
            MutableLiveData<Boolean> boolData = new MutableLiveData<>();
            boolData.setValue(l == null);
            return boolData;
        }, SimpleWrapper::setBool);

        LiveData<SimpleWrapper> liveWrapper = loader.load();
        LiveTest.verifyLiveData(liveWrapper, w -> w.getLong() == null);

        verify(runnable).run();
    }

    @Test
    public void loadValuesToWrapperConsistently() {
        MutableLiveData<Long> liveLong = new MutableLiveData<>();
        liveLong.setValue(1L);

        Loader<SimpleWrapper> loader = new Loader<>(SimpleWrapper::new);
        loader.addSource(liveLong, SimpleWrapper::setLong);
        loader.addDependentSource(liveLong, l -> getLiveString("foo" + l), SimpleWrapper::setString);
        loader.addDependentSource(liveLong, l -> {
            MutableLiveData<Boolean> liveBool = new MutableLiveData<>();
            liveBool.setValue(l == 1L);
            return liveBool;
        }, SimpleWrapper::setBool);

        Observer<SimpleWrapper> observer = mock(Observer.class);
        loader.load().observeForever(observer);
        verify(observer).onChanged(argThat(
                wrapper -> wrapper.getLong() == 1L
                        && wrapper.getString().equals("foo1")
                        && wrapper.getBool()
        ));

        liveLong.setValue(2L);

        verify(observer, times(4)).onChanged(argThat(
                wrapper -> wrapper.getLong() == 2L
                        && wrapper.getString().equals("foo2")
                        && !wrapper.getBool()
        ));
    }

    @Test
    public void loadingNullValueFromDependentSource() {
        MutableLiveData<Long> liveLong = new MutableLiveData<>();
        liveLong.setValue(1L);

        MutableLiveData<String> liveString = new MutableLiveData<>();
        liveString.setValue("foo");

        Loader<SimpleWrapper> loader = new Loader<>(SimpleWrapper::new);
        loader.addSource(liveLong, SimpleWrapper::setLong);
        loader.addDependentSource(liveLong, l -> liveString, SimpleWrapper::setString);

        LiveData<SimpleWrapper> liveWrapper = loader.load();
        LiveTest.verifyLiveData(liveWrapper, wrapper -> wrapper.getString().equals("foo"));

        liveLong.setValue(2L);

        LiveTest.verifyLiveData(liveWrapper, wrapper -> wrapper.getString().equals("foo"));
    }

    private LiveData<String> getLiveString(@Nullable String value) {
        MutableLiveData<String> result = new MutableLiveData<>();
        result.setValue(value);
        return result;
    }
}
