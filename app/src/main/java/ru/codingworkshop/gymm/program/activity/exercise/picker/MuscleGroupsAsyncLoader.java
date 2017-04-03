package ru.codingworkshop.gymm.program.activity.exercise.picker;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import ru.codingworkshop.gymm.data.GymDbHelper;
import ru.codingworkshop.gymm.data.model.MuscleGroup;

/**
 * Created by Радик on 21.03.2017.
 */

public class MuscleGroupsAsyncLoader extends AsyncTaskLoader<List<MuscleGroup>> {
    private List<MuscleGroup> loadedMuscles;

    MuscleGroupsAsyncLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (loadedMuscles == null)
            forceLoad();
        else
            deliverResult(loadedMuscles);
    }

    @Override
    public List<MuscleGroup> loadInBackground() {
        SQLiteOpenHelper dbHelper = new GymDbHelper(getContext());
        loadedMuscles = MuscleGroup.read(dbHelper.getReadableDatabase());
        return loadedMuscles;
    }
}
