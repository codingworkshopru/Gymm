package ru.codingworkshop.gymm.program.activity.exercise.picker.muscles;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.List;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.MuscleGroup;
import ru.codingworkshop.gymm.program.activity.exercise.picker.exercises.ExercisesActivity;

public class MusclesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<MuscleGroup>>, PlaceholderFragment.OnMuscleGroupSelectListener {

    private static final String TAG = MusclesActivity.class.getSimpleName();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muscles);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader lastLoader = loaderManager.getLoader(0);
        if (lastLoader != null)
            loaderManager.restartLoader(0, null, this);
        else
            loaderManager.initLoader(0, null, this);
    }

    @Override
    public Loader<List<MuscleGroup>> onCreateLoader(int id, Bundle args) {
        return new MuscleGroupsLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<MuscleGroup>> loader, List<MuscleGroup> data) {
        if (mSectionsPagerAdapter == null) {
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), data);

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<MuscleGroup>> loader) {

    }

    @Override
    public void onMuscleGroupSelect(MuscleGroup muscleGroup) {
        Intent intent = new Intent(this, ExercisesActivity.class);
        intent.putExtra(ExercisesActivity.MUSCLE_GROUP_ARG, muscleGroup.getId());
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(RESULT_OK, data);
        finish();
    }
}
