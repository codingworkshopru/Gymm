package ru.codingworkshop.gymm.ui.program.training;


import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import ru.codingworkshop.gymm.ui.common.EditTextValidator;
import ru.codingworkshop.gymm.ui.common.ListItemListeners;
import ru.codingworkshop.gymm.ui.program.common.ActionModeCallback;
import ru.codingworkshop.gymm.ui.program.common.MyAdapterDataObserver;
import ru.codingworkshop.gymm.ui.program.common.MySimpleCallback;
import ru.codingworkshop.gymm.ui.program.common.ProgramRecyclerView;

public class ProgramTrainingFragment extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ProgramTrainingViewModel viewModel;
    private ProgramTrainingTree tree;
    private Context context;
    private FragmentProgramTrainingBinding binding;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (viewModel == null) {
            viewModel = viewModelFactory.create(ProgramTrainingViewModel.class);
        }
        tree = viewModel.getProgramTrainingTree();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_program_training, container, false);
        binding.setProgramTraining(tree.getParent());
        binding.setInActionMode(new ObservableBoolean());

        Toolbar toolbar = binding.programTrainingToolbar;
        toolbar.inflateMenu(R.menu.program_training_menu);
        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);

        initExerciseList();
        return binding.getRoot();
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
                            getActivity().finish();
                        }
                    });
                }
                return true;
        }

        return false;
    }

    private void initExerciseList() {
        ProgramRecyclerView rv = binding.programExerciseList;
        ListItemListeners listeners = new ListItemListeners(R.layout.fragment_program_training_list_item)
                .setOnLongClickListener(v -> getActivity().startActionMode(new ActionModeCallback(rv, binding.getInActionMode())))
                .setOnReorderButtonDownListener(rv::startDragFromChildView, R.id.programExerciseReorderImage);
        ProgramExercisesAdapter adapter = new ProgramExercisesAdapter(listeners, tree.getChildren(), binding.getInActionMode());
        MyAdapterDataObserver observer = new MyAdapterDataObserver(
                rv, R.id.programTrainingBackground,
                R.string.program_training_activity_exercise_deleted_message, tree);
        adapter.registerAdapterDataObserver(observer);
        rv.setAdapter(adapter);
        rv.setItemTouchHelperCallback(new MySimpleCallback(tree));
    }
}
