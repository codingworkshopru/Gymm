package ru.codingworkshop.gymm.program.activity.exercise;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;

import ru.codingworkshop.gymm.data.GymContract;
import ru.codingworkshop.gymm.data.GymDbHelper;

import static ru.codingworkshop.gymm.program.activity.exercise.ProgramExerciseActivity.EXERCISES_PROJECTION;

/**
 * Created by Радик on 25.02.2017.
 */

final class ExercisesAsyncLoader extends AsyncTaskLoader<Cursor> {
    Cursor mExercisesCursor;

    ExercisesAsyncLoader(Context context, Cursor exercisesCursor) {
        super(context);
        mExercisesCursor = exercisesCursor;
    }

    @Override
    protected void onStartLoading() {
        if (mExercisesCursor != null && !mExercisesCursor.isClosed())
            deliverResult(mExercisesCursor);
        else
            forceLoad();
    }

    @Override
    public Cursor loadInBackground() {
        GymDbHelper dbHelper = new GymDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query(GymContract.ExerciseEntry.TABLE_NAME, EXERCISES_PROJECTION, null, null, null, null, GymContract.ExerciseEntry.COLUMN_NAME);
    }
};
