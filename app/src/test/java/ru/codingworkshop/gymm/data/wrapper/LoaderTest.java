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

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by Радик on 22.06.2017.
 */

@RunWith(JUnit4.class)
public class LoaderTest {
    private static final class SimpleWrapper {
        private Long number;
        private String anotherValue;

        private SimpleWrapper() {
        }

        void setWrappedValue(Long myNumber) {
            number = myNumber;
        }

        Long getWrappedValue() {
            return number;
        }

        void setAnotherValue(String value) {
            anotherValue = value;
        }

        String getAnotherValue() {
            return anotherValue;
        }
    }

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void loadValueToWrapper() {
        Loader<SimpleWrapper> loader = new Loader<>(SimpleWrapper::new);
        MutableLiveData<Long> liveData = new MutableLiveData<>();
        liveData.setValue(1L);
        loader.addSource(liveData, SimpleWrapper::setWrappedValue);
        LiveData<SimpleWrapper> resultWrapper = loader.load();

        Observer<SimpleWrapper> observer = mock(Observer.class);
        resultWrapper.observeForever(observer);
        verify(observer).onChanged(argThat(returned -> returned.getWrappedValue() == 1L));

        liveData.setValue(2L);
        verify(observer, times(2)).onChanged(argThat(returned -> returned.getWrappedValue() == 2L));
    }

    @Test
    public void runIfSourceIsNull() {
        Loader<SimpleWrapper> loader = new Loader<>(SimpleWrapper::new);
        LiveData<SimpleWrapper> liveWrapper = loader.load();
        MutableLiveData<Long> longData = new MutableLiveData<>();
        Runnable runnable = mock(Runnable.class);
        longData.setValue(1L);

        loader.addSource(longData, SimpleWrapper::setWrappedValue, runnable);
        LiveTest.verifyLiveData(liveWrapper, w -> w.getWrappedValue() == 1L);

        longData.setValue(null);
        LiveTest.verifyLiveData(liveWrapper, w -> w.getWrappedValue() == 1L);

        verify(runnable).run();
    }

    @Test
    public void loadValuesToWrapperConsistently() {
        Loader<SimpleWrapper> loader = new Loader<>(SimpleWrapper::new);
        MutableLiveData<Long> liveLong = new MutableLiveData<>();
        liveLong.setValue(1L);

        loader.addSource(liveLong, SimpleWrapper::setWrappedValue);
        loader.addDependentSource(liveLong, this::getStringWithLong, SimpleWrapper::setAnotherValue);

        Observer<SimpleWrapper> observer = mock(Observer.class);
        loader.load().observeForever(observer);
        verify(observer, times(1)).onChanged(argThat(
                wrapper -> wrapper.getWrappedValue() == 1L
                        && wrapper.getAnotherValue().equals("foo1")
        ));

        liveLong.setValue(2L);

        verify(observer, times(3)).onChanged(argThat(
                wrapper -> wrapper.getWrappedValue() == 2L
                        && wrapper.getAnotherValue().equals("foo2")
        ));
    }

    @Test
    public void loadingNullValueFromDependentSource() {
        Loader<SimpleWrapper> loader = new Loader<>(SimpleWrapper::new);
        MutableLiveData<Long> liveLong = new MutableLiveData<>();
        MutableLiveData<String> liveString = new MutableLiveData<>();
        liveLong.setValue(1L);
        liveString.setValue("foo");

        loader.addSource(liveLong, SimpleWrapper::setWrappedValue);
        loader.addDependentSource(liveLong, (l) -> liveString, SimpleWrapper::setAnotherValue);

        LiveData<SimpleWrapper> liveWrapper = loader.load();
        LiveTest.verifyLiveData(liveWrapper, wrapper -> wrapper.getAnotherValue().equals("foo"));

        liveLong.setValue(2L);

        LiveTest.verifyLiveData(liveWrapper, wrapper -> wrapper.getAnotherValue().equals("foo"));
    }

    private LiveData<String> getStringWithLong(long number) {
        return getLiveString("foo" + number);
    }

    private LiveData<String> getLiveString(@Nullable String value) {
        MutableLiveData<String> result = new MutableLiveData<>();
        result.setValue(value);
        return result;
    }
}
