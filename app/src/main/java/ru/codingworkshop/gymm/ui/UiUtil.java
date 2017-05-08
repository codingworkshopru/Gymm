package ru.codingworkshop.gymm.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
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

    public static final class AlertDialogFragment extends DialogFragment {

        public interface OnDialogButtonClickListener {
            void onDialogButtonClick(int dialogId, boolean positive);
        }

        public static final String TAG = AlertDialogFragment.class.getSimpleName();

        public static final String DIALOG_ID_KEY = "dialogIdKey";
        public static final String TITLE_RESOURCE_KEY = "titleResourceKey";
        public static final String MESSAGE_RESOURCE_KEY = "messageResourceKey";
        public static final String POSITIVE_BUTTON_RESOURCE_KEY = "positiveButtonResourceKey";
        public static final String NEGATIVE_BUTTON_RESOURCE_KEY = "negativeButtonResourceKey";

        public static AlertDialogFragment newInstance() {
            return new AlertDialogFragment();
        }

        public static final class ArgumentsBuilder {
            private Bundle args = new Bundle();

            public ArgumentsBuilder() {

            }

            public ArgumentsBuilder(Bundle args) {
                this.args = args;
            }

            public ArgumentsBuilder setDialogId(int dialogId) {
                args.putInt(DIALOG_ID_KEY, dialogId);
                return this;
            }

            public ArgumentsBuilder setTitle(@StringRes int title) {
                args.putInt(TITLE_RESOURCE_KEY, title);
                return this;
            }

            public ArgumentsBuilder setMessage(@StringRes int message) {
                args.putInt(MESSAGE_RESOURCE_KEY, message);
                return this;
            }

            public ArgumentsBuilder setPositiveButtonText(@StringRes int positiveButtonText) {
                args.putInt(POSITIVE_BUTTON_RESOURCE_KEY, positiveButtonText);
                return this;
            }

            public ArgumentsBuilder setPositiveButtonDefaultText() {
                return setPositiveButtonText(android.R.string.ok);
            }

            public ArgumentsBuilder setNegativeButtonText(@StringRes int negativeButtonText) {
                args.putInt(NEGATIVE_BUTTON_RESOURCE_KEY, negativeButtonText);
                return this;
            }

            public ArgumentsBuilder setNegativeButtonDefaultText() {
                return setNegativeButtonText(android.R.string.cancel);
            }

            public Bundle build() {
                return args;
            }
        }

        private OnDialogButtonClickListener listener;

        public void setListener(OnDialogButtonClickListener l) {
            listener = l;
        }

        public void show(@NonNull FragmentManager fragmentManager, @NonNull Bundle args) {
            setArguments(args);
            show(fragmentManager, TAG);
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle args = getArguments();
            final int dialogId = args.getInt(DIALOG_ID_KEY);
            @StringRes int title = args.getInt(TITLE_RESOURCE_KEY);
            @StringRes int message = args.getInt(MESSAGE_RESOURCE_KEY);
            @StringRes int positiveButton = args.getInt(POSITIVE_BUTTON_RESOURCE_KEY);
            @StringRes int negativeButton = args.getInt(NEGATIVE_BUTTON_RESOURCE_KEY);

            if (listener == null)
                throw new NullPointerException("Listener is not set");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            if (title != 0)
                builder.setTitle(title);

            if (message != 0)
                builder.setMessage(message);

            DialogInterface.OnClickListener dialogInterfaceListener = null;
            if (listener != null) {
                dialogInterfaceListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogButtonClick(dialogId, which == DialogInterface.BUTTON_POSITIVE);
                    }
                };
            }

            if (negativeButton != 0)
                builder.setNegativeButton(negativeButton, dialogInterfaceListener);

            if (positiveButton != 0)
                builder.setPositiveButton(positiveButton, dialogInterfaceListener);

            return builder.create();
        }
    }
}