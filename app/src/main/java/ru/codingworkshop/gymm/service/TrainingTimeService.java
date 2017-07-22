package ru.codingworkshop.gymm.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.ui.TrainingNotification;

/**
 * Created by Радик on 07.04.2017.
 */

public class TrainingTimeService extends Service {
    private HandlerThread handlerThread;
    private TimeHandler timeHandler;
    private EventBus serviceEventBus;
    private TrainingNotification notification;
    private IBinder binder;
    private RestTimer restTimer;

    private static final String TAG = TrainingTimeService.class.getSimpleName();

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        serviceEventBus = new EventBus();
        registerObserver(this);

        binder = new TimeServiceBinder(this);

        handlerThread = new HandlerThread(TrainingTimeService.class.getName(), Process.THREAD_PRIORITY_FOREGROUND);
        handlerThread.start();

        timeHandler = new TimeHandler(handlerThread.getLooper());
    }

    @Subscribe
    public void onTimerTick(RestTimer.TickEvent event) {
        notification.setRestTime(event.millisUntilFinished);
    }

    @Subscribe
    public void onTimerFinish(RestTimer.FinishEvent event) {
        Log.d(TAG, "onTimerFinish");
        restTimer = null;
        notification.restFinished();
    }

    public boolean isRestInProgress() {
        return restTimer != null;
    }

    public void startRest(long seconds) {
        if (seconds > 0 && restTimer == null) {
            Log.d(TAG, "startRest");
            long milliseconds = TimeUnit.SECONDS.toMillis(seconds);
            restTimer = new RestTimer(milliseconds, serviceEventBus);
            Message msg = timeHandler.obtainMessage(TimeHandler.MSG_START_REST_COUNTDOWN, restTimer);
            timeHandler.sendMessage(msg);
            notification.restStarted();
        } else if (restTimer != null) {
            throw new IllegalStateException("Rest timer isn't null");
        }
    }

    public void registerObserver(@NonNull Object obj) {
        serviceEventBus.register(obj);
    }

    public void unregisterObserver(@NonNull Object obj) {
        serviceEventBus.unregister(obj);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        if (intent == null)
            return START_STICKY;

        ProgramTraining programTraining = intent.getParcelableExtra("model");

        notification = new TrainingNotification(
                this,
                programTraining.getName(),
                programTraining.getId()
        );

        notification.show(this);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return binder;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        if (restTimer != null)
            restTimer.cancel();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2)
            handlerThread.getLooper().quit();
        else
            handlerThread.getLooper().quitSafely();

        stopForeground(true);
    }

    public static boolean isRunning(Context context) {
        return isServiceRunning(context, TrainingTimeService.class);
    }

    public static <S extends Service> boolean isServiceRunning(Context context, Class<S> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    public static final class TimeServiceBinder extends Binder {
        TrainingTimeService service;
        private TimeServiceBinder(TrainingTimeService service) {
            this.service = service;
        }

        public TrainingTimeService getService() {
            return service;
        }
    }
}
