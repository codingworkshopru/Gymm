package ru.codingworkshop.gymm.ui.program.exercise;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.databinding.FragmentProgramSetEditorBinding;

public class ProgramSetEditorFragment extends DialogFragment {
    private static final String PROGRAM_SET_KEY = "programSetKey";
    private static final String TAG = ProgramSetEditorFragment.class.getName();

    @VisibleForTesting
    OnSetReturnListener listener;
    private ProgramSet model;

    public interface OnSetReturnListener {
        void onSetReturn(ProgramSet programSet);
    }

    public static ProgramSetEditorFragment newInstance(long programExerciseId) {
        final ProgramSet programSet = new ProgramSet();
        programSet.setProgramExerciseId(programExerciseId);
        programSet.setReps(1);
        return newInstance(programSet);
    }

    public static ProgramSetEditorFragment newInstance(@NonNull ProgramSet programSet) {
        ProgramSetEditorFragment fragment = new ProgramSetEditorFragment();

        Bundle bundle = new Bundle(1);
        bundle.putParcelable(PROGRAM_SET_KEY, programSet);
        fragment.setArguments(bundle);

        return fragment;
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, TAG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        model = getArguments().getParcelable(PROGRAM_SET_KEY);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        FragmentProgramSetEditorBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_program_set_editor, null, false);
        binding.setSet(model);

        return new AlertDialog.Builder(getContext())
                .setView(binding.getRoot())
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, this::onPositiveButtonClick)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    public void onPositiveButtonClick(DialogInterface dialog, int which) {
        listener.onSetReturn(model);
    }
}
