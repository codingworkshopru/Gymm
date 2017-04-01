package ru.codingworkshop.gymm.program.activity.exercise.picker.exercises;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import ru.codingworkshop.gymm.data.GymDbHelper;
import ru.codingworkshop.gymm.data.model.Exercise;

/**
 * Created by Радик on 30.03.2017.
 */
class ExercisesAsyncLoader extends AsyncTaskLoader<List<Exercise>> {
    private final Bundle args;
    private List<Exercise> loadedExercises;

    public ExercisesAsyncLoader(Context context, Bundle args) {
        super(context);
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        if (loadedExercises == null)
            forceLoad();
        else
            deliverResult(loadedExercises);
    }

    @Override
    public List<Exercise> loadInBackground() {
        long muscleGroupId = args.getLong(ExercisesActivity.MUSCLE_GROUP_KEY);
        loadedExercises = new ArrayList<>();

        // first divider
        loadedExercises.add(new Exercise());

        SQLiteOpenHelper dbHelper = new GymDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        loadedExercises.addAll(Exercise.readByPrimaryMuscles(db, muscleGroupId));

        List<Exercise> secondaryMuscles = Exercise.readBySecondaryMuscles(db, muscleGroupId);
        if (!secondaryMuscles.isEmpty()) {
            // second divider
            loadedExercises.add(new Exercise());

            loadedExercises.addAll(secondaryMuscles);
        }

        return loadedExercises;
    }
}
