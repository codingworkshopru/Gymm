package ru.codingworkshop.gymm.ui.program.exercise;


import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.databinding.FragmentProgramExerciseBinding;
import ru.codingworkshop.gymm.ui.MainActivity;
import ru.codingworkshop.gymm.ui.TwoButtonAlert;
import ru.codingworkshop.gymm.ui.common.ListItemListeners;
import ru.codingworkshop.gymm.ui.program.common.ActionModeCallback;
import ru.codingworkshop.gymm.ui.program.common.BaseFragment;
import ru.codingworkshop.gymm.ui.program.common.MyAdapterDataObserver;
import ru.codingworkshop.gymm.ui.program.common.MySimpleCallback;
import ru.codingworkshop.gymm.ui.program.common.ProgramRecyclerView;

public class ProgramExerciseFragment extends BaseFragment {
    private static final String PROGRAM_EXERCISE_ID_KEY = "programExerciseId";
    private static final String PROGRAM_TRAINING_ID_KEY = "programTrainingId";
    static final String EXERCISE_ID_KEY = "exerciseId";
    static final String EXERCISE_NAME_KEY = "exerciseName";

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ProgramExerciseViewModel viewModel;
    private FragmentProgramExerciseBinding binding;
    private ProgramExerciseNode node;

    private TwoButtonAlert alert;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = viewModelFactory.create(ProgramExerciseViewModel.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSaveExercise:
                viewModel.save();
                return true;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data.hasExtra(EXERCISE_ID_KEY) && data.hasExtra(EXERCISE_NAME_KEY)) {
            Exercise exercise = new Exercise();
            exercise.setId(data.getLongExtra(EXERCISE_ID_KEY, 0L));
            exercise.setName(data.getStringExtra(EXERCISE_NAME_KEY));
            setExercise(exercise);
        } else {
            throw new IllegalArgumentException("Activity must return id and name of exercise");
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_program_exercise, parent,
                false);
        binding.setInActionMode(getInActionMode());
        binding.programExerciseName.setOnClickListener(this::onExercisePick);

        alert = new TwoButtonAlert(getChildFragmentManager(), (dialogId, positive) -> {
            if (positive) { cleanupAndClose(); }
        });

        viewModel.load(getArguments().getLong(PROGRAM_EXERCISE_ID_KEY)).observe(this, this::init);

        return binding;
    }

    private void init(boolean loaded) {
        if (!loaded) return;

        node = viewModel.getProgramExerciseNode();

        binding.setExercise(node.getExercise());

        initSetList();
    }

    private void initSetList() {
        final ProgramRecyclerView rv = binding.programSetList;
        ListItemListeners listeners = new ListItemListeners(R.layout.fragment_program_exercise_list_item)
                .setOnLongClickListener(this::onItemLongClick)
                .setOnReorderButtonDownListener(rv::startDragFromChildView, R.id.programSetReorderImage);
        final ProgramSetsAdapter adapter = new ProgramSetsAdapter(listeners, node.getChildren(), getInActionMode());
        MyAdapterDataObserver adapterDataObserver = new MyAdapterDataObserver(rv, R.id.programExerciseBackground, R.string.program_exercise_activity_set_deleted_message, node);
        adapter.registerAdapterDataObserver(adapterDataObserver);
        rv.setAdapter(adapter);
        rv.setItemTouchHelperCallback(new MySimpleCallback(node));
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
        Intent pickerIntent = new Intent(getContext(), MainActivity.class);
        startActivityForResult(pickerIntent, 0);
    }

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
    }
}
