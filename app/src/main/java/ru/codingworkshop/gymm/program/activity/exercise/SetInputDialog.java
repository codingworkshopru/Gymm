package ru.codingworkshop.gymm.program.activity.exercise;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.ProgramSet;

/**
 * Created by Радик on 25.02.2017.
 */

public final class SetInputDialog extends DialogFragment
        implements DialogInterface.OnClickListener {

    public interface SetInputDialogListener {
        void onProgramSetReturn(ProgramSet model);
    }

    private static final int REPS_MIN = 1;
    private static final int REPS_MAX = 100;

    private static final int MINUTES_MIN = 0;
    private static final int MINUTES_MAX = 60;

    private static final int SECONDS_MIN = 0;
    private static final int SECONDS_MAX = 59;

    public static final String DIALOG_MODEL_KEY = ProgramSet.class.getCanonicalName();

    private ProgramSet mModel;
    private SetInputDialogListener mSetInputDialogListener;

    // inputs
    private NumberPicker mRepsPicker;
    private NumberPicker mMinutesPicker;
    private NumberPicker mSecondsPicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View customView = inflater.inflate(R.layout.activity_program_exercise_dialog, null);
        mRepsPicker = (NumberPicker) customView.findViewById(R.id.program_exercise_reps_picker);
        mRepsPicker.setMinValue(REPS_MIN);
        mRepsPicker.setMaxValue(REPS_MAX);

        mMinutesPicker = (NumberPicker) customView.findViewById(R.id.program_exercise_rest_minutes_picker);
        mMinutesPicker.setMinValue(MINUTES_MIN);
        mMinutesPicker.setMaxValue(MINUTES_MAX);

        mSecondsPicker = (NumberPicker) customView.findViewById(R.id.program_exercise_rest_seconds_picker);
        mSecondsPicker.setMinValue(SECONDS_MIN);
        mSecondsPicker.setMaxValue(SECONDS_MAX);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.program_exercise_activity_dialog_title)
                .setCancelable(true)
                .setView(customView)
                .setPositiveButton(R.string.program_exercise_activity_dialog_positive_button_text, this)
                .setNegativeButton(R.string.program_exercise_activity_dialog_negative_button_text, null);

        Bundle savedModel = getArguments();
        if (savedModel == null)
            savedModel = savedInstanceState;

        if (savedModel != null && savedModel.containsKey(DIALOG_MODEL_KEY)) {
            mModel = savedModel.getParcelable(DIALOG_MODEL_KEY);
            if (mModel != null) {
                mRepsPicker.setValue(mModel.getReps());
                mMinutesPicker.setValue(mModel.getRestMinutes());
                mSecondsPicker.setValue(mModel.getRestSeconds());
            }
        } else {
            mModel = new ProgramSet();
            mModel.setReps(REPS_MIN);
        }

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mSetInputDialogListener = (SetInputDialogListener) getActivity();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DIALOG_MODEL_KEY, mModel);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        mModel.setReps(mRepsPicker.getValue());
        mModel.setTimeForRest(mMinutesPicker.getValue(), mSecondsPicker.getValue());
        mSetInputDialogListener.onProgramSetReturn(mModel);
    }
}
