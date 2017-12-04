package ru.codingworkshop.gymm.ui.program.exercise;


import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.support.AndroidSupportInjection;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.databinding.FragmentProgramSetEditorBinding;
import ru.codingworkshop.gymm.ui.program.ProgramTrainingViewModel;

public class ProgramSetEditorFragment extends DialogFragment {
    private static final String TAG = ProgramSetEditorFragment.class.getName();

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private FragmentProgramSetEditorBinding binding;

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, TAG);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        ProgramTrainingViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProgramTrainingViewModel.class);
        viewModel.getProgramSet().observe(this, set -> binding.setSet(set));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_program_set_editor, null, false);

        return new AlertDialog.Builder(getContext())
                .setView(binding.getRoot())
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, this::onPositiveButtonClick)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    public void onPositiveButtonClick(DialogInterface dialog, int which) {
        dismiss();
    }
}
