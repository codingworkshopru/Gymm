package ru.codingworkshop.gymm.program;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Радик on 17.03.2017.
 */

public class ProgramUtils {
    public static void showSnackbar(@NonNull View view, @StringRes int text, @StringRes int button, View.OnClickListener listener) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setAction(button, listener)
                .show();
    }

    public static void showAlert(AppCompatActivity activity, AlertDialogFragment.OnDialogButtonClickListener listener,
                                 @StringRes int title, @StringRes int message, @StringRes int positiveButton, @StringRes int negativeButton) {

        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.setListener(listener);

        Bundle arguments = new Bundle();
        arguments.putInt(AlertDialogFragment.TITLE_RESOURCE_KEY, title);
        arguments.putInt(AlertDialogFragment.MESSAGE_RESOURCE_KEY, message);
        arguments.putInt(AlertDialogFragment.POSITIVE_BUTTON_RESOURCE_KEY, positiveButton);
        arguments.putInt(AlertDialogFragment.NEGATIVE_BUTTON_RESOURCE_KEY, negativeButton);

        fragment.setArguments(arguments);

        fragment.show(activity.getSupportFragmentManager(), AlertDialogFragment.CANONICAL_NAME);
    }
}
