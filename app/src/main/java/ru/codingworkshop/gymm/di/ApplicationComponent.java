package ru.codingworkshop.gymm.di;


import android.content.Context;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import ru.codingworkshop.gymm.App;

/**
 * Created by Радик on 31.05.2017.
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(App app);

    @Component.Builder
    interface Builder {
        ApplicationComponent build();
        @BindsInstance Builder injectContext(Context context);
    }
}
