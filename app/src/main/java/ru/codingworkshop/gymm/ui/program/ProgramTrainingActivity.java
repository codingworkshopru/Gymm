package ru.codingworkshop.gymm.ui.program;

import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;
import dagger.android.support.HasSupportFragmentInjector;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.ui.program.exercise.ProgramExerciseFragment;
import ru.codingworkshop.gymm.ui.program.training.ProgramTrainingFragment;
import timber.log.Timber;

public class ProgramTrainingActivity extends AppCompatActivity implements HasSupportFragmentInjector {
    @Inject DispatchingAndroidInjector<Fragment> fragmentInjector;

    @VisibleForTesting
    ProgramTrainingFragment programTrainingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_training);

        Timber.d("onCreate");

        FragmentManager fragmentManager = getSupportFragmentManager();
        programTrainingFragment = (ProgramTrainingFragment) fragmentManager.findFragmentByTag(ProgramTrainingFragment.TAG);
        if (programTrainingFragment == null) {
            programTrainingFragment = ProgramTrainingFragment.newInstance();
            programTrainingFragment.setArguments(getIntent().getExtras());
            fragmentManager
                    .beginTransaction()
                    .add(R.id.programTrainingFragmentContainer, programTrainingFragment, ProgramTrainingFragment.TAG)
                    .commit();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Timber.d("onStop");
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }
}
