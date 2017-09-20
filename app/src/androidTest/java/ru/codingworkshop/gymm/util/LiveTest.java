package ru.codingworkshop.gymm.util;

import android.arch.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Радик on 20.09.2017 as part of the Gymm project.
 */

public class LiveTest {
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
