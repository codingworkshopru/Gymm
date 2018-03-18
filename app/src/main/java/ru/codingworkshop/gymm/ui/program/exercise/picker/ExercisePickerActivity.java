package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.ui.program.exercise.ProgramExerciseFragment;

public class ExercisePickerActivity extends AppCompatActivity implements
        HasSupportFragmentInjector
{

    @Inject DispatchingAndroidInjector<Fragment> fragmentInjector;
    @Inject ViewModelProvider.Factory viewModelFactory;

    private static final String EXERCISE_LIST_DIALOG_TAG = "exerciseListDialogTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        ExercisePickerViewModel viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ExercisePickerViewModel.class);
        viewModel.getMuscleGroup().observe(this, this::onMuscleGroupPicked);
        viewModel.getExercise().observe(this, this::onExercisePicked);

        setContentView(R.layout.activity_muscles);

        setSupportActionBar(findViewById(R.id.exercisePickerToolbar));
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }


        ViewPager viewPager = findViewById(R.id.exercisePickerHumanBodyContainer);
        if (viewPager != null) {
            viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    return MuscleGroupPickerFragment.newInstance(position == 0);
                }

                @Override
                public int getCount() {
                    return 2;
                }
            });

            TabLayout tabLayout = findViewById(R.id.exercisePickerHumanBodyTabs);

            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.exercisePickerHumanBodyAnterior, MuscleGroupPickerFragment.newInstance(true))
                    .replace(R.id.exercisePickerHumanBodyPosterior, MuscleGroupPickerFragment.newInstance(false))
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onMuscleGroupPicked(@Nullable MuscleGroup muscleGroup) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ExerciseListDialogFragment fragment = (ExerciseListDialogFragment) fragmentManager.findFragmentByTag(EXERCISE_LIST_DIALOG_TAG);
        if (fragment != null) {
            fragment.dismiss();
        }
        if (muscleGroup != null) {
            fragment = ExerciseListDialogFragment.newInstance(muscleGroup.getId(), muscleGroup.getName());
            fragment.show(fragmentManager, EXERCISE_LIST_DIALOG_TAG);
        }
    }

    public void onExercisePicked(Exercise exercise) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(ProgramExerciseFragment.EXERCISE_ID_KEY, exercise.getId());
        resultIntent.putExtra(ProgramExerciseFragment.EXERCISE_NAME_KEY, exercise.getName());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }
}
