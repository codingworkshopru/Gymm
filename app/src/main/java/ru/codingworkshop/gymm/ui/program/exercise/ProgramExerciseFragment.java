package ru.codingworkshop.gymm.ui.program.exercise;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.databinding.FragmentProgramExerciseBinding;
import ru.codingworkshop.gymm.databinding.FragmentProgramExerciseListItemBinding;
import ru.codingworkshop.gymm.ui.program.ProgramTrainingActivity;
import ru.codingworkshop.gymm.ui.program.ProgramTrainingViewModel;
import ru.codingworkshop.gymm.ui.program.common.FragmentAlert;
import ru.codingworkshop.gymm.ui.common.ListItemListeners;
import ru.codingworkshop.gymm.ui.program.common.ActionModeCallback;
import ru.codingworkshop.gymm.ui.program.common.BaseFragment;
import ru.codingworkshop.gymm.ui.program.common.MyAdapterDataObserver;
import ru.codingworkshop.gymm.ui.program.common.ItemTouchHelperCallback;
import ru.codingworkshop.gymm.ui.program.common.ProgramRecyclerView;
import ru.codingworkshop.gymm.ui.program.exercise.picker.ExercisePickerActivity;
import ru.codingworkshop.gymm.ui.util.AlertDialogFragment;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

public class ProgramExerciseFragment extends BaseFragment implements
        AlertDialogFragment.OnDialogButtonClickListener,
        ProgramTrainingActivity.OnSystemBackPressedListener,
        ProgramSetEditorFragment.OnProgramSetEditedListener
{
    public static final String EXERCISE_ID_KEY = "exerciseId";
    public static final String EXERCISE_NAME_KEY = "exerciseName";
    private static final int CANCEL_ALERT_ID = 0;
    private static final int EMPTY_EXERCISE_LIST_ALERT_ID = 1;
    private static final int EXERCISE_NOT_SELECTED_ALERT_ID = 2;
    public static final String TAG = "programExerciseFragmentTag";

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ProgramTrainingViewModel viewModel;
    private FragmentProgramExerciseBinding binding;
    private ProgramExerciseNode node;

    private FragmentAlert alert;
    private ProgramSetsAdapter programSetsAdapter;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        Timber.d("onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        viewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(ProgramTrainingViewModel.class);
        viewModel.getProgramExercise().observe(this, this::initData);
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
                    viewModel.saveProgramExercise();
                    close();
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
        if (resultCode == RESULT_OK && data != null && data.hasExtra(EXERCISE_ID_KEY) && data.hasExtra(EXERCISE_NAME_KEY)) {
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
        binding.programExerciseToolbar.setNavigationOnClickListener(v -> onFragmentClose());

        alert = new FragmentAlert(getChildFragmentManager());

        return binding;
    }

    @Override
    public void onDialogButtonClick(int dialogId, boolean positive) {
        if (!positive) return;

        switch (dialogId) {
            case CANCEL_ALERT_ID:
                close();
                break;
        }
    }

    private void onAddSet(View view) {
        viewModel.createProgramSet();
        showSetEditor();
    }

    private void onCreateSet(View view) {
        FragmentProgramExerciseListItemBinding binding = DataBindingUtil.getBinding(view);
        ProgramSet programSet = binding.getWrappedSet().getProgramSet();
        viewModel.setProgramSet(programSet);
        showSetEditor();
    }

    private void showSetEditor() {
        final ProgramSetEditorFragment programSetEditor = new ProgramSetEditorFragment();
        final FragmentManager childFragmentManager = getChildFragmentManager();
        programSetEditor.show(childFragmentManager);
    }

    @Override
    public void onProgramSetEdited(ProgramSet set) {
        final int sortOrder = set.getSortOrder();
        viewModel.getProgramSet().observe(this, s -> {
            if (s != null) return;

            if (sortOrder == -1) {
                programSetsAdapter.notifyItemInserted(node.getChildrenCount());
            } else {
                programSetsAdapter.notifyItemChanged(sortOrder);
            }
            viewModel.getProgramSet().removeObservers(this);
        });
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
                .setOnClickListener(this::onCreateSet)
                .setOnLongClickListener(this::onItemLongClick)
                .setOnReorderButtonDownListener(rv::startDragFromChildView, R.id.programSetReorderImage);
        programSetsAdapter = new ProgramSetsAdapter(listeners, node.getChildren(), getInActionMode());
        MyAdapterDataObserver adapterDataObserver = new MyAdapterDataObserver(rv, R.id.programExerciseBackground, R.string.program_exercise_activity_set_deleted_message, node);
        programSetsAdapter.registerAdapterDataObserver(adapterDataObserver);
        rv.setAdapter(programSetsAdapter);
        rv.setItemTouchHelperCallback(new ItemTouchHelperCallback(node));
    }

    protected void onItemLongClick(View v) {
        ActionModeCallback actionModeCallback = new ActionModeCallback(binding.programSetList, getInActionMode());
        getActivity().startActionMode(actionModeCallback);
    }

    private void onExercisePick(View view) {
        Intent pickerIntent = new Intent(getContext(), ExercisePickerActivity.class);
        startActivityForResult(pickerIntent, 0);
    }

    @Override
    public void onFragmentClose() {
        if (viewModel.isProgramExerciseChanged()) {
            alert.showTwoButtonsAlert(CANCEL_ALERT_ID, R.string.cancel_changes_question);
        } else {
            close();
        }
    }

    private void close() {
        getFragmentManager().popBackStack();
    }
}
