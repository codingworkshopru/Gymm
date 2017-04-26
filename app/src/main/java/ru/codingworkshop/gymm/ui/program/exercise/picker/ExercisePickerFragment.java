package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetBehavior.State;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.MuscleGroup;

/**
 * Created by Радик on 23.04.2017.
 */

public final class ExercisePickerFragment extends BottomSheetDialogFragment {

    private final static String MUSCLE_GROUP_MODEL = MuscleGroup.class.getCanonicalName();

    private final static String TAG = ExercisePickerFragment.class.getSimpleName();

    @NonNull
    public static ExercisePickerFragment newInstance(@NonNull MuscleGroup muscleGroup) {
        Bundle args = new Bundle(1);
        args.putParcelable(MUSCLE_GROUP_MODEL, muscleGroup);
        ExercisePickerFragment fragment = new ExercisePickerFragment();

        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MuscleGroup muscleGroup = getArguments().getParcelable(MUSCLE_GROUP_MODEL);

        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setContentView(R.layout.fragment_exercise_picker);

        Toolbar toolbar = (Toolbar) dialog.findViewById(R.id.fragment_exercise_picker_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        RecyclerView exercises = (RecyclerView) dialog.findViewById(R.id.fragment_exercise_picker_exercises);
        exercises.setLayoutManager(new LinearLayoutManager(getContext()));
        exercises.setAdapter(new ExercisePickerAdapter(muscleGroup, (ExercisePickerAdapter.OnExerciseClickListener)getActivity()));

        FrameLayout frame = (FrameLayout) dialog.findViewById(android.support.design.R.id.design_bottom_sheet);
        BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(frame);
        behavior.setBottomSheetCallback(new BottomSheetHeaderChanger(dialog, toolbar, exercises, muscleGroup.getName()));

        return dialog;
    }

    private static final class BottomSheetHeaderChanger extends BottomSheetBehavior.BottomSheetCallback {

        private Dialog dialog;
        private TextView titleView;
        private Toolbar toolbar;
        private RecyclerView exercisesView;

        BottomSheetHeaderChanger(@NonNull Dialog dialog, @NonNull Toolbar toolbar, @NonNull RecyclerView exercisesView, @NonNull String title) {
            this.dialog = dialog;
            this.titleView = (TextView) dialog.findViewById(R.id.fragment_exercise_picker_title);
            this.toolbar = toolbar;
            this.exercisesView = exercisesView;

            this.toolbar.setTitle(title);
            this.titleView.setText(title);
        }

        private void setToolbarVisible(boolean visible) {
            if (visible == (toolbar.getVisibility() == View.VISIBLE))
                return;

            toolbar.setVisibility(visible ? View.VISIBLE : View.GONE);
            titleView.setVisibility(visible ? View.GONE : View.VISIBLE);
            exercisesView.setBackgroundResource(visible ? R.drawable.bottom_shadow : 0);
        }

        @Override
        public void onStateChanged(@NonNull View bottomSheet, @State int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dialog.cancel();
            } else {
                setToolbarVisible(newState == BottomSheetBehavior.STATE_EXPANDED && bottomSheet.getTop() == 0);
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    }
}
