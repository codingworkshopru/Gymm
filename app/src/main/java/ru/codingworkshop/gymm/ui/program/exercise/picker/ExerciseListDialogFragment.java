package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
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
import ru.codingworkshop.gymm.ui.info.exercise.ExerciseInfoFragment;

/**
 * Created by Radik on 21.11.2017.
 */

public class ExerciseListDialogFragment extends BottomSheetDialogFragment {
    private static final String MUSCLE_GROUP_ID_KEY = "muscleGroupIdKey";
    private static final String MUSCLE_GROUP_NAME_KEY = "muscleGroupNameKey";

    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
    }

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @VisibleForTesting
    OnExerciseClickListener exerciseClickListener;
    private ExerciseListDialogViewModel viewModel;
    private RecyclerView recyclerView;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);

        viewModel = viewModelFactory.create(ExerciseListDialogViewModel.class);
        viewModel.load(getArguments().getLong(MUSCLE_GROUP_ID_KEY)).observe(this, this::onExercisesLoaded);

        if (exerciseClickListener == null) {
            if (context instanceof OnExerciseClickListener) {
                exerciseClickListener = (OnExerciseClickListener) context;
            } else if (getParentFragment() instanceof OnExerciseClickListener) {
                exerciseClickListener = (OnExerciseClickListener) getParentFragment();
            } else {
                throw new IllegalStateException("Parent must implement " + OnExerciseClickListener.class + " interface");
            }
        }
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
            ListItemListeners listItemListeners = new ListItemListeners(R.layout.fragment_exercise_picker_list_item);
            listItemListeners.setOnClickListener(this::onExerciseClick);
            listItemListeners.setOnButtonClickListener(this::onExerciseInfoButtonClick, R.id.exerciseListItemInfoButton);
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
        ExerciseInfoFragment fragment = ExerciseInfoFragment.newInstance(binding.getExercise().getId());
        fragment.show(getChildFragmentManager(), ExerciseInfoFragment.TAG);
    }

    private void onExerciseClick(View v) {
        Exercise exercise = ((FragmentExercisePickerListItemBinding) DataBindingUtil.getBinding(v)).getExercise();
        exerciseClickListener.onExerciseClick(exercise);
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
