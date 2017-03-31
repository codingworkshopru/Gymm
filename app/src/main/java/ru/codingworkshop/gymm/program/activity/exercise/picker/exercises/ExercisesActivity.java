package ru.codingworkshop.gymm.program.activity.exercise.picker.exercises;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.MuscleGroup;
import ru.codingworkshop.gymm.info.exercise.ExerciseInfoActivity;

public class ExercisesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, ExercisesAdapter.OnItemClickListener {

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

        mExercisesAdapter = new ExercisesAdapter(this);
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
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        return new ExercisesAsyncLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        while (data.moveToNext()) {
            Log.d(TAG, "id=" + data.getLong(0) + " " + data.getString(1));
        }
        mExercisesAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onInfoButtonClick(View view) {
        long clickedItemId = mPickerExercises.getChildItemId(view);

        Intent intent = new Intent(this, ExerciseInfoActivity.class);
        intent.putExtra(ExerciseInfoActivity.EXERCISE_ARG, clickedItemId);

        startActivity(intent);
    }

    @Override
    public void onItemClick(View view) {
        long clickedItemId = mPickerExercises.getChildItemId(view);

        Intent intent = new Intent();
        intent.putExtra(ExerciseInfoActivity.EXERCISE_ARG, clickedItemId);
        setResult(RESULT_OK, intent);

        finish();
    }
}
