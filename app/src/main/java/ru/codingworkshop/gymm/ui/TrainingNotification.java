package ru.codingworkshop.gymm.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.service.RestEventBusHolder;
import ru.codingworkshop.gymm.service.event.outcoming.RestFinishedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestStartedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestTimerTickEvent;
import ru.codingworkshop.gymm.ui.actual.ActualTrainingActivity;
import ru.codingworkshop.gymm.ui.actual.exercise.ActualExercisesFragment;
import ru.codingworkshop.gymm.ui.program.ProgramTrainingActivity;


/**
 * Created by Радик on 11.05.2017.
 */

public class TrainingNotification {
    private NotificationManager notificationManager;
    private Notification notification;
    private RemoteViews remoteViews;
    private Service service;

    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = TrainingNotification.class.getSimpleName();
    private final DateFormat NOTIFICATION_TIME_FORMATTER = new SimpleDateFormat("mm:ss", Locale.getDefault());

    public TrainingNotification(Service service, long actualTrainingId, String title) {
        this.service = service;

        notificationManager = (NotificationManager) this.service.getSystemService(Context.NOTIFICATION_SERVICE);

        remoteViews = new RemoteViews(this.service.getPackageName(), R.layout.notification_layout);
        remoteViews.setTextViewText(R.id.notification_title, title);
        remoteViews.setChronometer(R.id.notification_chronometer, SystemClock.elapsedRealtime(), null, true);

        Intent actualTrainingIntent = new Intent(service, ActualTrainingActivity.class);
        actualTrainingIntent.putExtra(ActualExercisesFragment.EXTRA_ACTUAL_TRAINING_ID, actualTrainingId);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(service)
                .addParentStack(ActualTrainingActivity.class)
                .addNextIntent(actualTrainingIntent);

        PendingIntent mainAction = stackBuilder.getPendingIntent(123, PendingIntent.FLAG_UPDATE_CURRENT);

        final String GYMM_CHANNEL_ID = "gymm_silent_channel_id";
        final String GYMM_CHANNEL_ID_2 = "gymm_channel_id";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel silentNotificationChannel = new NotificationChannel(
                    GYMM_CHANNEL_ID, "Gymm notifications", NotificationManager.IMPORTANCE_LOW);

            NotificationChannel notificationChannel = new NotificationChannel(
                    GYMM_CHANNEL_ID_2, "Gymm notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);

            notificationManager.createNotificationChannels(
                    Lists.newArrayList(silentNotificationChannel, notificationChannel));
        }

        notification = new NotificationCompat.Builder(this.service, GYMM_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setDefaults(0)
                .setOnlyAlertOnce(false)
                .setCustomContentView(remoteViews)
                .setShowWhen(false)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContentIntent(mainAction)
                .build();
    }

    public void show() {
        subscribe((RestEventBusHolder) service);
        service.startForeground(NOTIFICATION_ID, notification);
    }

    private void subscribe(RestEventBusHolder eventBusHolder) {
        eventBusHolder.getRestEventBus().register(this);
    }

    @Subscribe
    private void onRestStarted(RestStartedEvent event) {
        setRestTime(event.getMilliseconds());
        restStarted();
    }

    public void restStarted() {
        Log.d(TAG, "restStarted");
        setDefaults(0);
        setNotificationText(R.string.notification_time_till_rest_finish);
        setVisible(R.id.notification_rest_elapsed_time);
        setVisible(R.id.notification_rest_layout);
        update();
    }

    @Subscribe
    private void onRestFinished(RestFinishedEvent event) {
        restFinished();
    }

    public void restFinished() {
        setNotificationText(R.string.notification_rest_finished);
        setGone(R.id.notification_rest_elapsed_time);
        setDefaults(NotificationCompat.DEFAULT_ALL);
        update();
    }

    @Subscribe
    private void onTimerTick(RestTimerTickEvent event) {
        setRestTime(event.getMilliseconds());
    }

    public void setRestTime(long milliseconds) {
        String time = NOTIFICATION_TIME_FORMATTER.format(new Date(milliseconds));
        setText(R.id.notification_rest_elapsed_time, time);
        update();
    }

    private void update() {
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    // change defaults
    private void setDefaults(int defaults) {
        notification.defaults = defaults;
    }

    // change text
    private void setNotificationText(@StringRes int stringRes) {
        setText(R.id.notification_text, stringRes);
    }

    private void setText(@IdRes int viewId, @StringRes int stringRes) {
        setText(viewId, service.getString(stringRes));
    }

    private void setText(@IdRes int viewId, String text) {
        getRemoteViews().setTextViewText(viewId, text);
    }

    // change visibility
    private void setVisible(@IdRes int viewId) {
        setVisibility(viewId, View.VISIBLE);
    }

    private void setGone(@IdRes int viewId) {
        setVisibility(viewId, View.GONE);
    }

    private void setVisibility(@IdRes int viewId, int visibility) {
        getRemoteViews().setViewVisibility(viewId, visibility);
    }

    private RemoteViews getRemoteViews() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.N ? notification.contentView : remoteViews;
    }
}
