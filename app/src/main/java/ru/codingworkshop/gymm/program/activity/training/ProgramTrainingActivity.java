package ru.codingworkshop.gymm.program.activity.training;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

import ru.codingworkshop.gymm.program.ProgramAdapter;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.GymDbHelper;
import ru.codingworkshop.gymm.data.model.ProgramExercise;
import ru.codingworkshop.gymm.data.model.ProgramTraining;
import ru.codingworkshop.gymm.databinding.ActivityProgramTrainingBinding;
import ru.codingworkshop.gymm.program.activity.exercise.ProgramExerciseActivity;

public class ProgramTrainingActivity extends AppCompatActivity
        implements ActionMode.Callback,
        LoaderManager.LoaderCallbacks<Cursor>,
        ProgramAdapter.ListItemActionListener
{
    private ProgramTraining mModel;
    private ProgramAdapter<ExerciseViewHolder> mExercisesAdapter;
    private ActivityProgramTrainingBinding mBinding;

    private static final String TAG = ProgramTrainingActivity.class.getSimpleName();
    static final String TRAINING_MODEL_KEY = ProgramTraining.class.getCanonicalName();
    public static final String TRAINING_ID_KEY = ProgramTraining.class.getCanonicalName() + ".id";

    private static final int LOADER_TRAINING_LOAD = 0;
    private static final int LOADER_TRAINING_SAVE = 1;
    private static final int REQUEST_CODE_EXERCISE = 0;
    private RecyclerView mExercisesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // data binding
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_program_training);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar2));
        // setting "up" button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // populating spinner with days of week
        DateFormatSymbols dateFormatSymbols = java.text.DateFormatSymbols.getInstance(Locale.getDefault());
        String[] dayOfWeekNamesWrongOrder = dateFormatSymbols.getWeekdays();
        String[] dayOfWeekNames = new String[dayOfWeekNamesWrongOrder.length];
        int firstDayOfWeek = Calendar.getInstance(Locale.getDefault()).getFirstDayOfWeek();
        for (int i = 1; i < dayOfWeekNames.length; i++) {
            int index = (i + firstDayOfWeek - 1) % 8;
            if (index == 0)
                index++;
            dayOfWeekNames[i] = dayOfWeekNamesWrongOrder[index];
        }
        dayOfWeekNames[0] = "Не выбрано";


        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, dayOfWeekNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.program_training_weekday);
        spinner.setAdapter(adapter);


        // recycler view
        mExercisesView = (RecyclerView) findViewById(R.id.program_training_exercises_list);
        mExercisesView.setLayoutManager(new LinearLayoutManager(this));
        mExercisesView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mExercisesAdapter = new ProgramAdapter<>(this, new ExerciseViewHolderFactory());
        mExercisesView.setAdapter(mExercisesAdapter);
        mBinding.setAdapter(mExercisesAdapter);
        mExercisesAdapter.setModel(mModel);

        // restore model from bundle
        if (savedInstanceState != null && savedInstanceState.containsKey(TRAINING_MODEL_KEY)) {
            mModel = savedInstanceState.getParcelable(TRAINING_MODEL_KEY);
            onModelUpdated();
        }

        Intent intent = getIntent();
        if (mModel == null) {
            if (intent != null && intent.hasExtra(TRAINING_ID_KEY)) {
                long id = intent.getLongExtra(TRAINING_ID_KEY, 0);
                Bundle bundle = new Bundle();
                bundle.putLong(TRAINING_ID_KEY, id);
                getSupportLoaderManager().initLoader(LOADER_TRAINING_LOAD, bundle, this);
            } else {
                mModel = new ProgramTraining();
                onModelUpdated();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TRAINING_MODEL_KEY, mModel);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            ProgramExercise exercise = data.getParcelableExtra(ProgramExerciseActivity.EXERCISE_MODEL_KEY);
            int order = exercise.getOrder();
            if (order == -1) {
                mModel.addChild(exercise);
                mExercisesAdapter.notifyItemInserted(mModel.childrenCount() - 1);
            } else {
                mModel.setChild(order, exercise);
                mExercisesAdapter.notifyItemChanged(order);
            }
        }
    }

    public void onAddButtonClick(View view) {
        Intent intent = new Intent(this, ProgramExerciseActivity.class);
        startActivityForResult(intent, REQUEST_CODE_EXERCISE);
    }

    // menu
    //-----------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.program_training_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                getSupportLoaderManager().initLoader(LOADER_TRAINING_SAVE, null, ProgramTrainingActivity.this);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //-----------------------------------------

    // loader
    //-----------------------------------------
    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            @Override
            protected void onStartLoading() {
                Log.d(TAG, "onStartLoading: " + mModel);
                if (id != LOADER_TRAINING_LOAD || mModel == null)
                    forceLoad();
                else
                    deliverResult(null);
            }

            @Override
            public Cursor loadInBackground() {
                GymDbHelper dbHelper = new GymDbHelper(ProgramTrainingActivity.this);
                Log.d(TAG, "loadInBackground: " + mModel);
                if (id == LOADER_TRAINING_SAVE) {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    mModel.create(db, 0);
                    mModel.update(db);
                } else if (id == LOADER_TRAINING_LOAD) {
                    mModel = ProgramTraining.load(dbHelper.getReadableDatabase(), args.getLong(TRAINING_ID_KEY));
                }
                return null;
            }
        };
    }

    private void onModelUpdated() {
        mBinding.setTraining(mModel);
        mExercisesAdapter.setModel(mModel);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_TRAINING_LOAD)
            onModelUpdated();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    @Override
    public void onListItemClick(View view) {
        // TODO иерархическое появление (Активити вылезает прямо из элемента списка)
        ProgramExercise exercise = mModel.getChild(mExercisesView.getChildAdapterPosition(view));
        Intent intent = new Intent(this, ru.codingworkshop.gymm.program.activity.exercise.ProgramExerciseActivity.class);
        intent.putExtra(ru.codingworkshop.gymm.program.activity.exercise.ProgramExerciseActivity.EXERCISE_MODEL_KEY, exercise);
        startActivityForResult(intent, REQUEST_CODE_EXERCISE);
    }

    @Override
    public boolean onListItemLongClick(View view) {
        return false;
    }

    @Override
    public boolean onMove(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {

    }
}

