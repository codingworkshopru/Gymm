package ru.codingworkshop.gymm.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.ui.actual.ActualTrainingActivity;
import ru.codingworkshop.gymm.ui.program.training.ProgramTrainingActivity;

/**
 * Created by Радик on 11.05.2017.
 */

public final class TrainingNotification {
    private NotificationManager notificationManager;
    private Notification notification;
    private RemoteViews remoteViews;

    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = TrainingNotification.class.getSimpleName();

    public TrainingNotification(Context context, String title, long id) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
        setTrainingName(title);
        remoteViews.setChronometer(R.id.notification_chronometer, SystemClock.elapsedRealtime(), null, true);
        setRestSectionVisibility(false);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setOnlyAlertOnce(false)
                .setCustomContentView(remoteViews)
                .setShowWhen(false)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle());

        Intent actualTrainingIntent = new Intent(context, ActualTrainingActivity.class);
        actualTrainingIntent.putExtra(ProgramTrainingActivity.PROGRAM_TRAINING_ID, id);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context)
                .addParentStack(ActualTrainingActivity.class)
                .addNextIntent(actualTrainingIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(123, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        notification = notificationBuilder.build();
    }

    public Notification getNotification() {
        return notification;
    }

    private void setTrainingName(String title) {
        remoteViews.setTextViewText(R.id.notification_training_name, title);
    }

    public void setExerciseName(String text) {
        remoteViews.setTextViewText(R.id.notification_exercise_name, text);
        setRestSectionVisibility(true);
    }

    public void setRestTime(long milliseconds) {
        DateFormat f = new SimpleDateFormat("mm:ss");
        String time = f.format(new Date(milliseconds));
        remoteViews.setTextViewText(R.id.notification_rest_elapsed_time, time);
        update();
    }

    public void setRestSectionVisibility(boolean visible) {
        remoteViews.setViewVisibility(R.id.notification_rest_layout, visible ? View.VISIBLE : View.GONE);
        if (notification != null)
            update();
    }

    private void update() {
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
