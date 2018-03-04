package ru.codingworkshop.gymm.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.codingworkshop.gymm.db.GymmDatabase;
import ru.codingworkshop.gymm.db.initializer.DatabaseInitializer;

/**
 * Created by Радик on 31.05.2017.
 */

@Module(
        includes = {
                InitializerModule.class,
                ViewModelModule.class,
                ActivityModule.class,
                FragmentModule.class,
                DatabaseModule.class
        }
)
class ApplicationModule {

    @Provides
    @Singleton
    GymmDatabase provideDatabase(DatabaseInitializer databaseInitializer) {
        return databaseInitializer.getDatabase();
    }
}
