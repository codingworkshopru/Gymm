package ru.codingworkshop.gymm.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Радик on 04.04.2017.
 */

public class TimeService extends Service {
    // TODO проверить, как ведет себя при завершении вручную

    public interface TimeServiceObserver extends Parcelable {
        void notifyTick(long elapsedMillis);
    }

    private List<TimeServiceObserver> mObservers;
    private Thread mWorkerThread;

    private static final String TAG = TimeService.class.getSimpleName();

    private final class TimeServiceRunnable implements Runnable {

        private List<TimeServiceObserver> mObservers;

        TimeServiceRunnable(List<TimeServiceObserver> observers) {
            mObservers = observers;
        }

        @Override
        public void run() {
            long startingTimeSecs = System.currentTimeMillis()/1000;
            long currentTimeSecs = 0;

            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(100);
                    if (!mObservers.isEmpty()) {
                        long currentTime = System.currentTimeMillis();
                        if (currentTime/1000 > currentTimeSecs) {
                            currentTimeSecs = currentTime/1000;
                            for (TimeServiceObserver observer : mObservers)
                                observer.notifyTick(currentTimeSecs - startingTimeSecs);
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            stopSelf();
        }
    }

    @Override
    public void onCreate() {
        mObservers = new ArrayList<>();

        TimeServiceRunnable runnable = new TimeServiceRunnable(mObservers);
        mWorkerThread = new Thread(runnable);
        mWorkerThread.start();

        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TimeServiceObserver observer = intent.getParcelableExtra("");
        mObservers.add(observer);

        Log.d(TAG, "onStartCommand");

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mWorkerThread.interrupt();

        Log.d(TAG, "onDestroy");
    }
}
