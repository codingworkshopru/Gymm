package ru.codingworkshop.gymm.testing;

import android.support.v4.app.Fragment;

import dagger.android.AndroidInjector;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.ui.actual.ActualTrainingActivity;

/**
 * Created by Радик on 02.10.2017 as part of the Gymm project.
 */

public class ActualTrainingActivityInjectedFragments extends ActualTrainingActivity {
    public static Fragment fragment;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.actualTrainingFragmentContainer, fragment, ACTUAL_EXERCISES_FRAGMENT_TAG)
                .commitNow();
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return instance -> {};
    }
}
