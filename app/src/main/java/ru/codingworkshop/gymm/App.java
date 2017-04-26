package ru.codingworkshop.gymm;

import android.app.Application;
import android.support.annotation.NonNull;

import io.requery.Persistable;
import io.requery.sql.EntityDataStore;
import ru.codingworkshop.gymm.data.GymDatabaseSource;

/**
 * Created by Радик on 19.04.2017.
 */

public class App extends Application {
    private EntityDataStore<Persistable> dataStore;

    @NonNull
    public EntityDataStore<Persistable> getData() {
        if (dataStore == null) {
            dataStore = GymDatabaseSource.createDataStore(this);
        }
        return dataStore;
    }
}
