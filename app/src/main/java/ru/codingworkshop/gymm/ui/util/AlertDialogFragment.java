package ru.codingworkshop.gymm.ui.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import ru.codingworkshop.gymm.R;

/**
 * Created by Радик on 10.05.2017.
 */
public final class AlertDialogFragment extends DialogFragment {

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

    public void show(@NonNull FragmentManager fragmentManager, @NonNull Bundle args) {
        setArguments(args);
        show(fragmentManager, TAG);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnDialogButtonClickListener) {
            listener = (OnDialogButtonClickListener) context;
        } else if (getParentFragment() instanceof OnDialogButtonClickListener) {
            listener = (OnDialogButtonClickListener) getParentFragment();
        } else {
            throw new IllegalStateException("Parent activity or fragment must implement " + OnDialogButtonClickListener.class);
        }
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

        AlertDialog.Builder builder = new AlertDialog.Builder(
                getContext(),
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                        ? R.style.ThemeOverlay_AppCompat_Dialog_Alert : 0);

        if (title != 0) {
            builder.setTitle(title);
        }

        if (message != 0) {
            builder.setMessage(message);
        }

        DialogInterface.OnClickListener dialogInterfaceListener = null;
        if (listener != null) {
            dialogInterfaceListener = (dialog, which) -> {
                listener.onDialogButtonClick(dialogId, which == DialogInterface.BUTTON_POSITIVE);
            };
        }

        if (negativeButton != 0)
            builder.setNegativeButton(negativeButton, dialogInterfaceListener);

        if (positiveButton != 0)
            builder.setPositiveButton(positiveButton, dialogInterfaceListener);

        return builder.create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
