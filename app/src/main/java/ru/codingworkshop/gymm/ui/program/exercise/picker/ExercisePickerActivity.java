package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.ui.program.exercise.ProgramExerciseFragment;
import timber.log.Timber;

public class ExercisePickerActivity extends AppCompatActivity implements
        HasSupportFragmentInjector,
        MuscleGroupPickerFragment.OnMuscleGroupPickListener,
        ExerciseListDialogFragment.OnExerciseClickListener
{

    @Inject DispatchingAndroidInjector<Fragment> fragmentInjector;

    private static final String EXERCISE_LIST_DIALOG_TAG = "exerciseListDialogTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muscles);
        Timber.d("onCreate");
        ViewPager viewPager = findViewById(R.id.exercisePickerHumanBodyContainer);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return MuscleGroupPickerFragment.newInstance(position == 0);
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return getString(position == 0 ? R.string.muscles_activity_anterior : R.string.muscles_activity_anterior);
            }
        });

        TabLayout tabLayout = findViewById(R.id.exercisePickerHumanBodyTabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onMuscleGroupPick(MuscleGroup muscleGroup) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ExerciseListDialogFragment fragment = (ExerciseListDialogFragment) fragmentManager.findFragmentByTag(EXERCISE_LIST_DIALOG_TAG);
        if (fragment != null) {
            fragment.dismiss();
        }
        fragment = ExerciseListDialogFragment.newInstance(muscleGroup.getId(), muscleGroup.getName());
        fragment.show(fragmentManager, EXERCISE_LIST_DIALOG_TAG);
    }

    @Override
    public void onExerciseClick(Exercise exercise) {
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
