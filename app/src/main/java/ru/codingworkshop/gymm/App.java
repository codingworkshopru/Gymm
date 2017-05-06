package ru.codingworkshop.gymm;

import android.app.Application;
import android.support.annotation.NonNull;

import com.squareup.leakcanary.LeakCanary;

import io.requery.Persistable;
import io.requery.sql.EntityDataStore;
import ru.codingworkshop.gymm.data.GymDatabaseSource;

/**
 * Created by Радик on 19.04.2017.
 */

public class App extends Application {
    private EntityDataStore<Persistable> dataStore;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this))
            return;

        LeakCanary.install(this);
    }

    @NonNull
    public EntityDataStore<Persistable> getData() {
        if (dataStore == null) {
            dataStore = GymDatabaseSource.createDataStore(this);
        }
        return dataStore;
    }
}
