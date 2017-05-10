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

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import io.requery.Persistable;
import io.requery.sql.EntityDataStore;
import ru.codingworkshop.gymm.App;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.Exercise;
import ru.codingworkshop.gymm.data.model.ProgramExercise;
import ru.codingworkshop.gymm.data.model.ProgramSet;
import ru.codingworkshop.gymm.databinding.ActivityProgramExerciseBinding;
import ru.codingworkshop.gymm.databinding.ActivityProgramExerciseListItemBinding;
import ru.codingworkshop.gymm.ui.program.ActivityProperties;
import ru.codingworkshop.gymm.ui.program.Adapter;
import ru.codingworkshop.gymm.ui.program.EditModeCallbacks;
import ru.codingworkshop.gymm.ui.program.ModelHolder;
import ru.codingworkshop.gymm.ui.program.ViewHolderFactory;
import ru.codingworkshop.gymm.ui.program.events.ClickViewEvent;
import ru.codingworkshop.gymm.ui.program.exercise.picker.MuscleGroupsActivity;

/**
 * Created by Радик on 20.04.2017.
 */

public class ProgramExerciseActivity extends AppCompatActivity implements
        SetInputFragment.SetInputDialogListener,
        ProgramExerciseActivityAlerts.Callback {

    private EntityDataStore<Persistable> data;
    private SetInputFragment setInputFragment;
    private Adapter<ActivityProgramExerciseListItemBinding, ProgramSet> adapter;
    private ProgramExerciseActivityAlerts alerts;
    private ModelHolder<ProgramExercise, ProgramSet> modelHolder;
    private EventBus eventBus;

    private static final String TAG = ProgramExerciseActivity.class.getSimpleName();

    public static final String PROGRAM_EXERCISE_ID = "programExerciseId";
    public static final String PROGRAM_EXERCISE_MODEL = "programExerciseModel";
    public static final String EXERCISE_MODEL = "exerciseModel";
    public static final int PROGRAM_EXERCISE_CREATE_REQUEST_CODE = 0;
    public static final int PROGRAM_EXERCISE_MODIFY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventBus = new EventBus();
        eventBus.register(this);

        ActivityProgramExerciseBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_program_exercise);
        binding.setProperties(new ActivityProperties(eventBus));

        alerts = new ProgramExerciseActivityAlerts(this);

        data = ((App) getApplication()).getData();

        setInputFragment = (SetInputFragment) getFragmentManager().findFragmentByTag(SetInputFragment.TAG);
        if (setInputFragment == null)
            setInputFragment = SetInputFragment.newInstance();

        setSupportActionBar((Toolbar) findViewById(R.id.program_exercise_toolbar));
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
        }


        ViewHolderFactory<ActivityProgramExerciseListItemBinding> factory = new ViewHolderFactory<>(
                eventBus,
                binding.getProperties(),
                R.layout.activity_program_exercise_list_item,
                R.id.program_exercise_list_item_reorder_action
        );
        adapter = new Adapter<>(factory, eventBus);

        modelHolder = ModelHolder.newInstance(
                data,
                new ModelHolder.ProgramExerciseAdapter(),
                savedInstanceState != null ? savedInstanceState : getIntent().getExtras(),
                PROGRAM_EXERCISE_ID
        );

        binding.setExercise(modelHolder.getModel());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.program_exercise_sets_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new EditModeCallbacks(
                eventBus,
                this,
                recyclerView,
                R.string.program_exercise_activity_set_deleted_message
        );
        adapter.setModelHolder(modelHolder);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.program_exercise_name_layout).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ProgramExerciseActivity.this.onExercisePick(v);
            }
        });

        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(PROGRAM_EXERCISE_ID, modelHolder.getModel().getId());
    }

    @Override
    public void onBackPressed() {
        if (modelHolder.isModified()) {
            alerts.showUnsavedChanges();
        } else {
            finish();
        }
    }

    public void onAddButtonClick(View view) {
        setInputFragment.create(getFragmentManager());
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
                if (modelHolder.getModel().getExercise() == null)
                    alerts.showExerciseNotSelected();
                else if (adapter.getItemCount() == 0)
                    alerts.showOnEmptyList();
                else
                    finishWithSaving();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onExercisePick(View v) {
        Intent intent = new Intent(ProgramExerciseActivity.this, MuscleGroupsActivity.class);
        startActivityForResult(intent, 0);
    }

    @Subscribe
    public void onSetClick(ClickViewEvent<ActivityProgramExerciseListItemBinding> event) {
        ProgramSet set = event.getBinding().getModel();
        setInputFragment.modify(getFragmentManager(), set);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data.hasExtra(EXERCISE_MODEL)) {
            Exercise returnedExercise = data.getParcelableExtra(EXERCISE_MODEL);
            modelHolder.getModel().setExercise(returnedExercise);
        }
    }

    @Override
    public void onSetCreated(ProgramSet model) {
        adapter.addModel(model);
    }

    @Override
    public void onSetModified(ProgramSet model) {
        adapter.replaceModel(model);
    }

    private void finishWithSaving() {
        modelHolder.saveAllChanges();

        Intent resultData = new Intent();
        resultData.putExtra(PROGRAM_EXERCISE_ID, modelHolder.getModel().getId());

        setResult(RESULT_OK, resultData);
        finish();
    }

    @Override
    public void finishWithoutSaving() {
        modelHolder.undoAllChanges();

        finish();
    }

    @Override
    public void addListItem() {
        onAddButtonClick(null);
    }

    @Override
    public void pickExercise() {
        onExercisePick(null);
    }
}
