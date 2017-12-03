package ru.codingworkshop.gymm.data.tree.loader.common;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;

import ru.codingworkshop.gymm.util.LiveTest;

/**
 * Created by Radik on 09.11.2017.
 */

public class LoaderDelegateTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void loadTest() throws Exception {
        MutableLiveData<Long> liveTestData = new MutableLiveData<>();
        liveTestData.setValue(1L);

        AtomicLong testData = new AtomicLong();

        LoaderDelegate loaderDelegate = new LoaderDelegate();
        loaderDelegate.addSource(liveTestData, testData::set);

        LiveData<AtomicLong> liveLoadedTestData = loaderDelegate.mapLoaded(testData);
        LiveTest.verifyLiveData(liveLoadedTestData, loadedValue -> {
            return loadedValue.get() == 1L;
        });

        liveTestData.setValue(2L);
        LiveTest.verifyLiveData(liveLoadedTestData, loadedValue -> {
            return loadedValue.get() == 2L;
        });
    }
}
