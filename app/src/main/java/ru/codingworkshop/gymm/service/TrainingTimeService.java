package ru.codingworkshop.gymm.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import ru.codingworkshop.gymm.ActualTrainingActivity;
import ru.codingworkshop.gymm.MainActivity;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.ProgramTraining;

/**
 * Created by Радик on 07.04.2017.
 */

public class TrainingTimeService extends Service {
    private static boolean isStarted = false;
    private static final int IDLE_MESSAGE = 5146;
    private static final int STOP_MESSAGE = 6245;
    private HandlerThread mThread;
    private TimeHandler mHandler;
    private NotificationManager mNotificationManager;

    private static final String TAG = TrainingTimeService.class.getSimpleName();

    private class TimeHandler extends Handler {
        private final long mStartTime = System.currentTimeMillis();
        private long mElapsedTimeSecs;

        private final String TAG = TimeHandler.class.getSimpleName();

        TimeHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            long currentElapsedTimeSecs = (System.currentTimeMillis() - mStartTime) / 1000;
            if (mElapsedTimeSecs < currentElapsedTimeSecs) {
                mElapsedTimeSecs = currentElapsedTimeSecs;
                Log.d(TAG, "handleMessage: " + mElapsedTimeSecs);
            }

            switch (msg.what) {
                // loop
                case IDLE_MESSAGE:
                    sendMessageDelayed(obtainMessage(IDLE_MESSAGE), 100);
                    break;

                case STOP_MESSAGE:
                    removeMessages(IDLE_MESSAGE);
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        if (isStarted)
            throw new RuntimeException(TrainingTimeService.class.getSimpleName() + " already exists");

        mThread = new HandlerThread(TrainingTimeService.class.getName(), Process.THREAD_PRIORITY_FOREGROUND);
        mThread.start();

        mHandler = new TimeHandler(mThread.getLooper());
        Message msg = mHandler.obtainMessage();
        msg.what = IDLE_MESSAGE;
        mHandler.sendMessage(msg);

        isStarted = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        if (intent == null)
            return START_STICKY;

        ProgramTraining programTraining = intent.getParcelableExtra("model");

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
        remoteViews.setChronometer(R.id.chronometer2, SystemClock.elapsedRealtime(), null, true);
        remoteViews.setTextViewText(R.id.textView, programTraining.getName());
        remoteViews.setTextViewText(R.id.textView2, programTraining.getChild(0).getExercise().getName());
        remoteViews.setImageViewResource(R.id.imageView10, R.drawable.ic_fitness_200dp);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setCustomContentView(remoteViews)
                .setSmallIcon(R.drawable.ic_fitness_200dp)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true);

        Intent actualTrainingIntent = new Intent(this, ActualTrainingActivity.class);
        actualTrainingIntent.putExtra(MainActivity.PROGRAM_TRAINING_ID_KEY, programTraining.getId());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this)
                .addParentStack(ActualTrainingActivity.class)
                .addNextIntent(actualTrainingIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(pendingIntent);

        startForeground(12341234, notificationBuilder.build());

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        mHandler.sendMessage(mHandler.obtainMessage(STOP_MESSAGE));

        stopForeground(true);

        isStarted = false;
    }

    public static boolean isStarted() {
        return isStarted;
    }
}