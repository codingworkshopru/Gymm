package ru.codingworkshop.gymm.ui.program.training;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import io.requery.Persistable;
import io.requery.sql.EntityDataStore;
import ru.codingworkshop.gymm.App;
import ru.codingworkshop.gymm.MainActivity;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.ProgramExercise;
import ru.codingworkshop.gymm.data.model.ProgramTraining;
import ru.codingworkshop.gymm.data.model.ProgramTrainingEntity;
import ru.codingworkshop.gymm.databinding.ActivityProgramTrainingBinding;
import ru.codingworkshop.gymm.databinding.ActivityProgramTrainingListItemBinding;
import ru.codingworkshop.gymm.ui.program.Adapter;
import ru.codingworkshop.gymm.ui.program.exercise.ProgramExerciseActivity;

/**
 * Created by Радик on 20.04.2017.
 */

public class ProgramTrainingActivity extends AppCompatActivity {
    private ActivityProgramTrainingBinding binding;
    private EntityDataStore<Persistable> data;
    private Adapter<ActivityProgramTrainingListItemBinding, ProgramExercise> adapter;

    private static final String TAG = ProgramTrainingActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_program_training);

        setSupportActionBar((Toolbar) findViewById(R.id.program_training_toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        data = ((App) getApplication()).getData();

        ProgramTraining model;
        Intent startedIntent = getIntent();
        if (savedInstanceState != null && savedInstanceState.containsKey("model")) {
            model = savedInstanceState.getParcelable("model");
        } else if (startedIntent != null && startedIntent.hasExtra(MainActivity.PROGRAM_TRAINING_ID_KEY)) {
            long trainingId = startedIntent.getLongExtra(MainActivity.PROGRAM_TRAINING_ID_KEY, 0L);
            model = data.select(ProgramTraining.class)
                    .where(ProgramTrainingEntity.ID.eq(trainingId))
                    .get()
                    .first();
        } else {
            model = new ProgramTrainingEntity();
        }
        binding.setTraining(model);

        adapter = new Adapter<>(R.layout.activity_program_training_list_item);
        adapter.setDataList(model.getExercises());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.program_training_exercises_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("model", binding.getTraining());
    }

    public void onAddButtonClick(View view) {
        Intent intent = new Intent(this, ProgramExerciseActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            ProgramExercise returnedModel = data.getParcelableExtra(ProgramExerciseActivity.PROGRAM_EXERCISE_MODEL);
            if (returnedModel.getProgramTraining() == null) {
                adapter.addModel(returnedModel);
            } else {
                adapter.replaceModel(returnedModel);
            }
        }
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
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_done:
                data.upsert(binding.getTraining());
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // binding adapters
    //-----------------------------------------
    @BindingAdapter(value = {"app:value", "bind:valueAttrChanged"}, requireAll = false)
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

    @InverseBindingAdapter(attribute = "app:value", event = "bind:valueAttrChanged")
    public static int captureSpinnerValue(Spinner spinner) {
        return (int) spinner.getSelectedItemId();
    }
}
