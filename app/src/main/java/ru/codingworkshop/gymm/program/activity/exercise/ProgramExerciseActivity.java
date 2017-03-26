package ru.codingworkshop.gymm.program.activity.exercise;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.GymContract;
import ru.codingworkshop.gymm.data.model.Exercise;
import ru.codingworkshop.gymm.data.model.ProgramExercise;
import ru.codingworkshop.gymm.data.model.ProgramSet;
import ru.codingworkshop.gymm.databinding.ActivityProgramExerciseBinding;
import ru.codingworkshop.gymm.program.ProgramAdapter;
import ru.codingworkshop.gymm.program.ProgramUtils;
import ru.codingworkshop.gymm.program.activity.exercise.picker.MusclesActivity;
import ru.codingworkshop.gymm.program.activity.training.ProgramTrainingActivity;

public class ProgramExerciseActivity extends AppCompatActivity
        implements ActionMode.Callback,
        LoaderManager.LoaderCallbacks<Cursor>,
        SetInputDialog.SetInputDialogListener,
        ProgramAdapter.ListItemActionListener
{
    private static final String TAG = ProgramExerciseActivity.class.getSimpleName();

    private ProgramExercise mModel;
    private SimpleCursorAdapter mExercisesAdapter;
    private ProgramAdapter<SetViewHolder> mSetsAdapter;
    private RecyclerView mSetsView;
    private ActivityProgramExerciseBinding mBinding;

    public static final String EXERCISE_MODEL_KEY = ProgramExercise.class.getCanonicalName();
    private static final int EXERCISES_LOADER_ID = 0;
    static final String[] EXERCISES_PROJECTION = {
            GymContract.ExerciseEntry._ID,
            GymContract.ExerciseEntry.COLUMN_NAME
    };
    static final int EXERCISES_INDEX_ID = 0;
    static final int EXERCISES_INDEX_NAME = 1;

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

        setSupportActionBar((Toolbar) findViewById(R.id.program_exercise_toolbar));
        // setting "up" button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
        }

        // initializing adapter for spinner with exercises
        mExercisesAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_spinner_item,
                null,
                new String[] {GymContract.ExerciseEntry.COLUMN_NAME},
                new int[] {android.R.id.text1},
                0
        );
        mExercisesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        Spinner spinner = (Spinner) findViewById(R.id.program_exercise_exercises_dropdown);
//        spinner.setAdapter(mExercisesAdapter);

        // recycler view
        mSetsView = (RecyclerView) findViewById(R.id.program_exercise_sets_list);
        mSetsView.setLayoutManager(new LinearLayoutManager(this));
        mSetsView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mSetsAdapter = new ProgramAdapter<>(this, new SetViewHolderFactory());
        mSetsView.setAdapter(mSetsAdapter);
        mBinding.setAdapter(mSetsAdapter);
        mSetsAdapter.setModel(mModel);

        LoaderManager loaderManager = getSupportLoaderManager();
        if (loaderManager.getLoader(EXERCISES_LOADER_ID) == null)
            loaderManager.initLoader(EXERCISES_LOADER_ID, null, this);
        else
            loaderManager.restartLoader(EXERCISES_LOADER_ID, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXERCISE_MODEL_KEY, mModel);
    }

    public void onAddButtonClick(View view) {
        // TODO анимация FAB -> Dialog
        DialogFragment setInputDialog = new SetInputDialog();
        setInputDialog.show(getFragmentManager(), SetInputDialog.class.getSimpleName());
    }

    @Override
    public void onProgramSetReturn(ProgramSet model) {
        int modelPosition = model.getOrder();
        if (modelPosition == -1) {
            mModel.addChild(model);
            mSetsAdapter.notifyItemInserted(mSetsAdapter.getItemCount() - 1);
        } else {
            mModel.setChild(modelPosition, model);
            mSetsAdapter.notifyItemChanged(modelPosition);
        }
    }

    @Override
    public void onBackPressed() {
        if (mModel.isChanged()) {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishActivity(which == DialogInterface.BUTTON_POSITIVE);
                }
            };
            ProgramUtils.showAlert(this, listener, listener);
        } else {
            super.onBackPressed();
        }
    }

    public void onExercisePick(View v) {
        Intent intent = new Intent(this, MusclesActivity.class);
        startActivity(intent);
    }

    private void finishActivity(boolean save) {
        if (save) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXERCISE_MODEL_KEY, mModel);
            setResult(RESULT_OK, resultIntent);
            finishActivity(ProgramTrainingActivity.REQUEST_CODE_EXERCISE);
        }
        finish();
    }

    private void doActionModeChangeAnimation(final boolean actionModeOn) {
        int animationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.program_exercise_add_set);
        Animation iconsAnimation;
        if (actionModeOn) {
            fab.hide();
            iconsAnimation = AnimationUtils.makeInAnimation(this, false);
        } else {
            fab.show();
            iconsAnimation = AnimationUtils.makeOutAnimation(this, true);
        }

        iconsAnimation.setDuration(animationDuration);

        for (int i = 0; i < mSetsAdapter.getItemCount(); i++) {
            View view = mSetsView.getChildAt(i);
            if (view != null)
                view.findViewById(R.id.program_exercise_list_item_icons).startAnimation(iconsAnimation);
        }
    }

    // action mode (in fact edit mode)
    //-----------------------------------------
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//        findViewById(R.id.program_exercise_exercises_dropdown).setVisibility(View.GONE);
        doActionModeChangeAnimation(true);
        mSetsAdapter.attachItemTouchHelper(mSetsView);
        mSetsAdapter.setEditMode(true);
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
        mSetsAdapter.attachItemTouchHelper(null);
        doActionModeChangeAnimation(false);
        mSetsAdapter.setEditMode(false);
//        findViewById(R.id.program_exercise_exercises_dropdown).setVisibility(View.VISIBLE);
    }
    //-----------------------------------------

    // recycler view's items actions
    //-----------------------------------------
    @Override
    public void onListItemClick(View view) {
        Bundle arguments = new Bundle(1);
        ProgramSet set = mModel.getChild(mSetsView.getChildAdapterPosition(view));
        arguments.putParcelable(SetInputDialog.DIALOG_MODEL_KEY, set);

        DialogFragment setInputDialog = new SetInputDialog();
        setInputDialog.setArguments(arguments);
        setInputDialog.show(getFragmentManager(), SetInputDialog.class.getSimpleName());
    }

    @Override
    public boolean onListItemLongClick(View view) {
        startSupportActionMode(this);
        return true;
    }

    @Override
    public void onMove(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        ProgramUtils.showSnackbar(
                mSetsView,
                R.string.program_exercise_activity_set_deleted_message,
                R.string.cancel_button_text,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSetsAdapter.notifyItemInserted(mModel.restoreLastRemoved());
                    }
                });
    }
    //-----------------------------------------

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
                finishActivity(true);
                return true;

            case android.R.id.home:
                onBackPressed();
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
        return new ExercisesAsyncLoader(this, mExercisesAdapter.getCursor());
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
}
