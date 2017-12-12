package ru.codingworkshop.gymm.ui.program;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.db.GymmDatabase;
import ru.codingworkshop.gymm.ui.program.training.ProgramTrainingFragment;
import timber.log.Timber;

public class ProgramTrainingActivity extends AppCompatActivity implements HasSupportFragmentInjector {
    public static final String PROGRAM_TRAINING_ID_KEY = "programTrainingId";

    @Inject DispatchingAndroidInjector<Fragment> fragmentInjector;
    @Inject ViewModelProvider.Factory viewModelFactory;

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
        if (GymmDatabase.isValidId(vm.getProgramTrainingTree().getParent()) || programTrainingId == 0L) {
            addProgramTrainingFragment();
        } else {
            LiveDataUtil.getOnce(vm.loadTree(programTrainingId), unused -> addProgramTrainingFragment());
        }
    }

    private void addProgramTrainingFragment() {
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
