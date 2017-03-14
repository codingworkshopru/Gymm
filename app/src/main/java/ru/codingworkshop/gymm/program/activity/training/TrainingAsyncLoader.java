package ru.codingworkshop.gymm.program.activity.training;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import ru.codingworkshop.gymm.MainActivity;
import ru.codingworkshop.gymm.data.GymDbHelper;
import ru.codingworkshop.gymm.data.model.ProgramTraining;

/**
 * Created by Радик on 08.03.2017.
 */

public class TrainingAsyncLoader extends AsyncTaskLoader<ProgramTraining> {
    private final int id;
    private final Bundle args;
    private ProgramTraining model;

    public static final int LOADER_TRAINING_LOAD = 0;
    static final int LOADER_TRAINING_SAVE = 1;

    public TrainingAsyncLoader(Context context, int id, Bundle args) {
        super(context);

        this.id = id;
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        if (id != LOADER_TRAINING_LOAD || model == null)
            forceLoad();
        else
            deliverResult(null);
    }

    @Override
    public ProgramTraining loadInBackground() {
        GymDbHelper dbHelper = new GymDbHelper(getContext());
        if (id == LOADER_TRAINING_SAVE) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            model.create(db, 0);
            model.update(db);
        } else if (id == LOADER_TRAINING_LOAD) {
            model = ProgramTraining.load(dbHelper.getReadableDatabase(), args.getLong(MainActivity.PROGRAM_TRAINING_ID_KEY));
            return model;
        }
        return null;
    }
}
