package ru.codingworkshop.gymm;

import android.app.Activity;
import android.app.Application;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import ru.codingworkshop.gymm.di.DaggerApplicationComponent;
import timber.log.Timber;

//import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Радик on 19.04.2017.
 */

public class App extends Application implements HasActivityInjector {
    @Inject DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;
    @Inject AppInitializer initializer;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        DaggerApplicationComponent.builder()
                .injectContext(getApplicationContext())
                .build()
                .inject(this);

        initializer.initialize();

        installLeakCanary();
    }

    private void installLeakCanary() {
//        if (!LeakCanary.isInAnalyzerProcess(this))
//            LeakCanary.install(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}
