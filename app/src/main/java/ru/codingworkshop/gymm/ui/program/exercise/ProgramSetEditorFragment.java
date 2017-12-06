package ru.codingworkshop.gymm.ui.program.exercise;


import android.app.Dialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.databinding.FragmentProgramSetEditorBinding;
import ru.codingworkshop.gymm.ui.program.ProgramTrainingViewModel;
import timber.log.Timber;

public class ProgramSetEditorFragment extends DialogFragment {
    private static final String TAG = ProgramSetEditorFragment.class.getName();

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private FragmentProgramSetEditorBinding binding;
    private ProgramTrainingViewModel viewModel;

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, TAG);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        viewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(ProgramTrainingViewModel.class);
        viewModel.getProgramSet().observe(this, set -> binding.setSet(set));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_program_set_editor, null, false);

        return new AlertDialog.Builder(getContext())
                .setView(binding.getRoot())
                .setPositiveButton(android.R.string.ok, this::onDialogButtonClick)
                .setNegativeButton(android.R.string.cancel, this::onDialogButtonClick)
                .create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        viewModel.setProgramSet(null);
    }

    public void onDialogButtonClick(DialogInterface dialog, int which) {
        if (which == AlertDialog.BUTTON_NEGATIVE) {
            onCancel(dialog);
        }
        dismiss();
    }
}
