package ru.codingworkshop.gymm.util;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;

import org.mockito.ArgumentMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by Radik on 03.12.2017.
 */

public class LiveTest {
    public static <T> void verifyLiveData(LiveData<T> live, Predicate<T> check) {
        Observer<T> observer = mock(Observer.class);
        live.observeForever(observer);
        verify(observer).onChanged(argThat(new ArgumentMatcher<T>() {
            private T argument;
            @Override
            public boolean matches(T argument) {
                if (this.argument != null) return true;
                this.argument = argument;
                return check.test(argument);
            }
        }));
    }

    public static <T> T getValue(LiveData<T> liveData) throws InterruptedException {
        List<T> result = new ArrayList<>(1);

        CountDownLatch countDownLatch = new CountDownLatch(1);

        liveData.observeForever(value -> {
            result.add(value);
            countDownLatch.countDown();
        });

        countDownLatch.await(2, TimeUnit.SECONDS);

        return result.get(0);
    }
}
