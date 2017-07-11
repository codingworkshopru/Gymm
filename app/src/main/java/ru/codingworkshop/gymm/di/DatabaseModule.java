package ru.codingworkshop.gymm.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.codingworkshop.gymm.db.GymmDatabase;
import ru.codingworkshop.gymm.db.dao.ExerciseDao;
import ru.codingworkshop.gymm.db.dao.MuscleGroupDao;

/**
 * Created by Радик on 18.06.2017.
 */

@Module
class DatabaseModule {
    @Provides
    @Singleton
    ExerciseDao providesExerciseDao(GymmDatabase db) {
        return db.getExerciseDao();
    }

    @Provides
    @Singleton
    MuscleGroupDao providesMuscleGroupDao(GymmDatabase db) {
        return db.getMuscleGroupDao();
    }
}
