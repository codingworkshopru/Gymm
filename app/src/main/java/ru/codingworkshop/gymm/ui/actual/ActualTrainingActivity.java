package ru.codingworkshop.gymm.ui.actual;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import javax.inject.Inject;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.databinding.ActivityActualTrainingStepperItemBinding;
import ru.codingworkshop.gymm.ui.common.BindingListAdapter;

public class ActualTrainingActivity extends LifecycleActivity {
    public static final String EXTRA_ACTUAL_TRAINING_ID = "extraActualTrainingId";
    public static final String EXTRA_PROGRAM_TRAINING_ID = "extraProgramTrainingId";

    private ActualTrainingViewModel viewModel;
    private ActualTrainingTree tree;

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    @Inject public ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_actual_training);
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        initViewModel();
        loadData();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ActualTrainingViewModel.class);
    }

    private void loadData() {
        final long foo = getIntent().getLongExtra(EXTRA_PROGRAM_TRAINING_ID, 0L);
        viewModel.startTraining(foo).observe(this, this::dataLoaded);
    }

    private void dataLoaded(boolean loaded) {
        if (!loaded) return;

        tree = viewModel.getActualTrainingTree();
        initViews();
    }

    private void initViews() {
        setToolbarTitle(tree.getProgramTraining().getName());
        RecyclerView recyclerView = findViewById(R.id.actualExerciseSteps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(new BindingListAdapter<ActualExerciseNode, ActivityActualTrainingStepperItemBinding>(tree.getChildren()) {
            ActivityActualTrainingStepperItemBinding activeView;

            @Override
            protected ActivityActualTrainingStepperItemBinding createBinding(ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(ActualTrainingActivity.this);
                ActivityActualTrainingStepperItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.activity_actual_training_stepper_item, parent, false);
                binding.getRoot().setOnClickListener(v -> {
                    if (binding == activeView) {
                        return;
                    }

                    viewModel.createActualExercise(recyclerView.getChildAdapterPosition(binding.getRoot()));

                    if (activeView != null) {
                        activeView.setActive(false);
                    }
                    binding.setActive(true);
                    activeView = binding;
                });
                return binding;
            }

            @Override
            protected void bind(ActivityActualTrainingStepperItemBinding binding, ActualExerciseNode item) {
                final ProgramExerciseNode programExerciseNode = item.getProgramExerciseNode();
                final int sortOrder = programExerciseNode.getSortOrder();
                binding.setFirst(sortOrder == 0);
                binding.setLast(sortOrder == tree.getChildren().size() - 1);
                binding.setIndex(sortOrder + 1);
                binding.setTitle(programExerciseNode.getExercise().getName());
            }
        });
    }

    private void setToolbarTitle(String title) {
        Toolbar toolbar = findViewById(R.id.actualTrainingToolbar);
        toolbar.setTitle(title);
    }
}
