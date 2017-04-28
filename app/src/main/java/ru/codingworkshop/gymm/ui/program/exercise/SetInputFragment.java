package ru.codingworkshop.gymm.ui.program.exercise;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
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
 * Created by Радик on 21.04.2017.
 */

public final class SetInputFragment extends DialogFragment
        implements DialogInterface.OnClickListener {

    interface SetInputDialogListener {
        void onSetCreated(ProgramSet model);

        void onSetModified(ProgramSet model);
    }

    private static final int REPS_MIN = 1;
    private static final int REPS_MAX = 100;

    private static final int MINUTES_MIN = 0;
    private static final int MINUTES_MAX = 60;

    private static final int SECONDS_MIN = 0;
    private static final int SECONDS_MAX = 59;
    private static final int SECONDS_STEP = 5;

    public static final String DIALOG_MODEL_KEY = ProgramSet.class.getCanonicalName();
    public static final String DIALOG_ACTION_KEY = "dialogActionKey";
    public static final int DIALOG_ACTION_CREATE = 0;
    public static final int DIALOG_ACTION_MODIFY = 1;

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

    public void create(FragmentManager manager) {
        Bundle args = new Bundle(1);
        args.putInt(DIALOG_ACTION_KEY, DIALOG_ACTION_CREATE);
        setArguments(args);
        show(manager, TAG);
    }

    public void modify(FragmentManager manager, ProgramSet set) {
        Bundle args = new Bundle(2);
        args.putInt(DIALOG_ACTION_KEY, DIALOG_ACTION_MODIFY);
        args.putParcelable(DIALOG_MODEL_KEY, set);
        setArguments(args);
        show(manager, TAG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();

        Bundle arguments = getArguments();
        if (arguments == null)
            throw new IllegalArgumentException("arguments bundle mustn't be null");

        int action = arguments.getInt(DIALOG_ACTION_KEY);

        @StringRes int title = R.string.program_exercise_activity_dialog_title_edit;

        if (savedInstanceState != null) {
            mModel = savedInstanceState.getParcelable(DIALOG_MODEL_KEY);
        } else if (action == DIALOG_ACTION_MODIFY) {
            mModel = arguments.getParcelable(DIALOG_MODEL_KEY);
        } else if (action == DIALOG_ACTION_CREATE) {
            mModel = new ProgramSetEntity();
            mModel.setReps(REPS_MIN);
            title = R.string.program_exercise_activity_dialog_title;
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
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
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null);

        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DIALOG_MODEL_KEY, mModel);
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

        switch (getArguments().getInt(DIALOG_ACTION_KEY)) {

            case DIALOG_ACTION_CREATE:
                mSetInputDialogListener.onSetCreated(mModel);
                break;

            case DIALOG_ACTION_MODIFY:
                mSetInputDialogListener.onSetModified(mModel);
                break;

            default:
                throw new IllegalStateException("Unknown dialog action");
        }
    }
}
