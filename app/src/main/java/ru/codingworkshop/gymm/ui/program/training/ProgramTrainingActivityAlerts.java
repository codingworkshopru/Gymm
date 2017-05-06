package ru.codingworkshop.gymm.ui.program.training;

import android.app.Activity;
import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.ui.program.ProgramActivityAlerts;

/**
 * Created by Радик on 04.05.2017.
 */

final class ProgramTrainingActivityAlerts extends ProgramActivityAlerts {
    public ProgramTrainingActivityAlerts(@NonNull Activity activityForAlert) {
        super(activityForAlert);
    }

    @Override
    public void showOnEmptyList() {
        showOnEmptyList(R.string.program_training_activity_empty_list_dialog_message);
    }
}
