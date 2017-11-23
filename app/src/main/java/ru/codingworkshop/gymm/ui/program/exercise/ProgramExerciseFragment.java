package ru.codingworkshop.gymm.ui.program.exercise;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.databinding.FragmentProgramExerciseBinding;
import ru.codingworkshop.gymm.databinding.FragmentProgramExerciseListItemBinding;
import ru.codingworkshop.gymm.ui.program.common.FragmentAlert;
import ru.codingworkshop.gymm.ui.common.ListItemListeners;
import ru.codingworkshop.gymm.ui.program.common.ActionModeCallback;
import ru.codingworkshop.gymm.ui.program.common.BaseFragment;
import ru.codingworkshop.gymm.ui.program.common.MyAdapterDataObserver;
import ru.codingworkshop.gymm.ui.program.common.ItemTouchHelperCallback;
import ru.codingworkshop.gymm.ui.program.common.ProgramRecyclerView;
import ru.codingworkshop.gymm.ui.program.exercise.picker.ExercisePickerActivity;
import timber.log.Timber;

public class ProgramExerciseFragment extends BaseFragment {
    public static final String EXERCISE_ID_KEY = "exerciseId";
    public static final String EXERCISE_NAME_KEY = "exerciseName";
    private static final String PROGRAM_EXERCISE_ID_KEY = "programExerciseId";
    private static final String PROGRAM_TRAINING_ID_KEY = "programTrainingId";
    private static final int CANCEL_ALERT_ID = 0;
    private static final int EMPTY_EXERCISE_LIST_ALERT_ID = 1;
    private static final int EXERCISE_NOT_SELECTED_ALERT_ID = 2;
    public static final String TAG = "programExerciseFragmentTag";

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ProgramExerciseViewModel viewModel;
    private FragmentProgramExerciseBinding binding;
    private ProgramExerciseNode node;

    private FragmentAlert alert;
    private ProgramSetsAdapter programSetsAdapter;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        Timber.d("onAttach");
        viewModel = viewModelFactory.create(ProgramExerciseViewModel.class);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSaveExercise:
                if (validate()) {
                    viewModel.save();
                    getFragmentManager().popBackStack();
                }
                return true;
        }

        return false;
    }

    private boolean validate() {
        if (node.getExercise() == null) {
            alert.showOneButtonAlert(EXERCISE_NOT_SELECTED_ALERT_ID, R.string.program_exercise_activity_exercise_empty_text);
            return false;
        }
        if (!node.hasChildren()) {
            alert.showOneButtonAlert(EMPTY_EXERCISE_LIST_ALERT_ID, R.string.program_exercise_activity_empty_list_dialog_message);
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data.hasExtra(EXERCISE_ID_KEY) && data.hasExtra(EXERCISE_NAME_KEY)) {
            Exercise exercise = new Exercise();
            exercise.setId(data.getLongExtra(EXERCISE_ID_KEY, 0L));
            exercise.setName(data.getStringExtra(EXERCISE_NAME_KEY));
            setExercise(exercise);
        }
    }

    private void setExercise(Exercise exercise) {
        node.setExercise(exercise);
        binding.setExercise(exercise);
    }

    @Override
    protected Toolbar getToolbar() {
        Toolbar toolbar = binding.programExerciseToolbar;
        toolbar.inflateMenu(R.menu.program_exercise_menu);
        return toolbar;
    }

    @Override
    protected ViewDataBinding createBinding(LayoutInflater inflater, ViewGroup parent) {
        Timber.d("createBinding");
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_program_exercise, parent,
                false);
        binding.setInActionMode(getInActionMode());
        binding.programExerciseName.setOnClickListener(this::onExercisePick);
        binding.programExerciseAddSetButton.setOnClickListener(this::onAddSet);

        alert = new FragmentAlert(getChildFragmentManager(), (dialogId, positive) -> {
            switch (dialogId) {
                case CANCEL_ALERT_ID:
                    cleanupAndClose();
                    break;
            }
        });

        final Bundle arguments = getArguments();
        LiveData<ProgramExerciseNode> liveLoaded;
        if (arguments.containsKey(PROGRAM_EXERCISE_ID_KEY)) {
            liveLoaded = viewModel.load(arguments.getLong(PROGRAM_EXERCISE_ID_KEY));
        } else if (arguments.containsKey(PROGRAM_TRAINING_ID_KEY)) {
            viewModel.setProgramTrainingId(arguments.getLong(PROGRAM_TRAINING_ID_KEY));
            liveLoaded = viewModel.create();
        } else {
            throw new IllegalArgumentException("argument neither contains exercise id nor training id");
        }
        liveLoaded.observe(this, this::initData);

        return binding;
    }

    private void onAddSet(View view) {
        ProgramSetEditorFragment programSetEditor = ProgramSetEditorFragment.newInstance(node.getId());
        programSetEditor.listener = programSet -> { // FIXME: 22.11.2017 this shouldn't be set here
            viewModel.addProgramSet(programSet);
            programSetsAdapter.notifyItemInserted(programSet.getSortOrder());
        };
        programSetEditor.show(getChildFragmentManager());
    }

    private void initData(ProgramExerciseNode loadedNode) {
        if (loadedNode == null) return;
        Timber.d("initData");
        node = loadedNode;

        binding.setExercise(node.getExercise());

        initSetList();
    }

    private void initSetList() {
        final ProgramRecyclerView rv = binding.programSetList;
        ListItemListeners listeners = new ListItemListeners(R.layout.fragment_program_exercise_list_item)
                .setOnClickListener(this::onItemClick)
                .setOnLongClickListener(this::onItemLongClick)
                .setOnReorderButtonDownListener(rv::startDragFromChildView, R.id.programSetReorderImage);
        programSetsAdapter = new ProgramSetsAdapter(listeners, node.getChildren(), getInActionMode());
        MyAdapterDataObserver adapterDataObserver = new MyAdapterDataObserver(rv, R.id.programExerciseBackground, R.string.program_exercise_activity_set_deleted_message, node);
        programSetsAdapter.registerAdapterDataObserver(adapterDataObserver);
        rv.setAdapter(programSetsAdapter);
        rv.setItemTouchHelperCallback(new ItemTouchHelperCallback(node));
    }

    private void onItemClick(View view) {
        FragmentProgramExerciseListItemBinding binding = DataBindingUtil.getBinding(view);
        ProgramSetEditorFragment programSetEditor = ProgramSetEditorFragment.newInstance(binding.getWrappedSet().getProgramSet());
        programSetEditor.listener = programSet -> { // FIXME: 22.11.2017 this shouldn't be set here
            viewModel.replaceProgramSet(programSet);
            programSetsAdapter.notifyItemChanged(programSet.getSortOrder());
        };
        programSetEditor.show(getChildFragmentManager());
    }

    protected void onItemLongClick(View v) {
        ActionModeCallback actionModeCallback = new ActionModeCallback(binding.programSetList, getInActionMode());
        getActivity().startActionMode(actionModeCallback);
        viewModel.setChildrenChanged();
    }

    public static ProgramExerciseFragment newInstanceForNew(long programTrainingId) {
        return newInstance(PROGRAM_TRAINING_ID_KEY, programTrainingId);
    }

    public static ProgramExerciseFragment newInstanceForExistent(long programExerciseId) {
        return newInstance(PROGRAM_EXERCISE_ID_KEY, programExerciseId);
    }

    private static ProgramExerciseFragment newInstance(String key, long value) {
        Bundle args = new Bundle();
        args.putLong(key, value);
        ProgramExerciseFragment fragment = new ProgramExerciseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void onExercisePick(View view) {
        Intent pickerIntent = new Intent(getContext(), ExercisePickerActivity.class);
        startActivityForResult(pickerIntent, 0);
    }

    void onFragmentClose() {
        if (viewModel.isChanged()) {
            alert.showTwoButtonsAlert(0, R.string.cancel_changes_question);
        } else {
            cleanupAndClose();
        }
    }

    private void cleanupAndClose() {
        viewModel.deleteIfDrafting();
        close();
    }

    private void close() {
    }
}
