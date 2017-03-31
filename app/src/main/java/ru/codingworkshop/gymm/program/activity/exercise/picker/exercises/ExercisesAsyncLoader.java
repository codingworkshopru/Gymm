package ru.codingworkshop.gymm.program.activity.exercise.picker.exercises;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import ru.codingworkshop.gymm.data.GymContract;
import ru.codingworkshop.gymm.data.GymDbHelper;
import ru.codingworkshop.gymm.data.QueryBuilder;

/**
 * Created by Радик on 30.03.2017.
 */
class ExercisesAsyncLoader extends AsyncTaskLoader<Cursor> {
    private final Bundle args;
    private Cursor loadedCursor;

    public ExercisesAsyncLoader(Context context, Bundle args) {
        super(context);
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        if (loadedCursor == null)
            forceLoad();
        else
            deliverResult(loadedCursor);
    }

    @Override
    public Cursor loadInBackground() {
        final String groupOrder = "group_order";

        QueryBuilder.QueryPart exercisesWithPrimaryMuscles = new QueryBuilder.QueryPart(GymContract.ExerciseEntry.TABLE_NAME)
                .setColumns(GymContract.ExerciseEntry._ID, GymContract.ExerciseEntry.COLUMN_NAME, "0 AS " + groupOrder)
                .setSelection(GymContract.ExerciseEntry.COLUMN_PRIMARY_MUSCLE_GROUP_ID + "=?");

        QueryBuilder.QueryPart exercisesWithSecondaryMuscles = new QueryBuilder.QueryPart(GymContract.ExerciseEntry.TABLE_NAME)
                .setColumns(GymContract.ExerciseEntry._ID, GymContract.ExerciseEntry.COLUMN_NAME, "1 AS " + groupOrder);

        QueryBuilder.QueryPart exercisesMusclesLink = new QueryBuilder.QueryPart(GymContract.SecondaryMuscleGroupLinkEntry.TABLE_NAME)
                .setThisJoinColumn(GymContract.SecondaryMuscleGroupLinkEntry.COLUMN_EXERCISE_ID)
                .setOtherJoinColumn(GymContract.ExerciseEntry._ID)
                .setSelection(GymContract.SecondaryMuscleGroupLinkEntry.COLUMN_MUSCLE_GROUP_ID + "=?");

        SQLiteOpenHelper dbHelper = new GymDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String argument = String.valueOf(args.getLong(ExercisesActivity.MUSCLE_GROUP_KEY));
        String selectionArgs[] = {argument, argument};

        String dividerQuery = "SELECT 0, \"\"";

        loadedCursor = db.rawQuery(
                dividerQuery + ",0 UNION " +
                        QueryBuilder.build(exercisesWithPrimaryMuscles) + " UNION " +
                        dividerQuery + ",1 UNION " +
                        QueryBuilder.build(exercisesWithSecondaryMuscles, exercisesMusclesLink) +
                        " ORDER BY " + groupOrder + "," + QueryBuilder.getColumnWithTable(GymContract.ExerciseEntry.COLUMN_NAME, GymContract.ExerciseEntry.TABLE_NAME),
                selectionArgs
        );

        return loadedCursor;
    }
}
