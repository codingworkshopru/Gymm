package ru.codingworkshop.gymm.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import ru.codingworkshop.gymm.ui.util.AlertDialogFragment;

/**
 * Created by Радик on 04.05.2017.
 */

public class ActivityAlerts implements AlertDialogFragment.OnDialogButtonClickListener {
    private Activity activity;
    private AlertDialogFragment dialogFragment;

    public ActivityAlerts(@NonNull Activity activityForAlert) {
        activity = activityForAlert;

        dialogFragment = (AlertDialogFragment) activity.getFragmentManager().findFragmentByTag(AlertDialogFragment.TAG);
        if (dialogFragment == null) {
            dialogFragment = AlertDialogFragment.newInstance();
        }
        dialogFragment.setListener(this);
    }

    protected void showAlert(int dialogId, @NonNull Bundle args) {
        args = new AlertDialogFragment.ArgumentsBuilder(args)
                .setDialogId(dialogId)
                .build();

        dialogFragment.show(activity.getFragmentManager(), args);
    }

    protected void showAlertWithDefaultButtons(int dialogId, @StringRes int message) {
        Bundle args = new AlertDialogFragment.ArgumentsBuilder()
                .setMessage(message)
                .setPositiveButtonDefaultText()
                .setNegativeButtonDefaultText()
                .build();

        showAlert(dialogId, args);
    }

    @Override
    public void onDialogButtonClick(int dialogId, boolean positive) {

    }
}
