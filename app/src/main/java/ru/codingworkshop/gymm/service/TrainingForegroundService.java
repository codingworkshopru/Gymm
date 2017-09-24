package ru.codingworkshop.gymm.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.support.annotation.Nullable;

import timber.log.Timber;

/**
 * Created by Радик on 21.09.2017 as part of the Gymm project.
 */

public class TrainingForegroundService extends Service {

    static final class ServiceBinder extends Binder {
        private TrainingForegroundService service;

        public ServiceBinder(TrainingForegroundService service) {
            this.service = service;
        }

        public TrainingForegroundService getService() {
            return service;
        }
    }

    private Looper serviceLooper;
    private RestController timerController;
    private IBinder binder = new ServiceBinder(this);

    public void startRest(long millisecondsForRest) {
        Timber.d("startRest: %d", millisecondsForRest);

        timerController.startRest(millisecondsForRest);
    }

    @Override
    public void onCreate() {
        Timber.d("onCreate");

        HandlerThread thread = new HandlerThread(this.getClass().getSimpleName(),
                Process.THREAD_PRIORITY_FOREGROUND);
        thread.start();

        serviceLooper = thread.getLooper();
        timerController = new RestController(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("onStartCommand");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Timber.d("onDestroy");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            serviceLooper.quitSafely();
        } else {
            serviceLooper.quit();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Timber.d("onBind");
        return binder;
    }

    public void pauseRest() {
        timerController.pauseRest();
    }

    public void resumeRest() {
        timerController.resumeRest();
    }

    public void stopRest() {
        timerController.stopRest();
    }

    public void addRestTime(long additionalTime) {
        timerController.addRestTime(additionalTime);
    }

    public boolean isRestInProgress() {
        return timerController.isRestInProgress();
    }

    public boolean isRestInPause() {
        return timerController.isRestInPause();
    }

    public long getMillisecondsLeft() {
        return timerController.getMillisecondsLeft();
    }

    public static boolean isRunning(Context context) {
        return isServiceRunning(context, TrainingForegroundService.class);
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}
