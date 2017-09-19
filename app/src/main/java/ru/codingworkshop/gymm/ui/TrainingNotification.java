package ru.codingworkshop.gymm.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.codingworkshop.gymm.R;


/**
 * Created by Радик on 11.05.2017.
 */

public final class TrainingNotification {
    private NotificationManager notificationManager;
    private Notification notification;
    private RemoteViews remoteViews;
    private Context context;

    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = TrainingNotification.class.getSimpleName();

    public TrainingNotification(Context c, String title, long id) {
        context = c;

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
        remoteViews.setTextViewText(R.id.notification_title, title);
        remoteViews.setChronometer(R.id.notification_chronometer, SystemClock.elapsedRealtime(), null, true);

//        Intent actualTrainingIntent = new Intent(context, ActualTrainingActivity.class);
//        actualTrainingIntent.putExtra(ProgramTrainingActivity.PROGRAM_TRAINING_ID, id);
//
//        TaskStackBuilder stackBuilder = TaskStackBuilder.programTrainingInstance(context)
//                .addParentStack(ActualTrainingActivity.class)
//                .addNextIntent(actualTrainingIntent);

//        PendingIntent mainAction = stackBuilder.getPendingIntent(123, PendingIntent.FLAG_UPDATE_CURRENT);

        final String GYMM_CHANNEL_ID = "gymm_channel_id";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(GYMM_CHANNEL_ID, "Gymm notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notification = new NotificationCompat.Builder(context, GYMM_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setOnlyAlertOnce(false)
                .setCustomContentView(remoteViews)
                .setShowWhen(false)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
//                .setContentIntent(mainAction)
                .build();
    }

    public void show(Service service) {
        setGone(R.id.notification_rest_layout);
        service.startForeground(NOTIFICATION_ID, notification);
    }

    public void restStarted() {
        Log.d(TAG, "restStarted");
        setDefaults(0);
        setNotificationText(R.string.notification_time_till_rest_finish);
        setVisible(R.id.notification_rest_elapsed_time);
        setVisible(R.id.notification_rest_layout);
        update();
    }

    public void restFinished() {
        setNotificationText(R.string.notification_rest_finished);
        setGone(R.id.notification_rest_elapsed_time);
        setDefaults(NotificationCompat.DEFAULT_ALL);
        update();
    }

    public void setRestTime(long milliseconds) {
        DateFormat f = new SimpleDateFormat("mm:ss");
        String time = f.format(new Date(milliseconds));
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
        setText(viewId, context.getString((stringRes)));
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
