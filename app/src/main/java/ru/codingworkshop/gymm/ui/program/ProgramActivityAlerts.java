package ru.codingworkshop.gymm.ui.program;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.ui.ActivityAlerts;
import ru.codingworkshop.gymm.ui.util.AlertDialogFragment;

/**
 * Created by Радик on 04.05.2017.
 */

public abstract class ProgramActivityAlerts extends ActivityAlerts {

    public interface Callback {
        void finishWithoutSaving();
        void addListItem();
    }

    private static final int BACK_DIALOG_ID = 0;
    private static final int EMPTY_LIST_DIALOG_ID = 1;

    private Callback callback;

    public ProgramActivityAlerts(@NonNull Activity activityForAlert) {
        super(activityForAlert);
        callback = (Callback) activityForAlert;
    }

    public void showUnsavedChanges() {
        showAlertWithDefaultButtons(BACK_DIALOG_ID, R.string.save_changes_question);
    }

    abstract public void showOnEmptyList();

    protected void showOnEmptyList(@StringRes int message) {
        Bundle args = new AlertDialogFragment.ArgumentsBuilder()
                .setMessage(message)
                .setPositiveButtonText(R.string.add_button_text)
                .setNegativeButtonDefaultText()
                .build();

        showAlert(EMPTY_LIST_DIALOG_ID, args);
    }


    @Override
    public void onDialogButtonClick(int dialogId, boolean positive) {
        if (dialogId == BACK_DIALOG_ID && positive)
            callback.finishWithoutSaving();

        if (dialogId == EMPTY_LIST_DIALOG_ID && positive)
            callback.addListItem();
    }
}
