package ru.codingworkshop.gymm.di;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntKey;
import dagger.multibindings.IntoMap;
import ru.codingworkshop.gymm.db.initializer.DatabaseInitializer;
import ru.codingworkshop.gymm.db.initializer.ExerciseInitializer;
import ru.codingworkshop.gymm.db.initializer.Initializer;
import ru.codingworkshop.gymm.db.initializer.MuscleGroupInitializer;

/**
 * Created by Радик on 18.06.2017.
 */

@Module
abstract class InitializerModule {
    @Binds
    @IntoMap
    @IntKey(0)
    abstract Initializer bindsDatabaseInitializer(DatabaseInitializer databaseInitializer);

    @Binds
    @IntoMap
    @IntKey(1)
    abstract Initializer bindsMuscleGroupInitializer(MuscleGroupInitializer muscleGroupInitializer);

    @Binds
    @IntoMap
    @IntKey(2)
    abstract Initializer bindsExerciseInitializer(ExerciseInitializer exerciseInitializer);
}
