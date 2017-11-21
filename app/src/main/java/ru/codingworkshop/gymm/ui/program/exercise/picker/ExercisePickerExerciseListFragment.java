package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.List;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.databinding.FragmentExercisePickerListItemBinding;
import ru.codingworkshop.gymm.ui.common.ClickableBindingListAdapter;
import ru.codingworkshop.gymm.ui.common.ListItemListeners;

/**
 * Created by Radik on 21.11.2017.
 */

public class ExercisePickerExerciseListFragment extends BottomSheetDialogFragment {
    private static final String MUSCLE_GROUP_ID_KEY = "muscleGroupIdKey";
    private static final String MUSCLE_GROUP_NAME_KEY = "muscleGroupNameKey";

    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
    }

    @VisibleForTesting
    ViewModelProvider.Factory viewModelFactory;
    @VisibleForTesting
    OnExerciseClickListener exerciseClickListener;
    private ExercisePickerExerciseListFragmentViewModel viewModel;
    private RecyclerView recyclerView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        viewModel = viewModelFactory.create(ExercisePickerExerciseListFragmentViewModel.class);

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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setContentView(R.layout.fragment_exercise_picker);

        Toolbar toolbar = dialog.findViewById(R.id.fragment_exercise_picker_toolbar);
        if (toolbar != null) {
            toolbar.setTitle(getArguments().getString(MUSCLE_GROUP_NAME_KEY));
        }

        viewModel.load(getArguments().getLong(MUSCLE_GROUP_ID_KEY)).observe(this, this::onExercisesLoaded);

        recyclerView = dialog.findViewById(R.id.fragment_exercise_picker_exercises);

        return dialog;
    }

    private void onExercisesLoaded(List<Exercise> exercises) {
        if (exercises != null && !exercises.isEmpty()) {
            ListItemListeners listItemListeners = new ListItemListeners(R.layout.fragment_exercise_picker_list_item);
            listItemListeners.setOnClickListener(v -> {
                Exercise exercise = ((FragmentExercisePickerListItemBinding) DataBindingUtil.getBinding(v)).getExercise();
                exerciseClickListener.onExerciseClick(exercise);
            });
            ClickableBindingListAdapter<Exercise, FragmentExercisePickerListItemBinding> adapter = new ClickableBindingListAdapter<Exercise, FragmentExercisePickerListItemBinding>(exercises, listItemListeners) {
                @Override
                protected void bind(FragmentExercisePickerListItemBinding binding, Exercise item) {
                    binding.setExercise(item);
                }
            };
            recyclerView.setAdapter(adapter);
        }
    }

    public static ExercisePickerExerciseListFragment newInstance(long muscleGroupId, String title) {
        Bundle args = new Bundle();
        args.putLong(MUSCLE_GROUP_ID_KEY, muscleGroupId);
        args.putString(MUSCLE_GROUP_NAME_KEY, title);
        ExercisePickerExerciseListFragment fragment = new ExercisePickerExerciseListFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
