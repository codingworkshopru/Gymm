package ru.codingworkshop.gymm.ui.program.exercise;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.ui.program.ProgramActivityAlerts;
import ru.codingworkshop.gymm.ui.util.AlertDialogFragment;

/**
 * Created by Радик on 04.05.2017.
 */

final class ProgramExerciseActivityAlerts extends ProgramActivityAlerts {

    public interface Callback extends ProgramActivityAlerts.Callback {
        void pickExercise();
    }

    public static final int EXERCISE_NOT_SELECTED_DIALOG_ID = 3;

    private Callback callback;

    public ProgramExerciseActivityAlerts(@NonNull Activity activityForAlert) {
        super(activityForAlert);
        callback = (Callback) activityForAlert;
    }

    void showExerciseNotSelected() {
        Bundle args = new AlertDialogFragment.ArgumentsBuilder()
                .setMessage(R.string.program_exercise_activity_exercise_not_selected_message)
                .setPositiveButtonText(R.string.select_button_text)
                .setNegativeButtonDefaultText()
                .build();

        showAlert(EXERCISE_NOT_SELECTED_DIALOG_ID, args);
    }

    @Override
    public void showOnEmptyList() {
        showOnEmptyList(R.string.program_exercise_activity_empty_list_dialog_message);
    }

    @Override
    public void onDialogButtonClick(int dialogId, boolean positive) {
        super.onDialogButtonClick(dialogId, positive);

        if (dialogId == EXERCISE_NOT_SELECTED_DIALOG_ID && positive)
            callback.pickExercise();
    }
}
