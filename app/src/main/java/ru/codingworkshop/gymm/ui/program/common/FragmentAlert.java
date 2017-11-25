package ru.codingworkshop.gymm.ui.program.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;

import ru.codingworkshop.gymm.ui.util.AlertDialogFragment;

/**
 * Created by Радик on 04.05.2017.
 */

public class FragmentAlert {
    private FragmentManager fragmentManager;
    private AlertDialogFragment dialogFragment;

    public FragmentAlert(@NonNull FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;

        dialogFragment = (AlertDialogFragment) fragmentManager.findFragmentByTag(AlertDialogFragment.TAG);
        if (dialogFragment == null) {
            dialogFragment = AlertDialogFragment.newInstance();
        }
    }

    public void showAlert(int dialogId, @NonNull Bundle args) {
        args = new AlertDialogFragment.ArgumentsBuilder(args)
                .setDialogId(dialogId)
                .build();

        dialogFragment.show(fragmentManager, args);
    }

    public void showOneButtonAlert(int dialogId, @StringRes int message) {
        Bundle args = new AlertDialogFragment.ArgumentsBuilder()
                .setMessage(message)
                .setPositiveButtonDefaultText()
                .build();

        showAlert(dialogId, args);
    }

    public void showTwoButtonsAlert(int dialogId, @StringRes int message) {
        Bundle args = new AlertDialogFragment.ArgumentsBuilder()
                .setMessage(message)
                .setPositiveButtonDefaultText()
                .setNegativeButtonDefaultText()
                .build();

        showAlert(dialogId, args);
    }

    public void hideAlert() {
        dialogFragment.dismiss();
    }
}
