package ru.codingworkshop.gymm.program;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Радик on 05.04.2017.
 */

public class AlertDialogFragment extends DialogFragment {
    static final String CANONICAL_NAME = AlertDialogFragment.class.getCanonicalName();
    public static final String TITLE_RESOURCE_KEY = CANONICAL_NAME + ".TITLE_RESOURCE_KEY";
    public static final String MESSAGE_RESOURCE_KEY = CANONICAL_NAME + ".MESSAGE_RESOURCE_KEY";
    public static final String POSITIVE_BUTTON_RESOURCE_KEY = CANONICAL_NAME + ".POSITIVE_BUTTON_RESOURCE_KEY";
    public static final String NEGATIVE_BUTTON_RESOURCE_KEY = CANONICAL_NAME + ".NEGATIVE_BUTTON_RESOURCE_KEY";

    public interface OnDialogButtonClickListener {
        void onButtonClick(boolean positive);
    }

    private OnDialogButtonClickListener mListener;

    public void setListener(OnDialogButtonClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        @StringRes int title = args.getInt(TITLE_RESOURCE_KEY);
        @StringRes int message = args.getInt(MESSAGE_RESOURCE_KEY);
        @StringRes int positiveButton = args.getInt(POSITIVE_BUTTON_RESOURCE_KEY);
        @StringRes int negativeButton = args.getInt(NEGATIVE_BUTTON_RESOURCE_KEY);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (title != 0)
            builder.setTitle(title);

        if (message != 0)
            builder.setMessage(message);

        if (mListener != null) {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onButtonClick(which == DialogInterface.BUTTON_POSITIVE);
                }
            };

            if (negativeButton != 0)
                builder.setNegativeButton(negativeButton, listener);

            if (positiveButton != 0)
                builder.setPositiveButton(positiveButton, listener);
        }

        return builder.create();
    }
}
