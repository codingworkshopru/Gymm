package ru.codingworkshop.gymm.db.initializer;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase.Builder;
import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.codingworkshop.gymm.BuildConfig;
import ru.codingworkshop.gymm.db.GymmDatabase;

/**
 * Created by Радик on 23.05.2017.
 */

@Singleton
public final class DatabaseInitializer implements Initializer {
    private static final String DATABASE_NAME = "ExerciseEditor.db";

    private GymmDatabase db;
    private Context context;

    @Inject
    DatabaseInitializer(Context context) {
        this.context = context;
    }


    public GymmDatabase getDatabase() {
        return db;
    }

    @Override
    public void initialize() {
        if (BuildConfig.DEBUG) {
            this.context.deleteDatabase(DATABASE_NAME);
        }

        Builder<GymmDatabase> dbBuilder = Room.databaseBuilder(this.context, GymmDatabase.class, DATABASE_NAME);
        db = dbBuilder.build();
    }
}
