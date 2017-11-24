package ru.codingworkshop.gymm.ui.program.training;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ViewDataBinding;
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

import dagger.android.support.AndroidSupportInjection;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.databinding.FragmentProgramTrainingBinding;
import ru.codingworkshop.gymm.databinding.FragmentProgramTrainingListItemBinding;
import ru.codingworkshop.gymm.ui.program.common.FragmentAlert;
import ru.codingworkshop.gymm.ui.common.EditTextValidator;
import ru.codingworkshop.gymm.ui.common.ListItemListeners;
import ru.codingworkshop.gymm.ui.program.common.ActionModeCallback;
import ru.codingworkshop.gymm.ui.program.common.BaseFragment;
import ru.codingworkshop.gymm.ui.program.common.MyAdapterDataObserver;
import ru.codingworkshop.gymm.ui.program.common.ItemTouchHelperCallback;
import ru.codingworkshop.gymm.ui.program.common.ProgramRecyclerView;
import ru.codingworkshop.gymm.ui.program.exercise.ProgramExerciseFragment;
import timber.log.Timber;

public class ProgramTrainingFragment extends BaseFragment {
    public static final String PROGRAM_TRAINING_ID_KEY = "programTrainingId";
    public static final String TAG = "programTrainingFragmentTag";

    private static final int CANCEL_ALERT_ID = 0;
    private static final int EMPTY_EXERCISE_LIST_ALERT_ID = 1;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ProgramTrainingViewModel viewModel;
    private ProgramTrainingTree tree;
    private FragmentProgramTrainingBinding binding;

    private FragmentAlert alert;
    private ProgramRecyclerView programExerciseList;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        Timber.d("onAttach");
        viewModel = viewModelFactory.create(ProgramTrainingViewModel.class);

        final Bundle arguments = getArguments();
        LiveData<ProgramTrainingTree> liveLoaded;
        if (arguments != null) {
            liveLoaded = viewModel.load(arguments.getLong(PROGRAM_TRAINING_ID_KEY));
        } else {
            liveLoaded = viewModel.create();
        }
        liveLoaded.observe(this, this::initData);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Timber.d("onDetach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.d("onDestroyView");
    }

    @Override
    protected Toolbar getToolbar() {
        Toolbar toolbar = binding.programTrainingToolbar;
        toolbar.inflateMenu(R.menu.program_training_menu);
        return toolbar;
    }

    @Override
    protected ViewDataBinding createBinding(LayoutInflater inflater, ViewGroup parent) {
        Timber.d("createBinding");
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_program_training, parent, false);
        binding.setInActionMode(new ObservableBoolean());
        binding.programTrainingAddExerciseButton.setOnClickListener(this::onAddExerciseButtonClick);

        alert = new FragmentAlert(getChildFragmentManager(), (dialogId, positive) -> {
            if (!positive) return;

            switch (dialogId) {
                case CANCEL_ALERT_ID:
                    cleanupAndClose();
                    break;
            }
        });

        return binding;
    }

    private void initData(ProgramTrainingTree loadedTree) {
        if (loadedTree == null) return;
        Timber.d("initData");
        tree = loadedTree;

        binding.setProgramTraining(tree.getParent());

        initExerciseList();
    }

    private void initExerciseList() {
        programExerciseList = binding.programExerciseList;
        final ObservableBoolean inActionMode = binding.getInActionMode();
        ListItemListeners listeners = new ListItemListeners(R.layout.fragment_program_training_list_item)
                .setOnClickListener(this::onExerciseClick)
                .setOnLongClickListener(this::onExerciseLongClick)
                .setOnReorderButtonDownListener(programExerciseList::startDragFromChildView, R.id.programExerciseReorderImage);
        ProgramExercisesAdapter adapter = new ProgramExercisesAdapter(listeners, tree.getChildren(), inActionMode);
        MyAdapterDataObserver observer = new MyAdapterDataObserver(
                programExerciseList, R.id.programTrainingBackground,
                R.string.program_training_activity_exercise_deleted_message, tree);
        adapter.registerAdapterDataObserver(observer);
        programExerciseList.setAdapter(adapter);
        programExerciseList.setItemTouchHelperCallback(new ItemTouchHelperCallback(tree));
    }

    private void onExerciseLongClick(View v) {
        final ActionModeCallback actionModeCallback = new ActionModeCallback(binding.programExerciseList, binding.getInActionMode());
        getActivity().startActionMode(actionModeCallback);
        viewModel.setChildrenChanged();
    }

    private void onAddExerciseButtonClick(View v) {
        openExerciseEditor(ProgramExerciseFragment.newInstanceForNew(tree.getParent().getId()));
    }

    private void onExerciseClick(View v) {
        ProgramExerciseNode n = tree.getChildren().get(programExerciseList.getChildAdapterPosition(v));
        openExerciseEditor(ProgramExerciseFragment.newInstanceForExistent(n.getId()));
    }

    private void openExerciseEditor(Fragment exerciseEditor) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.programTrainingFragmentContainer, exerciseEditor)
                .addToBackStack(null)
                .commit();
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
                final LiveData<Boolean> liveValid = validate();
                LiveDataUtil.getOnce(liveValid, valid -> {
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
