package ru.codingworkshop.gymm.ui.program;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.ui.program.training.ProgramTrainingFragment;
import timber.log.Timber;

public class ProgramTrainingActivity extends AppCompatActivity implements HasSupportFragmentInjector {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static final String PROGRAM_TRAINING_ID_KEY = "programTrainingId";

    @Inject DispatchingAndroidInjector<Fragment> fragmentInjector;
    @Inject ViewModelProvider.Factory viewModelFactory;
    @Nullable private LiveData<ProgramTrainingTree> programTrainingTreeLiveData;

    public interface OnSystemBackPressedListener {
        void onFragmentClose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_training);

        Timber.d("onCreate");

        ProgramTrainingViewModel vm = ViewModelProviders.of(this, viewModelFactory).get(ProgramTrainingViewModel.class);

        long programTrainingId = getIntent().getLongExtra(PROGRAM_TRAINING_ID_KEY, 0L);
        if (programTrainingId == 0L) {
            addProgramTrainingFragment();
        } else {
            programTrainingTreeLiveData = vm.loadTree(programTrainingId);
            programTrainingTreeLiveData.observe(this, (unused) -> addProgramTrainingFragment());
        }
    }

    private void addProgramTrainingFragment() {
        if (programTrainingTreeLiveData != null) {
            programTrainingTreeLiveData.removeObservers(this);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(ProgramTrainingFragment.TAG) == null) {
            ProgramTrainingFragment programTrainingFragment = new ProgramTrainingFragment();
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
    public void onBackPressed() {
        for (Fragment f : getSupportFragmentManager().getFragments()) {
            if (f.isResumed() && f instanceof OnSystemBackPressedListener) {
                ((OnSystemBackPressedListener) f).onFragmentClose();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }
}
