package ru.codingworkshop.gymm;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.NumberPicker;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import ru.codingworkshop.gymm.data.GymContract;
import ru.codingworkshop.gymm.data.GymDbHelper;
import ru.codingworkshop.gymm.data.model.Exercise;
import ru.codingworkshop.gymm.data.model.ProgramExercise;
import ru.codingworkshop.gymm.data.model.ProgramSet;
import ru.codingworkshop.gymm.databinding.ActivityProgramExerciseBinding;

public class ProgramExerciseActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnLongClickListener {

    private static final String TAG = ProgramExerciseActivity.class.getSimpleName();

    // models
    private ProgramExercise mModel;
    private ProgramSet mCurrentSet;

    // adapters
    private SimpleCursorAdapter mExercisesAdapter;
    private ProgramAdapter<SetViewHolder> mSetsAdapter;

    // bindings
    private ActivityProgramExerciseBinding mBinding;

    // callbacks
    private ActionMode.Callback mActionModeCallback;

    // input dialog views
    private AlertDialog mInputDialog;
    private NumberPicker mRepsPicker;
    private NumberPicker mMinutesPicker;
    private NumberPicker mSecondsPicker;

    static final String EXERCISE_MODEL_KEY = "ru.condingworkshop.EXERCISE_MODEL";
    private static final String EXERCISE_SET_KEY = EXERCISE_MODEL_KEY + "_SET";
    private static final String EXERCISE_IS_DIALOG_SHOWING = "ru.codingworkshop.IS_DIALOG_SHOWING";
    private static final int EXERCISES_LOADER_ID = 0;
    private static final String[] EXERCISES_PROJECTION = {
            GymContract.ExerciseEntry._ID,
            GymContract.ExerciseEntry.COLUMN_NAME
    };
    private static final int EXERCISES_INDEX_ID = 0;
    private static final int EXERCISES_INDEX_NAME = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // data binding
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_program_exercise);

        // restore model from intent or bundle
        Intent intent = getIntent();
        if (savedInstanceState != null && savedInstanceState.containsKey(EXERCISE_MODEL_KEY))
            mModel = savedInstanceState.getParcelable(EXERCISE_MODEL_KEY);
        else if (intent != null && intent.hasExtra(EXERCISE_MODEL_KEY))
            mModel = intent.getParcelableExtra(EXERCISE_MODEL_KEY);
        else
            mModel = new ProgramExercise();

        mCurrentSet = new ProgramSet();
        mCurrentSet.setReps(1);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        // setting "up" button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // setting adapter to spinner
        mExercisesAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_spinner_item,
                null,
                new String[] {GymContract.ExerciseEntry.COLUMN_NAME},
                new int[] {android.R.id.text1},
                0
        );
        mExercisesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.program_exercises_dropdown);
        spinner.setAdapter(mExercisesAdapter);

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(EXERCISES_LOADER_ID, null, this);


        // recycler view
        final RecyclerView rv = (RecyclerView) findViewById(R.id.program_exercise_sets_list);
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rv.setLayoutManager(new LinearLayoutManager(this));
        mSetsAdapter = new ProgramAdapter<>(mModel, SetViewHolder.class, null, this);
        rv.setAdapter(mSetsAdapter);

        // alert for data input
        View view = getLayoutInflater().inflate(R.layout.activity_program_exercise_dialog, null);
        mInputDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.program_exercise_activity_dialog_title))
                .setCancelable(true)
                .setView(view)
                .setNegativeButton(getString(R.string.program_exercise_activity_dialog_negative_button_text), null)
                .setPositiveButton(
                        getString(R.string.program_exercise_activity_dialog_positive_button_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCurrentSet = new ProgramSet();
                                saveDataInputToModel();
                                mSetsAdapter.notifyItemInserted(mSetsAdapter.getItemCount());
                                mModel.addChild(mCurrentSet);
                            }
                }).create();

        mRepsPicker = (NumberPicker) view.findViewById(R.id.program_exercise_reps_picker);
        mRepsPicker.setMinValue(1);
        mRepsPicker.setMaxValue(100);

        mMinutesPicker = (NumberPicker) view.findViewById(R.id.program_exercise_rest_minutes_picker);
        mMinutesPicker.setMinValue(0);
        mMinutesPicker.setMaxValue(60);

        mSecondsPicker = (NumberPicker) view.findViewById(R.id.program_exercise_rest_seconds_picker);
        mSecondsPicker.setMinValue(0);
        mSecondsPicker.setMaxValue(59);

        mActionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.setTitle("Режим редактирования");
                mSetsAdapter.setEditMode(true);

                for (int i = 0; i < mSetsAdapter.getItemCount(); i++) {
                    View view = rv.getChildAt(i);
                    if (view != null) {
                        View view1 = view.findViewById(R.id.program_exercise_list_item_reorder_action);
                        View view2 = view.findViewById(R.id.program_exercise_list_item_delete_sweep);
                        view1.setVisibility(View.VISIBLE);
                        view2.setVisibility(View.VISIBLE);
                        Animation animation = AnimationUtils.makeInAnimation(ProgramExerciseActivity.this, false);
                        animation.setDuration(500);
                        animation.setFillAfter(true);
                        view1.startAnimation(animation);
                        view2.startAnimation(animation);
                    }
                }
                mSetsAdapter.attachItemTouchHelperToRecyclerView(rv);
                return true;
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
                mSetsAdapter.attachItemTouchHelperToRecyclerView(null);
                mSetsAdapter.setEditMode(false);
                for (int i = 0; i < mSetsAdapter.getItemCount(); i++) {
                    View view = rv.getChildAt(i);
                    if (view != null) {
                        View view1 = view.findViewById(R.id.program_exercise_list_item_reorder_action);
                        View view2 = view.findViewById(R.id.program_exercise_list_item_delete_sweep);
                        Animation animation = AnimationUtils.makeOutAnimation(ProgramExerciseActivity.this, true);
                        animation.setDuration(500);
                        animation.setFillAfter(true);
                        view1.startAnimation(animation);
                        view2.startAnimation(animation);
                        view1.setVisibility(View.GONE);
                        view2.setVisibility(View.GONE);
                    }
                }
            }
        };
    }

    private void saveDataInputToModel() {
        mCurrentSet.setReps(mRepsPicker.getValue());
        mCurrentSet.setTimeForRest(mMinutesPicker.getValue(), mSecondsPicker.getValue());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // restore current data from bundle
        if (savedInstanceState == null)
            return;

        mCurrentSet = savedInstanceState.getParcelable(EXERCISE_SET_KEY);

        if (savedInstanceState.getBoolean(EXERCISE_IS_DIALOG_SHOWING))
            onAddButtonClick(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        outState.putBoolean(EXERCISE_IS_DIALOG_SHOWING, mInputDialog.isShowing());
        outState.putParcelable(EXERCISE_MODEL_KEY, mModel);

        saveDataInputToModel();
        outState.putParcelable(EXERCISE_SET_KEY, mCurrentSet);
    }

    @Override
    protected void onDestroy() {
        if (mInputDialog.isShowing())
            mInputDialog.dismiss();

        super.onDestroy();
    }

    public void onAddButtonClick(View view) {
        mRepsPicker.setValue(mCurrentSet.getReps());
        mMinutesPicker.setValue(mCurrentSet.getRestMinutes());
        mSecondsPicker.setValue(mCurrentSet.getRestSeconds());
        mInputDialog.show();
    }

    // on list item long click
    @Override
    public boolean onLongClick(View v) {
        startSupportActionMode(mActionModeCallback);
        Log.d(TAG, "onLongClick");
        return true;
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
                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXERCISE_MODEL_KEY, mModel);
                setResult(RESULT_OK, resultIntent);

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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public Cursor loadInBackground() {
                GymDbHelper dbHelper = new GymDbHelper(ProgramExerciseActivity.this);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                return db.query(GymContract.ExerciseEntry.TABLE_NAME, EXERCISES_PROJECTION, null, null, null, null, GymContract.ExerciseEntry.COLUMN_NAME);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mExercisesAdapter.swapCursor(data);
        mBinding.setExercise(mModel);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    //-----------------------------------------

    // RecyclerView's view holder
    //-----------------------------------------
    static final class SetViewHolder extends ProgramAdapter.ProgramViewHolder<ProgramSet> {
        private TextView mReps;
        private TextView mRestTime;

        SetViewHolder(View itemView) {
            super(itemView);
            mReps = (TextView) itemView.findViewById(R.id.program_exercise_list_item_reps);
            mRestTime = (TextView) itemView.findViewById(R.id.program_exercise_list_item_rest_time);
        }

        @Override
        public void setModel(ProgramSet model) {
            int reps = model.getReps();
            mReps.setText(itemView.getResources().getQuantityString(R.plurals.number_of_sets, reps, reps));

            int minutes = model.getRestMinutes();
            int seconds = model.getRestSeconds();

            Resources res = itemView.getResources();
            mRestTime.setText(res.getString(R.string.program_exercise_activity_dialog_rest_time_label) + " " +
                    res.getQuantityString(R.plurals.minutes, minutes, minutes) + " " +
                    res.getQuantityString(R.plurals.seconds, seconds, seconds)
            );
        }
    }
    //-----------------------------------------

    // binding adapters
    //-----------------------------------------
    @BindingAdapter(value = {"bind:value", "bind:valueAttrChanged"}, requireAll = false)
    public static void setSpinnerValue(Spinner spinner, Exercise exercise, final InverseBindingListener bindingListener) {
        Cursor cursor = ((SimpleCursorAdapter) spinner.getAdapter()).getCursor();

        if (cursor != null && exercise != null) {

            cursor.moveToFirst();
            do {
                if (cursor.getLong(EXERCISES_INDEX_ID) == exercise.getId())
                    break;
            } while (cursor.moveToNext());

            if (!cursor.isAfterLast())
                spinner.setSelection(cursor.getPosition(), false);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (bindingListener != null)
                    bindingListener.onChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @InverseBindingAdapter(attribute = "bind:value", event = "bind:valueAttrChanged")
    public static Exercise captureSpinnerValue(Spinner spinner) {
        Cursor cursor = ((SimpleCursorAdapter) spinner.getAdapter()).getCursor();

        if (cursor == null)
            return null;

        cursor.moveToPosition(spinner.getSelectedItemPosition());

        Exercise exercise = new Exercise();
        exercise.setId(cursor.getLong(EXERCISES_INDEX_ID));
        exercise.setName(cursor.getString(EXERCISES_INDEX_NAME));

        return exercise;
    }
    //-----------------------------------------
}
