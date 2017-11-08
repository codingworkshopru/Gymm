package ru.codingworkshop.gymm.ui.program.training;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.databinding.FragmentProgramTrainingBinding;
import ru.codingworkshop.gymm.ui.FragmentAlert;
import ru.codingworkshop.gymm.ui.common.EditTextValidator;
import ru.codingworkshop.gymm.ui.common.ListItemListeners;
import ru.codingworkshop.gymm.ui.program.common.ActionModeCallback;
import ru.codingworkshop.gymm.ui.program.common.BaseFragment;
import ru.codingworkshop.gymm.ui.program.common.MyAdapterDataObserver;
import ru.codingworkshop.gymm.ui.program.common.ItemTouchHelperCallback;
import ru.codingworkshop.gymm.ui.program.common.ProgramRecyclerView;

public class ProgramTrainingFragment extends BaseFragment {
    private static final String PROGRAM_TRAINING_ID_KEY = "programTrainingId";
    private static final int CANCEL_ALERT_ID = 0;
    private static final int EMPTY_EXERCISE_LIST_ALERT_ID = 1;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ProgramTrainingViewModel viewModel;
    private ProgramTrainingTree tree;
    private FragmentProgramTrainingBinding binding;

    private FragmentAlert alert;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = viewModelFactory.create(ProgramTrainingViewModel.class);
    }

    @Override
    protected Toolbar getToolbar() {
        Toolbar toolbar = binding.programTrainingToolbar;
        toolbar.inflateMenu(R.menu.program_training_menu);
        return toolbar;
    }

    @Override
    protected ViewDataBinding createBinding(LayoutInflater inflater, ViewGroup parent) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_program_training, parent, false);
        binding.setInActionMode(new ObservableBoolean());

        alert = new FragmentAlert(getChildFragmentManager(), (dialogId, positive) -> {
            if (!positive) return;

            switch (dialogId) {
                case CANCEL_ALERT_ID:
                    cleanupAndClose();
                    break;
            }
        });

        final Bundle arguments = getArguments();
        LiveData<Boolean> liveLoaded;
        if (arguments != null) {
            liveLoaded = viewModel.load(arguments.getLong(PROGRAM_TRAINING_ID_KEY));
        } else {
            liveLoaded = viewModel.create();
        }
        liveLoaded.observe(this, this::init);

        return binding;
    }

    private void init(boolean loaded) {
        if (!loaded) return;

        tree = viewModel.getProgramTrainingTree();

        binding.setProgramTraining(tree.getParent());

        initExerciseList();
    }

    private void initExerciseList() {
        ProgramRecyclerView rv = binding.programExerciseList;
        final ObservableBoolean inActionMode = binding.getInActionMode();
        ListItemListeners listeners = new ListItemListeners(R.layout.fragment_program_training_list_item)
                .setOnClickListener(this::onExerciseClick)
                .setOnLongClickListener(this::onExerciseLongClick)
                .setOnReorderButtonDownListener(rv::startDragFromChildView, R.id.programExerciseReorderImage);
        ProgramExercisesAdapter adapter = new ProgramExercisesAdapter(listeners, tree.getChildren(), inActionMode);
        MyAdapterDataObserver observer = new MyAdapterDataObserver(
                rv, R.id.programTrainingBackground,
                R.string.program_training_activity_exercise_deleted_message, tree);
        adapter.registerAdapterDataObserver(observer);
        rv.setAdapter(adapter);
        rv.setItemTouchHelperCallback(new ItemTouchHelperCallback(tree));
    }

    private void onExerciseLongClick(View v) {
        final ActionModeCallback actionModeCallback = new ActionModeCallback(binding.programExerciseList, binding.getInActionMode());
        getActivity().startActionMode(actionModeCallback);
        viewModel.setChildrenChanged();
    }

    private void onExerciseClick(View v) {
        // TODO: 18.10.2017 open exercise editor
    }

    @VisibleForTesting
    void onFragmentClose() {
        if (viewModel.isChanged()) {
            alert.showTwoButtonsAlert(CANCEL_ALERT_ID, R.string.cancel_changes_question);
        } else {
            cleanupAndClose();
        }
    }

    private void cleanupAndClose() {
        viewModel.deleteIfDrafting();
        close();
    }

    private void close() {
        getActivity().finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSaveTraining:
                validate().observe(this, valid -> {
                    if (valid != null && valid) {
                        close();
                    }
                });
                return true;
        }

        return false;
    }

    private LiveData<Boolean> validate() {
        if (!tree.hasChildren()) {
            alert.showOneButtonAlert(EMPTY_EXERCISE_LIST_ALERT_ID, R.string.program_training_activity_empty_list_dialog_message);
            return LiveDataUtil.getLive(false);
        }

        final EditTextValidator editTextValidator = new EditTextValidator(binding.programTrainingNameLayout, R.string.program_training_activity_name_empty_error);

        if (!editTextValidator.validate()) {
            return LiveDataUtil.getLive(false);
        }

        return Transformations.map(viewModel.save(), saved -> {
            editTextValidator.addValidation(s -> saved, R.string.program_training_activity_name_duplicate_error);
            return editTextValidator.validate();
        });
    }

    public static ProgramTrainingFragment newInstance() {
        return new ProgramTrainingFragment();
    }

    public static ProgramTrainingFragment newInstance(long programTrainingId) {
        Bundle args = new Bundle();
        args.putLong(PROGRAM_TRAINING_ID_KEY, programTrainingId);

        ProgramTrainingFragment fragment = newInstance();
        fragment.setArguments(args);
        return fragment;
    }
}
