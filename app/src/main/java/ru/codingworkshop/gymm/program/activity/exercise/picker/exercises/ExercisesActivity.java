package ru.codingworkshop.gymm.program.activity.exercise.picker.exercises;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.Exercise;
import ru.codingworkshop.gymm.data.model.MuscleGroup;
import ru.codingworkshop.gymm.info.exercise.ExerciseInfoActivity;

public class ExercisesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Exercise>>, ExercisesAdapter.OnItemClickListener {

    private RecyclerView mPickerExercises;
    private ExercisesAdapter mExercisesAdapter;

    private static final String TAG = ExercisesActivity.class.getSimpleName();
    public static final String MUSCLE_GROUP_ARG = MuscleGroup.class.getCanonicalName();
    static final String MUSCLE_GROUP_KEY = MUSCLE_GROUP_ARG + "_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        Toolbar toolbar = (Toolbar) findViewById(R.id.picker_exercises_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mExercisesAdapter = new ExercisesAdapter(this, this);
        mPickerExercises = (RecyclerView) findViewById(R.id.picker_exercises_list);
        mPickerExercises.setAdapter(mExercisesAdapter);
        mPickerExercises.setLayoutManager(new LinearLayoutManager(this));
        mPickerExercises.setHasFixedSize(true);

        Bundle args = new Bundle(1);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(MUSCLE_GROUP_ARG)) {
            args.putLong(MUSCLE_GROUP_KEY, intent.getLongExtra(MUSCLE_GROUP_ARG, 0));
        } else {
            throw new IllegalArgumentException("No argument for " + this.getClass().getName());
        }

        Loader loader = getSupportLoaderManager().getLoader(0);
        if (loader == null)
            getSupportLoaderManager().initLoader(0, args, this);
        else
            getSupportLoaderManager().restartLoader(0, args, this);
    }

    @Override
    public Loader<List<Exercise>> onCreateLoader(int id, final Bundle args) {
        return new ExercisesAsyncLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<List<Exercise>> loader, List<Exercise> data) {
        mExercisesAdapter.setExercises(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Exercise>> loader) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onInfoButtonClick(View view) {
        int clickedItemPosition = mPickerExercises.getChildAdapterPosition(view);

        Intent intent = new Intent(this, ExerciseInfoActivity.class);
        intent.putExtra(ExerciseInfoActivity.EXERCISE_ARG, mExercisesAdapter.getItem(clickedItemPosition));

        startActivity(intent);
    }

    @Override
    public void onItemClick(View view) {
        int clickedItemPosition = mPickerExercises.getChildAdapterPosition(view);

        Intent intent = new Intent();
        intent.putExtra(ExerciseInfoActivity.EXERCISE_ARG, mExercisesAdapter.getItem(clickedItemPosition));
        setResult(RESULT_OK, intent);
        finish();
    }
}
