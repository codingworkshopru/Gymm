package ru.codingworkshop.gymm.ui.program.training;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.databinding.FragmentProgramTrainingBinding;
import ru.codingworkshop.gymm.ui.common.EditTextValidator;
import ru.codingworkshop.gymm.ui.common.ListItemListeners;
import ru.codingworkshop.gymm.ui.program.ProgramTrainingActivity;
import ru.codingworkshop.gymm.ui.program.ProgramTrainingViewModel;
import ru.codingworkshop.gymm.ui.program.common.ActionModeCallback;
import ru.codingworkshop.gymm.ui.program.common.BaseFragment;
import ru.codingworkshop.gymm.ui.program.common.FragmentAlert;
import ru.codingworkshop.gymm.ui.program.common.ItemTouchHelperCallback;
import ru.codingworkshop.gymm.ui.program.common.MyAdapterDataObserver;
import ru.codingworkshop.gymm.ui.program.common.ProgramRecyclerView;
import ru.codingworkshop.gymm.ui.program.exercise.ProgramExerciseFragment;
import ru.codingworkshop.gymm.ui.util.AlertDialogFragment;
import timber.log.Timber;

public class ProgramTrainingFragment extends BaseFragment implements
        AlertDialogFragment.OnDialogButtonClickListener,
        ProgramTrainingActivity.OnSystemBackPressedListener {

    public static final String TAG = "programTrainingFragmentTag";

    private static final int CANCEL_ALERT_ID = 0;
    private static final int EMPTY_EXERCISE_LIST_ALERT_ID = 1;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ProgramTrainingViewModel viewModel;
    private ProgramTrainingTree tree;
    private FragmentProgramTrainingBinding binding;

    private FragmentAlert alert;

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
        toolbar.setNavigationOnClickListener(v -> onFragmentClose());
        return toolbar;
    }

    @Override
    protected ViewDataBinding createBinding(LayoutInflater inflater, ViewGroup parent) {
        Timber.d("createBinding");
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_program_training, parent, false);
        binding.setInActionMode(new ObservableBoolean());
        binding.programTrainingAddExerciseButton.setOnClickListener(this::onAddExerciseButtonClick);

        alert = new FragmentAlert(getChildFragmentManager());
        Timber.d("alert created");

        tree = Preconditions.checkNotNull(viewModel.getProgramTrainingTree(), "tree is null");
        binding.setProgramTraining(tree.getParent());

        initExerciseList();

        return binding;
    }

    private void initExerciseList() {
        ProgramRecyclerView programExerciseList = binding.programExerciseList;
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

    @Override
    public void onDialogButtonClick(int dialogId, boolean positive) {
        if (!positive) return;

        switch (dialogId) {
            case CANCEL_ALERT_ID:
                close();
                break;
        }
    }

    private void onExerciseLongClick(View v) {
        final ActionModeCallback actionModeCallback = new ActionModeCallback(binding.programExerciseList, binding.getInActionMode());
        getActivity().startActionMode(actionModeCallback);
    }

    private void onAddExerciseButtonClick(View v) {
        viewModel.createProgramExercise();
        openExerciseEditor();
    }

    private void onExerciseClick(View v) {
        ProgramExerciseNode n = tree.getChildren().get(binding.programExerciseList.getChildAdapterPosition(v));
        viewModel.setProgramExercise(n);
        openExerciseEditor();
    }

    private void openExerciseEditor() {
        ProgramExerciseFragment programExerciseFragment =
                (ProgramExerciseFragment) getFragmentManager().findFragmentByTag(ProgramExerciseFragment.TAG);


        if (programExerciseFragment == null) {
            programExerciseFragment = new ProgramExerciseFragment();
        }

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.programTrainingFragmentContainer, programExerciseFragment, ProgramExerciseFragment.TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onFragmentClose() {
        viewModel.isProgramTrainingChanged().observe(this, this::showAlertIfChangedOrClose);
    }

    private void showAlertIfChangedOrClose(Boolean changed) {
        if (changed != null && changed) {
            if (!alert.isShown()) {
                alert.showTwoButtonsAlert(CANCEL_ALERT_ID, R.string.cancel_changes_question);
            }
        } else {
            close();
        }
    }

    private void close() {
        getActivity().finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSaveTraining:
                save();
                return true;
        }

        return false;
    }

    private void save() {
        final EditTextValidator editTextValidator =
            new EditTextValidator(binding.programTrainingNameLayout, R.string.program_training_activity_name_empty_error);

        if (syncValidate(editTextValidator)) {
            LiveDataUtil.getOnce(asyncValidate(editTextValidator), valid -> {
                if (valid != null && valid) {
                    viewModel.saveTree();
                    close();
                }
            });
        }
    }

    private boolean syncValidate(EditTextValidator editTextValidator) {
        if (!tree.hasChildren()) {
            alert.showOneButtonAlert(EMPTY_EXERCISE_LIST_ALERT_ID, R.string.program_training_activity_empty_list_dialog_message);
            return false;
        }

        return editTextValidator.validate();
    }

    private LiveData<Boolean> asyncValidate(EditTextValidator editTextValidator) {
        return Transformations.map(viewModel.validateProgramTraining(), valid -> {
            editTextValidator.addValidation(s -> valid, R.string.program_training_activity_name_duplicate_error);
            return editTextValidator.validate();
        });
    }
}
