package ru.codingworkshop.gymm.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.support.annotation.VisibleForTesting;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

import ru.codingworkshop.gymm.ui.TrainingNotification;
import timber.log.Timber;

/**
 * Created by Радик on 21.09.2017 as part of the Gymm project.
 */

public class TrainingForegroundService extends Service implements RestEventBusHolder {

    public static final class ServiceBinder extends Binder {
        private TrainingForegroundService service;

        public ServiceBinder(TrainingForegroundService service) {
            this.service = service;
        }

        public TrainingForegroundService getService() {
            return service;
        }
    }

    public static final String ACTUAL_TRAINING_ID_KEY = "actualTrainingIdKey";
    public static final String TRAINING_NAME_KEY = "trainingNameKey";

    @VisibleForTesting
    TrainingNotification notification;
    private Looper serviceLooper;
    private RestController restController;
    private IBinder binder = new ServiceBinder(this);

    public static void startService(Context context, long actualTrainingId, String notificationTitle) {
        if (isRunning(context)) return;

        Intent intent = new Intent(context, TrainingForegroundService.class);
        intent.putExtras(createExtras(actualTrainingId, notificationTitle));
        context.startService(intent);
    }

    public static Bundle createExtras(long actualTrainingId, String notificationTitle) {
        Bundle extras = new Bundle(2);
        extras.putLong(ACTUAL_TRAINING_ID_KEY, actualTrainingId);
        extras.putString(TRAINING_NAME_KEY, notificationTitle);
        return extras;
    }

    @Override
    public void onCreate() {
        Timber.d("onCreate");

        HandlerThread thread = new HandlerThread(this.getClass().getSimpleName(),
                Process.THREAD_PRIORITY_FOREGROUND);
        thread.start();

        serviceLooper = thread.getLooper();
        restController = new RestController(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("onStartCommand");

        Preconditions.checkArgument(intent.hasExtra(ACTUAL_TRAINING_ID_KEY));
        Preconditions.checkArgument(intent.hasExtra(TRAINING_NAME_KEY));

        notification = new TrainingNotification(this,
                intent.getLongExtra(ACTUAL_TRAINING_ID_KEY, 0L),
                intent.getStringExtra(TRAINING_NAME_KEY));

        notification.show();

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Timber.d("onDestroy");

        stopForeground(true);
        notification = null;
        restController = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            serviceLooper.quitSafely();
        } else {
            serviceLooper.quit();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Timber.d("onBind");
        return binder;
    }

    @Override
    public EventBus getRestEventBus() {
        return restController.getRestEventBus();
    }

    public boolean isRestInProgress() {
        return restController.isRestInProgress();
    }

    public boolean isRestInPause() {
        return restController.isRestInPause();
    }

    public long getMillisecondsLeft() {
        return restController.getMillisecondsLeft();
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
