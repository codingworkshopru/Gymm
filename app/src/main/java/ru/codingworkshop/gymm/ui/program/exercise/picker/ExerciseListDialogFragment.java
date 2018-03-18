package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.databinding.FragmentExercisePickerListItemBinding;
import ru.codingworkshop.gymm.ui.common.ClickableBindingListAdapter;
import ru.codingworkshop.gymm.ui.common.ListItemListeners;
import ru.codingworkshop.gymm.ui.info.exercise.ExerciseInfoActivity;

/**
 * Created by Radik on 21.11.2017.
 */

public class ExerciseListDialogFragment extends BottomSheetDialogFragment {
    private static final String MUSCLE_GROUP_ID_KEY = "muscleGroupIdKey";
    private static final String MUSCLE_GROUP_NAME_KEY = "muscleGroupNameKey";

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ExercisePickerViewModel viewModel;
    private RecyclerView recyclerView;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(ExercisePickerViewModel.class);
        viewModel.getExercisesForMuscleGroup().observe(this, this::onExercisesLoaded);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        viewModel.clearMuscleGroup();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setContentView(R.layout.fragment_exercise_picker);

        Toolbar toolbar = dialog.findViewById(R.id.fragment_exercise_picker_toolbar);
        if (toolbar != null) {
            toolbar.setTitle(getArguments().getString(MUSCLE_GROUP_NAME_KEY));
        }

        recyclerView = dialog.findViewById(R.id.fragment_exercise_picker_exercises);

        return dialog;
    }

    private void onExercisesLoaded(List<Exercise> exercises) {
        if (exercises != null && !exercises.isEmpty()) {
            ListItemListeners listItemListeners = new ListItemListeners(R.layout.fragment_exercise_picker_list_item)
                    .setOnClickListener(this::onExerciseClick)
                    .setOnButtonClickListener(this::onExerciseInfoButtonClick, R.id.exerciseListItemInfoButton);
            ClickableBindingListAdapter<Exercise, FragmentExercisePickerListItemBinding> adapter = new ClickableBindingListAdapter<Exercise, FragmentExercisePickerListItemBinding>(exercises, listItemListeners) {
                @Override
                protected void bind(FragmentExercisePickerListItemBinding binding, Exercise item) {
                    binding.setExercise(item);
                }
            };
            recyclerView.setAdapter(adapter);
        }
    }

    private void onExerciseInfoButtonClick(View view) {
        FragmentExercisePickerListItemBinding binding = DataBindingUtil.findBinding(view);
        Intent exerciseInfoStart = new Intent(getContext(), ExerciseInfoActivity.class);
        exerciseInfoStart.putExtra(ExerciseInfoActivity.EXERCISE_ID_KEY, binding.getExercise().getId());
        startActivity(exerciseInfoStart);
    }

    private void onExerciseClick(View v) {
        Exercise exercise = ((FragmentExercisePickerListItemBinding) DataBindingUtil.getBinding(v)).getExercise();
        viewModel.setExercise(exercise);
    }

    public static ExerciseListDialogFragment newInstance(long muscleGroupId, String title) {
        Bundle args = new Bundle();
        args.putLong(MUSCLE_GROUP_ID_KEY, muscleGroupId);
        args.putString(MUSCLE_GROUP_NAME_KEY, title);
        ExerciseListDialogFragment fragment = new ExerciseListDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
