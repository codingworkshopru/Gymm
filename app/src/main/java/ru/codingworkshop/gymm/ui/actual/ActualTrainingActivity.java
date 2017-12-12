package ru.codingworkshop.gymm.ui.actual;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.ui.actual.exercise.ActualExercisesFragment;
import ru.codingworkshop.gymm.ui.actual.rest.ActualTrainingRestFragment;
import timber.log.Timber;

public class ActualTrainingActivity extends AppCompatActivity implements
        ActualExercisesFragment.ActualExercisesCallback,
        ActualTrainingRestFragment.ActualTrainingRestCallback,
        HasSupportFragmentInjector
{

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    @VisibleForTesting
    public Fragment actualExercisesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        setContentView(R.layout.activity_actual_training);
        super.onCreate(savedInstanceState);

        if (actualExercisesFragment == null) {
            actualExercisesFragment = new ActualExercisesFragment();
        }
        actualExercisesFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.actualTrainingFragmentContainer, actualExercisesFragment)
                .commit();
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
