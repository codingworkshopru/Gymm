package ru.codingworkshop.gymm.ui.actual;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.ui.actual.exercise.ActualExercisesFragment;
import ru.codingworkshop.gymm.ui.actual.rest.ActualTrainingRestFragment;

public class ActualTrainingActivity extends AppCompatActivity implements
        ActualExercisesFragment.ActualExercisesCallback,
        ActualTrainingRestFragment.ActualTrainingRestCallback, HasSupportFragmentInjector
{

    public static final String ACTUAL_EXERCISES_FRAGMENT_TAG = "actualExercisesFragmentTag";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;
    @VisibleForTesting
    Fragment actualExercisesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_training);

        actualExercisesFragment = getSupportFragmentManager().findFragmentByTag(ACTUAL_EXERCISES_FRAGMENT_TAG);
        if (actualExercisesFragment == null) {
            actualExercisesFragment = new ActualExercisesFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.actualTrainingFragmentContainer, actualExercisesFragment, ACTUAL_EXERCISES_FRAGMENT_TAG)
                    .commit();
        }
        actualExercisesFragment.setArguments(getIntent().getExtras());
    }

    @Override
    public void onStartRest(int restSeconds) {
        final ActualTrainingRestFragment fragment = new ActualTrainingRestFragment();
        fragment.setRestTime(restSeconds * 1000);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.actualTrainingFragmentContainer, fragment)
                .commit();
    }

    @Override
    public void onRestStopped() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.actualTrainingFragmentContainer, actualExercisesFragment)
                .commit();
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }
}
