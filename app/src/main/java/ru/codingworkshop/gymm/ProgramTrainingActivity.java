package ru.codingworkshop.gymm;

import java.text.DateFormatSymbols;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;

import ru.codingworkshop.gymm.data.GymDbHelper;
import ru.codingworkshop.gymm.data.model.ProgramExercise;
import ru.codingworkshop.gymm.data.model.ProgramTraining;
import ru.codingworkshop.gymm.databinding.ActivityProgramTrainingBinding;

public class ProgramTrainingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private ProgramTraining mModel;
    private ProgramAdapter<ExerciseViewHolder> mExercisesAdapter;
    private ActivityProgramTrainingBinding mBinding;

    private static final String TAG = ProgramTrainingActivity.class.getSimpleName();
    static final String TRAINING_MODEL_KEY = "ru.codingworkshop.TRAINING_NEW_MODEL";

    private static final int LOADER_TRAINING_LOAD = 0;
    private static final int LOADER_TRAINING_SAVE = 1;
    private static final int REQUEST_CODE_EXERCISE = 0;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // data binding
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_program_training);

        // restore model from bundle
        if (savedInstanceState != null && savedInstanceState.containsKey(TRAINING_MODEL_KEY))
            mModel = savedInstanceState.getParcelable(TRAINING_MODEL_KEY);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar2));
        // setting "up" button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // populating spinner with days of week
        DateFormatSymbols dateFormatSymbols = java.text.DateFormatSymbols.getInstance(Locale.getDefault());
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, dateFormatSymbols.getWeekdays());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.program_training_weekday);
        spinner.setAdapter(adapter);

        // recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.program_training_exercises_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mExercisesAdapter = new ProgramAdapter<>(mModel, ExerciseViewHolder.class, this);
        mExercisesAdapter.attachItemTouchHelperToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mExercisesAdapter);
        mRecyclerView.setHasFixedSize(true);

        Intent intent = getIntent();
        if (mModel == null) {
            if (intent != null && intent.hasExtra("shit")) {
                long id = intent.getLongExtra("shit", 0);
                Bundle bundle = new Bundle();
                bundle.putLong("shit", id);
                getSupportLoaderManager().initLoader(LOADER_TRAINING_LOAD, bundle, this);
            } else {
                mModel = new ProgramTraining();
                onModelUpdated();
            }
        }
        Log.d(TAG, "onCreate: " + mModel);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TRAINING_MODEL_KEY, mModel);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + mModel);
        if (data != null) {
            ProgramExercise exercise = data.getParcelableExtra(ru.codingworkshop.gymm.program.activity.exercise.ProgramExerciseActivity.EXERCISE_MODEL_KEY);
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
        Intent intent = new Intent(this, ru.codingworkshop.gymm.program.activity.exercise.ProgramExerciseActivity.class);
        startActivityForResult(intent, REQUEST_CODE_EXERCISE);
    }

    // on list item click
    @Override
    public void onClick(View v) {
        // TODO иерархическое появление (Активити вылезает прямо из элемента списка)
        ExerciseViewHolder vh = (ExerciseViewHolder) mRecyclerView.getChildViewHolder(v);
        Intent intent = new Intent(this, ru.codingworkshop.gymm.program.activity.exercise.ProgramExerciseActivity.class);
        intent.putExtra(ru.codingworkshop.gymm.program.activity.exercise.ProgramExerciseActivity.EXERCISE_MODEL_KEY, vh.getModel());
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
                    mModel = ProgramTraining.load(dbHelper.getReadableDatabase(), args.getLong("shit"));
                }
                return null;
            }
        };
    }

    void onModelUpdated() {
        mBinding.setProgram(mModel);
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
    //-----------------------------------------

    // recycler view adapter
    //-----------------------------------------
//    private static final class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ExerciseViewHolder> {
//        ProgramTraining programTraining;
//
//        ExercisesAdapter(ProgramTraining training) {
//            programTraining = training;
//        }
//
//        public void setModel(ProgramTraining training) {
//            programTraining = training;
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//            View view = inflater.inflate(android.R.layout.two_line_list_item, parent, false);
//
//            return new ExerciseViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(ExerciseViewHolder holder, int position) {
//            holder.setText1(programTraining.getChild(position).getExercise().getName());
//            holder.setText2(programTraining.getChild(position).childrenCount());
//        }
//
//        @Override
//        public int getItemCount() {
//            return programTraining.childrenCount();
//        }

        static final class ExerciseViewHolder extends ProgramAdapter.ProgramViewHolder<ProgramExercise> {
            private TextView text1;
            private TextView text2;

            ExerciseViewHolder(View itemView) {
                super(itemView);
                text1 = (TextView) itemView.findViewById(R.id.program_exercise_list_item_reps);
                text2 = (TextView) itemView.findViewById(R.id.program_exercise_list_item_rest_time);
            }

            @Override
            public void setModel(ProgramExercise model) {
                super.setModel(model);
                text1.setText(model.getExercise().getName());
                text2.setText(model.childrenCount() + "");
            }
        }
//    }
    //-----------------------------------------

    // binding adapters
    //-----------------------------------------
    @BindingAdapter(value = {"bind:value", "bind:valueAttrChanged"}, requireAll = false)
    public static void setSpinnerValue(Spinner spinner, int value, final InverseBindingListener bindingListener) {
        spinner.setSelection(value, false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (bindingListener != null) {
                    bindingListener.onChange();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @InverseBindingAdapter(attribute = "bind:value", event = "bind:valueAttrChanged")
    public static int captureSpinnerValue(Spinner spinner) {
        return (int) spinner.getSelectedItemId();
    }
    //-----------------------------------------
}
