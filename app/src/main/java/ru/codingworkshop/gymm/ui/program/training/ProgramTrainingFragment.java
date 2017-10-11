package ru.codingworkshop.gymm.ui.program.training;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.databinding.FragmentProgramTrainingBinding;
import ru.codingworkshop.gymm.ui.TwoButtonAlert;
import ru.codingworkshop.gymm.ui.common.EditTextValidator;
import ru.codingworkshop.gymm.ui.common.ListItemListeners;
import ru.codingworkshop.gymm.ui.program.common.ActionModeCallback;
import ru.codingworkshop.gymm.ui.program.common.MyAdapterDataObserver;
import ru.codingworkshop.gymm.ui.program.common.MySimpleCallback;
import ru.codingworkshop.gymm.ui.program.common.ProgramRecyclerView;

public class ProgramTrainingFragment extends Fragment {
    private static final String PROGRAM_TRAINING_ID_KEY = "programTrainingId";

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ProgramTrainingViewModel viewModel;
    private ProgramTrainingTree tree;
    private FragmentProgramTrainingBinding binding;

    private TwoButtonAlert alert;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = viewModelFactory.create(ProgramTrainingViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Bundle arguments = getArguments();
        LiveData<Boolean> liveLoaded;
        if (arguments != null) {
            liveLoaded = viewModel.load(arguments.getLong(PROGRAM_TRAINING_ID_KEY));
        } else {
            liveLoaded = viewModel.create();
        }
        liveLoaded.observe(this, this::init);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_program_training, container, false);
        binding.setInActionMode(new ObservableBoolean());

        alert = new TwoButtonAlert(getChildFragmentManager(), (dialogId, positive) -> {
            if (positive) { cleanupAndClose(); }
        });

        Toolbar toolbar = binding.programTrainingToolbar;
        toolbar.inflateMenu(R.menu.program_training_menu);
        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);

        return binding.getRoot();
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
        rv.setItemTouchHelperCallback(new MySimpleCallback(tree));
    }

    private void onExerciseLongClick(View v) {
        final ActionModeCallback actionModeCallback = new ActionModeCallback(binding.programExerciseList, binding.getInActionMode());
        getActivity().startActionMode(actionModeCallback);
        viewModel.setChildrenChanged(true);
    }

    private void onExerciseClick(View v) {

    }

    @VisibleForTesting
    void onFragmentClose() {
        if (viewModel.isChanged()) {
            alert.showAlertWithDefaultButtons(0, R.string.cancel_changes_question);
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
            case R.id.action_done:
                final EditTextValidator editTextValidator = new EditTextValidator(binding.programTrainingNameLayout, R.string.program_training_activity_name_empty_error);
                if (editTextValidator.validate()) {
                    viewModel.save().observe(this, saved -> {
                        if (saved == null) return;

                        editTextValidator.addValidation(s -> saved, R.string.program_training_activity_name_duplicate_error);
                        if (editTextValidator.validate()) {
                            close();
                        }
                    });
                }
                return true;
        }

        return false;
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
