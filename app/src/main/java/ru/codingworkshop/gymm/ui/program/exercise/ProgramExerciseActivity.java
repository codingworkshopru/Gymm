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
import io.requery.proxy.PropertyState;
import io.requery.sql.EntityDataStore;
import ru.codingworkshop.gymm.App;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.ModelUtil;
import ru.codingworkshop.gymm.data.model.Exercise;
import ru.codingworkshop.gymm.data.model.ProgramExercise;
import ru.codingworkshop.gymm.data.model.ProgramExerciseEntity;
import ru.codingworkshop.gymm.data.model.ProgramSet;
import ru.codingworkshop.gymm.data.model.ProgramSetEntity;
import ru.codingworkshop.gymm.databinding.ActivityProgramExerciseBinding;
import ru.codingworkshop.gymm.databinding.ActivityProgramExerciseListItemBinding;
import ru.codingworkshop.gymm.ui.program.ActivityProperties;
import ru.codingworkshop.gymm.ui.program.Adapter;
import ru.codingworkshop.gymm.ui.program.EditModeCallbacks;
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
    private ActivityProgramExerciseBinding binding;
    private ProgramExerciseActivityAlerts alerts;
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

        binding = DataBindingUtil.setContentView(this, R.layout.activity_program_exercise);
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

        ProgramExercise model;
        if (savedInstanceState != null && savedInstanceState.containsKey(PROGRAM_EXERCISE_MODEL)) {
            model = savedInstanceState.getParcelable(PROGRAM_EXERCISE_MODEL);
            model = data.select(ProgramExercise.class)
                    .where(ProgramExerciseEntity.ID.eq(model.getId()))
                    .get()
                    .first();
        } else if (getIntent().hasExtra(PROGRAM_EXERCISE_ID)) {
            long exerciseId = getIntent().getLongExtra(PROGRAM_EXERCISE_ID, 0L);
            model = data.select(ProgramExercise.class)
                    .where(ProgramExerciseEntity.ID.eq(exerciseId))
                    .get()
                    .first();
        } else {
            model = new ProgramExerciseEntity();
            model.setDrafting(true);

            data.insert(model);
        }
        binding.setExercise(model);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.program_exercise_sets_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new EditModeCallbacks(
                eventBus,
                this,
                recyclerView,
                R.string.program_exercise_activity_set_deleted_message
        );

        ViewHolderFactory<ActivityProgramExerciseListItemBinding> factory = new ViewHolderFactory<>(
                eventBus,
                binding.getProperties(),
                R.layout.activity_program_exercise_list_item,
                R.id.program_exercise_list_item_reorder_action
        );
        adapter = new Adapter<>(factory, eventBus);
        adapter.setDataList(model.getSets());
        recyclerView.setAdapter(adapter);

        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PROGRAM_EXERCISE_MODEL, binding.getExercise());
    }

    @Override
    public void onBackPressed() {
        if (ModelUtil.isEntityModified(binding.getExercise())) {
            alerts.showUnsavedChanges();
        } else {
            finishWithoutSaving();
        }
    }

    private void finishActivity() {
        if (!getIntent().hasExtra(PROGRAM_EXERCISE_ID))
            data.delete(binding.getExercise()); // TODO удалять в onDestroy, если пользователь просто закрыл приложение
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
                if (binding.getExercise().getExercise() == null)
                    alerts.showExerciseNotSelected();
                else if (adapter.getDataList().isEmpty())
                    alerts.showOnEmptyList();
                else
                    finishWithSaving();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onExercisePick(View view) {
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
            binding.getExercise().setExercise(returnedExercise);
        }
    }

    @Override
    public void onSetCreated(ProgramSet model) {
        model.setDrafting(true);
        model.setProgramExercise(binding.getExercise());
        data.insert(model);
        adapter.addModel(model);
    }

    @Override
    public void onSetModified(ProgramSet model) {
        boolean modified = ModelUtil.isAttributeModified((ProgramExerciseEntity) binding.getExercise(), ProgramExerciseEntity.SETS);
        adapter.replaceModel(model);
        data.update(model);
        if (!modified)
            ProgramExerciseEntity.$TYPE.getProxyProvider().apply((ProgramExerciseEntity)binding.getExercise()).setState(ProgramExerciseEntity.SETS, PropertyState.LOADED);
    }

    private void finishWithSaving() {
        ProgramExercise model = binding.getExercise();
        if (model.isDrafting())
            model.setDrafting(false);
        data.update(model);
        try {
            data.delete(ProgramSet.class)
                    .where(ProgramSetEntity.PROGRAM_EXERCISE.isNull())
                    .get()
                    .call();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent resultData = new Intent();
        resultData.putExtra(PROGRAM_EXERCISE_ID, model.getId());

        setResult(RESULT_OK, resultData);
        finish();
    }

    @Override
    public void finishWithoutSaving() {
        ProgramExercise model = binding.getExercise();

        if (model.isDrafting()) {
            data.delete(model);
        } else if (ModelUtil.areAssociationsModified(model)) {
            ModelUtil.refreshAll(model, data);
//            data.refreshAll(model);
        }

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
