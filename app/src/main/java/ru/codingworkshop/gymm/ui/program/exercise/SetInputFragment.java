package ru.codingworkshop.gymm.ui.program.exercise;

/**
 * Created by Радик on 21.04.2017.
 */

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.ProgramSet;
import ru.codingworkshop.gymm.data.model.ProgramSetEntity;

/**
 * Created by Радик on 25.02.2017.
 */

public final class SetInputFragment extends DialogFragment
        implements DialogInterface.OnClickListener {

    interface SetInputDialogListener {
        void onSetInputDialogFinished(ProgramSet model);
    }

    private static final int REPS_MIN = 1;
    private static final int REPS_MAX = 100;

    private static final int MINUTES_MIN = 0;
    private static final int MINUTES_MAX = 60;

    private static final int SECONDS_MIN = 0;
    private static final int SECONDS_MAX = 59;
    private static final int SECONDS_STEP = 5;

    public static final String DIALOG_MODEL_KEY = ProgramSet.class.getCanonicalName();

    public static final String TAG = SetInputFragment.class.getSimpleName();

    private ProgramSet mModel;
    private SetInputDialogListener mSetInputDialogListener;

    // inputs
    private NumberPicker mRepsPicker;
    private NumberPicker mMinutesPicker;
    private NumberPicker mSecondsPicker;

    public static SetInputFragment newInstance() {
        return new SetInputFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        LayoutInflater inflater = getActivity().getLayoutInflater();

        @StringRes int title = R.string.program_exercise_activity_dialog_title_edit;

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DIALOG_MODEL_KEY)) {
            mModel = arguments.getParcelable(DIALOG_MODEL_KEY);
        } else {
            ProgramSet newModel = new ProgramSetEntity();
            if (mModel != null) {
                newModel.setReps(mModel.getReps());
                newModel.setSecondsForRest(mModel.getSecondsForRest());
            } else {
                newModel.setReps(REPS_MIN);
            }
            title = R.string.program_exercise_activity_dialog_title;
            mModel = newModel;
        }

        View customView = inflater.inflate(R.layout.activity_program_exercise_dialog, null);

        mRepsPicker = initPicker(customView, R.id.program_exercise_reps_picker, REPS_MIN, REPS_MAX, 1, mModel.getReps());
        mMinutesPicker = initPicker(customView, R.id.program_exercise_rest_minutes_picker, MINUTES_MIN, MINUTES_MAX, 1, mModel.getSecondsForRest() / 60);
        mSecondsPicker = initPicker(customView, R.id.program_exercise_rest_seconds_picker, SECONDS_MIN, SECONDS_MAX, SECONDS_STEP, mModel.getSecondsForRest() % 60);

        if (getActivity() instanceof SetInputDialogListener)
            mSetInputDialogListener = (SetInputDialogListener) getActivity();
        else
            throw new ClassCastException("Activity must implement " + SetInputDialogListener.class.getSimpleName() + " interface");

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setCancelable(true)
                .setView(customView)
                .setPositiveButton(R.string.ok_button_text, this)
                .setNegativeButton(R.string.cancel_button_text, null);

        return builder.create();
    }

    private NumberPicker initPicker(@NonNull View parent, @IdRes int pickerId, int min, int max, int step, int value) {
        NumberPicker picker = (NumberPicker) parent.findViewById(pickerId);
        picker.setMinValue(min);
        picker.setMaxValue(max);

        if (step != 1) {
            int maxWithStep = max / step;
            picker.setMaxValue(maxWithStep);

            String[] values = new String[maxWithStep + 1];
            for (int i = 0; i < values.length; i++)
                values[i] = String.valueOf(i * step);
            picker.setDisplayedValues(values);

            value /= step;
        }

        if (value != 0)
            picker.setValue(value);

        return picker;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        mModel.setReps(mRepsPicker.getValue());
        mModel.setSecondsForRest(mMinutesPicker.getValue() * 60 + mSecondsPicker.getValue() * SECONDS_STEP);
        mSetInputDialogListener.onSetInputDialogFinished(mModel);
    }
}
