package ru.codingworkshop.gymm.ui.program.training;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import io.requery.Persistable;
import io.requery.sql.EntityDataStore;
import ru.codingworkshop.gymm.App;
import ru.codingworkshop.gymm.BuildConfig;
import ru.codingworkshop.gymm.MainActivity;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.ProgramExercise;
import ru.codingworkshop.gymm.data.model.ProgramExerciseEntity;
import ru.codingworkshop.gymm.data.model.ProgramSet;
import ru.codingworkshop.gymm.data.model.ProgramSetEntity;
import ru.codingworkshop.gymm.data.model.ProgramTraining;
import ru.codingworkshop.gymm.data.model.ProgramTrainingEntity;
import ru.codingworkshop.gymm.databinding.ActivityProgramTrainingBinding;
import ru.codingworkshop.gymm.databinding.ActivityProgramTrainingListItemBinding;
import ru.codingworkshop.gymm.ui.program.ActivityProperties;
import ru.codingworkshop.gymm.ui.program.Adapter;
import ru.codingworkshop.gymm.ui.program.EditModeCallbacks;
import ru.codingworkshop.gymm.ui.program.ModelHolder;
import ru.codingworkshop.gymm.ui.program.ViewHolderFactory;
import ru.codingworkshop.gymm.ui.program.events.ClickViewEvent;
import ru.codingworkshop.gymm.ui.program.exercise.ProgramExerciseActivity;

/**
 * Created by Радик on 20.04.2017.
 */

public class ProgramTrainingActivity extends AppCompatActivity implements ProgramTrainingActivityAlerts.Callback {

    private ActivityProgramTrainingBinding binding;
    private EntityDataStore<Persistable> data;
    private Adapter<ActivityProgramTrainingListItemBinding, ProgramExercise> adapter;
    private EventBus eventBus;
    private ProgramTrainingActivityAlerts alerts;
    private TextInputLayout nameInputLayout;
    private ModelHolder<ProgramTraining, ProgramExercise> modelHolder;

    public static final String PROGRAM_TRAINING_ID = "programTrainingId";
    public static final String PROGRAM_TRAINING_MODEL = "programTrainingModel";

    private static final String TAG = ProgramTrainingActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventBus = new EventBus();
        eventBus.register(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_program_training);
        binding.setProperties(new ActivityProperties(eventBus));

        alerts = new ProgramTrainingActivityAlerts(this);
        nameInputLayout = ((TextInputLayout)findViewById(R.id.program_training_name_layout));

        ((TextInputEditText) findViewById(R.id.program_training_name)).addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameInputLayout.setError(null);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });

        setSupportActionBar((Toolbar) findViewById(R.id.program_training_toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        data = ((App) getApplication()).getData();
        modelHolder = new ModelHolder<>(data, new ModelHolder.ProgramTrainingAdapter());

        Intent startedIntent = getIntent();
        if (savedInstanceState != null && savedInstanceState.containsKey(PROGRAM_TRAINING_ID)) {
            long trainingId = savedInstanceState.getLong(PROGRAM_TRAINING_ID);
            modelHolder.selectWithDrafting(trainingId);
        } else if (startedIntent != null && startedIntent.hasExtra(MainActivity.PROGRAM_TRAINING_ID_KEY)) {
            long trainingId = startedIntent.getLongExtra(MainActivity.PROGRAM_TRAINING_ID_KEY, 0L);
            modelHolder.select(trainingId);
        } else {
            modelHolder.createNewModel();
        }

        binding.setTraining(modelHolder.getModel());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.program_training_exercises_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new EditModeCallbacks(eventBus, this, recyclerView, R.string.program_training_activity_exercise_deleted_message);

        ViewHolderFactory<ActivityProgramTrainingListItemBinding> factory = new ViewHolderFactory<>(
                eventBus,
                binding.getProperties(),
                R.layout.activity_program_training_list_item,
                R.id.program_training_list_item_reorder_action
        );
        adapter = new Adapter<>(factory, eventBus);
        adapter.setModelHolder(modelHolder);
        recyclerView.setAdapter(adapter);

        Log.d(TAG, "onCreate");

        // check for garbage
        if (BuildConfig.DEBUG) {
            int exerciseGarbage = 0;
            int setGarbage = 0;
            try {
                exerciseGarbage = data.count(ProgramExercise.class).where(ProgramExerciseEntity.PROGRAM_TRAINING.isNull()).get().call();
                setGarbage = data.count(ProgramSet.class).where(ProgramSetEntity.PROGRAM_EXERCISE.isNull()).get().call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (exerciseGarbage > 0)
                throw new IllegalStateException(exerciseGarbage + " program exercise haven't parent program training");
            if (setGarbage > 0)
                throw new IllegalStateException(setGarbage + " program set haven't parent program exercise");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(PROGRAM_TRAINING_ID, modelHolder.getModel().getId());
    }

    @Override
    public void onBackPressed() {
        if (modelHolder.isModified() || modelHolder.getModel().isDrafting()) {
            alerts.showUnsavedChanges();
        } else {
            super.onBackPressed();
        }
    }

    public void onAddButtonClick(View view) {
        Intent intent = new Intent(this, ProgramExerciseActivity.class);
        startActivityForResult(intent, ProgramExerciseActivity.PROGRAM_EXERCISE_CREATE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            ProgramExercise returnedExercise = this.data.select(ProgramExercise.class)
                    .where(ProgramExerciseEntity.ID.eq(data.getLongExtra(ProgramExerciseActivity.PROGRAM_EXERCISE_ID, 0L)))
                    .get()
                    .first();

            if (requestCode == ProgramExerciseActivity.PROGRAM_EXERCISE_CREATE_REQUEST_CODE)
                adapter.addModel(returnedExercise);
            else if (requestCode == ProgramExerciseActivity.PROGRAM_EXERCISE_MODIFY_REQUEST_CODE)
                adapter.replaceModel(returnedExercise);
        } else {
            adapter.notifyDataSetChanged();
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
        ProgramTraining model = modelHolder.getModel();

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_done:
                String trainingName = model.getName();
                int trainingsWithSameName = 0;
                try {
                    trainingsWithSameName = data.count(ProgramTraining.class)
                            .where(ProgramTrainingEntity.NAME.eq(trainingName)
                                            .and(ProgramTrainingEntity.ID.notEqual(model.getId())))
                            .get()
                            .call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (Strings.isNullOrEmpty(trainingName))
                    nameInputLayout.setError(getString(R.string.program_training_activity_name_empty_error));
                else if (trainingsWithSameName != 0)
                    nameInputLayout.setError(getString(R.string.program_training_activity_name_duplicate_error));
                else if (adapter.getItemCount() == 0)
                    alerts.showOnEmptyList();
                else
                    finishWithSaving();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Subscribe
    public void exerciseClick(ClickViewEvent<ActivityProgramTrainingListItemBinding> event) {
        ProgramExercise exercise = event.getBinding().getModel();
        Intent intent = new Intent(this, ProgramExerciseActivity.class);
        intent.putExtra(ProgramExerciseActivity.PROGRAM_EXERCISE_ID, exercise.getId());
        startActivityForResult(intent, ProgramExerciseActivity.PROGRAM_EXERCISE_MODIFY_REQUEST_CODE);
    }

    public void finishWithSaving() {
        modelHolder.saveAllChanges();
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void finishWithoutSaving() {
        modelHolder.undoAllChanges();

        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void addListItem() {
        onAddButtonClick(null);
    }

    // binding adapters
    //-----------------------------------------
    @BindingAdapter(value = {"app:value", "app:valueAttrChanged"}, requireAll = false)
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

    @InverseBindingAdapter(attribute = "app:value", event = "app:valueAttrChanged")
    public static int captureSpinnerValue(Spinner spinner) {
        return (int) spinner.getSelectedItemId();
    }
}
