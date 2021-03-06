package ru.codingworkshop.gymm.db.initializer;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.codingworkshop.gymm.db.GymmDatabase;

/**
 * Created by Радик on 23.05.2017.
 */

@Singleton
public final class DatabaseInitializer implements Initializer {
    @VisibleForTesting
    public static final String DATABASE_NAME = "ExerciseEditor.db";

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
        db = Room.
                databaseBuilder(context, GymmDatabase.class, DATABASE_NAME)
                .build();
    }
}
