package ru.codingworkshop.gymm.program;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;

import ru.codingworkshop.gymm.R;

/**
 * Created by Радик on 17.03.2017.
 */

public class ProgramUtils {
    public static void showSnackbar(@NonNull View view, @StringRes int text, @StringRes int button, View.OnClickListener listener) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setAction(button, listener)
                .show();
    }

    public static void showAlert(Context context, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        new AlertDialog.Builder(context)
                .setMessage(R.string.save_changes_question)
                .setPositiveButton(R.string.save_button_text, positiveListener)
                .setNegativeButton(R.string.no_button_text, negativeListener)
                .setNeutralButton(R.string.cancel_button_text, null)
                .show();
    }
}
