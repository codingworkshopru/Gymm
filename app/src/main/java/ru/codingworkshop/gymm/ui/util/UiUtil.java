package ru.codingworkshop.gymm.ui.util;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by Радик on 28.04.2017.
 */

public class UiUtil {
    public static void showSnackbar(@NonNull View view, @StringRes int text, @StringRes int button, View.OnClickListener listener) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setAction(button, listener)
                .show();
    }

}
