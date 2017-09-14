package ru.codingworkshop.gymm.ui.actual;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import javax.inject.Inject;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
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
    private MyFragmentPagerAdapter fragmentPagerAdapter;
    private ViewPager viewPager;

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
        fragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), tree);
        viewPager = new ViewPager(this);
        viewPager.setId(View.generateViewId());
        ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
        layoutParams.height = ViewPager.LayoutParams.MATCH_PARENT;
        layoutParams.width = ViewPager.LayoutParams.MATCH_PARENT;
        viewPager.setLayoutParams(layoutParams);
        viewPager.setAdapter(fragmentPagerAdapter);

        setToolbarTitle(tree.getProgramTraining().getName());
        RecyclerView recyclerView = findViewById(R.id.actualExerciseSteps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(new BindingListAdapter<ActualExerciseNode, ActivityActualTrainingStepperItemBinding>(tree.getChildren()) {
            private ActivityActualTrainingStepperItemBinding activeView;

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
                        FrameLayout layout = activeView.getRoot().findViewById(R.id.stepperItemActualSetsContainer);
                        layout.removeView(viewPager);
                    }

                    FrameLayout layout = binding.getRoot().findViewById(R.id.stepperItemActualSetsContainer);
                    layout.addView(viewPager);
                    viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), tree));
//                    fragmentPagerAdapter.notifyDataSetChanged(binding.getIndex());

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
                binding.setIndex(sortOrder);
                binding.setTitle(programExerciseNode.getExercise().getName());
            }
        });
    }

    private void setToolbarTitle(String title) {
        Toolbar toolbar = findViewById(R.id.actualTrainingToolbar);
        toolbar.setTitle(title);
    }

    private static class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private int exerciseIndex;
        private ActualTrainingTree tree;

        public MyFragmentPagerAdapter(FragmentManager fm, ActualTrainingTree tree) {
            super(fm);
            this.tree = tree;
        }

        @Override
        public Fragment getItem(int position) {
            final int reps = getProgramSets().get(position).getReps();
            return ActualSetFragment.newInstance(position, getCount(), reps);
        }

        @Override
        public int getCount() {
            return getProgramSets().size();
        }

        private List<ProgramSet> getProgramSets() {
            return tree.getChildren().get(exerciseIndex).getProgramExerciseNode().getChildren();
        }

        public void notifyDataSetChanged(int exerciseIndex) {
            this.exerciseIndex = exerciseIndex;
            notifyDataSetChanged();
        }
    }
}

