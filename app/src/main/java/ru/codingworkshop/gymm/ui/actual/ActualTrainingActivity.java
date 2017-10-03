package ru.codingworkshop.gymm.ui.actual;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.ui.actual.exercise.ActualExercisesFragment;
import ru.codingworkshop.gymm.ui.actual.rest.ActualTrainingRestFragment;
import ru.codingworkshop.gymm.ui.common.LoadingFragment;
import timber.log.Timber;

public class ActualTrainingActivity extends AppCompatActivity implements
        ActualExercisesFragment.ActualExercisesCallback,
        ActualTrainingRestFragment.ActualTrainingRestCallback{

    private LoadingFragment loadingFragment;

    @VisibleForTesting()
    public Fragment actualExercisesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_actual_training);
        super.onCreate(savedInstanceState);

        loadingFragment = new LoadingFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.actualTrainingFragmentContainer, loadingFragment)
                .commit();

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
    public void onLoadingFinished() {
        Timber.d("onLoadingFinished");

        getSupportFragmentManager()
                .beginTransaction()
                .remove(loadingFragment)
                .commit();
    }

    @Override
    public void onRestStopped() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.actualTrainingFragmentContainer, actualExercisesFragment)
                .commit();
    }
}
