package ru.codingworkshop.gymm;

import android.app.Activity;
import android.support.multidex.MultiDexApplication;

import com.squareup.leakcanary.LeakCanary;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import ru.codingworkshop.gymm.di.DaggerApplicationComponent;
import timber.log.Timber;

/**
 * Created by Радик on 19.04.2017.
 */

public class App extends MultiDexApplication implements HasActivityInjector {
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
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}
