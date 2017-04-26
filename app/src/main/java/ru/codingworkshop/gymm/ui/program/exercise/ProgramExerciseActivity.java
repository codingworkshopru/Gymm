package ru.codingworkshop.gymm.ui.program.exercise;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import io.requery.Persistable;
import io.requery.sql.EntityDataStore;
import ru.codingworkshop.gymm.App;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.Exercise;
import ru.codingworkshop.gymm.data.model.ProgramExercise;
import ru.codingworkshop.gymm.data.model.ProgramExerciseEntity;
import ru.codingworkshop.gymm.data.model.ProgramSet;
import ru.codingworkshop.gymm.databinding.ActivityProgramExerciseBinding;
import ru.codingworkshop.gymm.databinding.ActivityProgramExerciseListItemBinding;
import ru.codingworkshop.gymm.ui.program.Adapter;
import ru.codingworkshop.gymm.ui.program.exercise.picker.MuscleGroupsActivity;

/**
 * Created by Радик on 20.04.2017.
 */

public class ProgramExerciseActivity extends AppCompatActivity
        implements SetInputFragment.SetInputDialogListener {
    private EntityDataStore<Persistable> data;
    private ProgramExercise model;
    private SetInputFragment setInputFragment;
    private Adapter<ActivityProgramExerciseListItemBinding, ProgramSet> adapter;
    ActivityProgramExerciseBinding binding;

    private static final String TAG = ProgramExerciseActivity.class.getSimpleName();

    public static final String PROGRAM_EXERCISE_ID = "programExerciseId";
    public static final String PROGRAM_EXERCISE_MODEL = "programExerciseModel";
    public static final String EXERCISE_MODEL = "exerciseModel";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_program_exercise);

        data = ((App) getApplication()).getData();

        setInputFragment = SetInputFragment.newInstance();

        setSupportActionBar((Toolbar) findViewById(R.id.program_exercise_toolbar));
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(PROGRAM_EXERCISE_MODEL)) {
            model = savedInstanceState.getParcelable(PROGRAM_EXERCISE_MODEL);
        }

        if (model == null) {
            long exerciseId = getIntent().getLongExtra(PROGRAM_EXERCISE_ID, 0L);
            if (exerciseId != 0L) {
                model = data.select(ProgramExercise.class)
                        .where(ProgramExerciseEntity.ID.eq(exerciseId))
                        .get()
                        .first();
            } else {
                model = new ProgramExerciseEntity();
            }
        }

        adapter = new Adapter<>(R.layout.activity_program_exercise_list_item);
        adapter.setDataList(model.getSets());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.program_exercise_sets_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PROGRAM_EXERCISE_MODEL, model);
    }

    public void onAddButtonClick(View view) {
        getFragmentManager()
                .beginTransaction()
                .add(0, setInputFragment, SetInputFragment.class.getSimpleName())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.program_training_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_done:
                if (model.getId() != 0L)
                    data.update(model);

                Intent resultData = new Intent();
                resultData.putExtra(PROGRAM_EXERCISE_MODEL, model);
                setResult(RESULT_OK, resultData);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSetInputDialogFinished(ProgramSet model) {
        if (model.getExercise() == null)
            adapter.addModel(model);
    }

    public void onExercisePick(View view) {
        Intent intent = new Intent(ProgramExerciseActivity.this, MuscleGroupsActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data.hasExtra(EXERCISE_MODEL)) {
            Exercise returnedExercise = data.getParcelableExtra(EXERCISE_MODEL);
            model.setExercise(returnedExercise);
            binding.setExercise(returnedExercise);
        }
    }
}
