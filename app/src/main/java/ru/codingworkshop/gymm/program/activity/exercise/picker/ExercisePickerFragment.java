package ru.codingworkshop.gymm.program.activity.exercise.picker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.Exercise;
import ru.codingworkshop.gymm.data.model.MuscleGroup;
import ru.codingworkshop.gymm.info.exercise.ExerciseInfoActivity;

/**
 * Created by Радик on 03.04.2017.
 */

public class ExercisePickerFragment extends BottomSheetDialogFragment implements LoaderManager.LoaderCallbacks<List<Exercise>>, ExercisesAdapter.OnExerciseClickListener {

    private ExercisesAdapter mExercisesAdapter;
    private RecyclerView mExercisesList;
    private static final String TAG = ExercisePickerFragment.class.getSimpleName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MuscleGroup muscleGroup = getArguments().getParcelable(MusclesActivity.MUSCLE_GROUP_ARG);

        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setContentView(R.layout.fragment_exercise_picker);

        final Toolbar toolbar = (Toolbar) dialog.findViewById(R.id.fragment_exercise_picker_toolbar);
        toolbar.setTitle(muscleGroup.getName());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        final TextView title = (TextView) dialog.findViewById(R.id.fragment_exercise_picker_title);
        title.setText(muscleGroup.getName());


        mExercisesAdapter = new ExercisesAdapter(this, getContext());
        mExercisesList = (RecyclerView) dialog.findViewById(R.id.fragment_exercise_picker_exercises);
        mExercisesList.setAdapter(mExercisesAdapter);
        mExercisesList.setLayoutManager(new LinearLayoutManager(getContext()));

        FrameLayout frame = (FrameLayout) dialog.findViewById(android.support.design.R.id.design_bottom_sheet);
        BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(frame);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dialog.cancel();
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED && mExercisesList.computeVerticalScrollExtent() != mExercisesList.computeVerticalScrollRange()) {
                    title.setVisibility(View.GONE);
                    toolbar.setVisibility(View.VISIBLE);
                } else if (toolbar.getVisibility() == View.VISIBLE) {
                    toolbar.setVisibility(View.GONE);
                    title.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        // start loader to get exercises from db
        Bundle args = new Bundle(1);
        args.putLong(MusclesActivity.MUSCLE_GROUP_ARG, muscleGroup.getId());

        Loader loader = getLoaderManager().getLoader(0);
        if (loader == null)
            getLoaderManager().initLoader(0, args, this);
        else
            getLoaderManager().restartLoader(0, args, this);

        return dialog;
    }

    @Override
    public void onExerciseInfoClick(View view) {
        int clickedItemPosition = mExercisesList.getChildAdapterPosition(view);

        Intent intent = new Intent(getContext(), ExerciseInfoActivity.class);
        intent.putExtra(ExerciseInfoActivity.EXERCISE_ARG, mExercisesAdapter.getItem(clickedItemPosition));

        startActivity(intent);
    }

    @Override
    public void onExerciseClick(View view) {
        int clickedItemPosition = mExercisesList.getChildAdapterPosition(view);

        Intent intent = new Intent();
        intent.putExtra(ExerciseInfoActivity.EXERCISE_ARG, mExercisesAdapter.getItem(clickedItemPosition));
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public Loader<List<Exercise>> onCreateLoader(int id, Bundle args) {
        return new ExercisesAsyncLoader(getContext(), args);
    }

    @Override
    public void onLoadFinished(Loader<List<Exercise>> loader, List<Exercise> data) {
        mExercisesAdapter.setExercises(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Exercise>> loader) {

    }
}
