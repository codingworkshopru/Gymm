package ru.codingworkshop.gymm.ui.actual;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import javax.inject.Inject;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.databinding.ActivityActualTrainingStepperItemBinding;

public class ActualTrainingActivity extends LifecycleActivity implements ActualSetFragment.OnActualSetSaveListener {
    public static final String EXTRA_ACTUAL_TRAINING_ID = "extraActualTrainingId";
    public static final String EXTRA_PROGRAM_TRAINING_ID = "extraProgramTrainingId";

    private ActualTrainingViewModel viewModel;
    private ActualTrainingTree tree;

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    @Inject public ViewModelProvider.Factory viewModelFactory;

    private ViewPager viewPager;
    private ActualSetsFragmentPagerAdapter fragmentPagerAdapter;

    private ActivityActualTrainingStepperItemBinding activeBindingItem;
    private RecyclerView recyclerView;

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

    @SuppressLint("NewApi")
    private void initViews() {
        setToolbarTitle(tree.getProgramTraining().getName());

        fragmentPagerAdapter = new ActualSetsFragmentPagerAdapter(getSupportFragmentManager());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        viewPager = new ActualSetsViewPager(this);
        viewPager.setId(View.generateViewId());
        viewPager.setLayoutParams(layoutParams);
        viewPager.setAdapter(fragmentPagerAdapter);

        recyclerView = findViewById(R.id.actualExerciseSteps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ActualTrainingStepperAdapter(tree.getChildren(), this, this::onStepperItemActivate));
    }

    @Override
    public void onActualSetSave(int index, ActualSet actualSet) {
        viewModel.createActualSet(activeBindingItem.getIndex(), actualSet);
    }

    public void onStepperItemActivate(View view) {
        ActivityActualTrainingStepperItemBinding binding = DataBindingUtil.getBinding(view);

        if (binding == activeBindingItem) {
            return;
        }
        viewModel.createActualExercise(recyclerView.getChildAdapterPosition(binding.getRoot()));

        if (activeBindingItem != null) {
            activeBindingItem.setActive(false);
            FrameLayout layout = activeBindingItem.getRoot().findViewById(R.id.stepperItemActualSetsContainer);
            layout.removeView(viewPager);
        }

        FrameLayout layout = binding.getRoot().findViewById(R.id.stepperItemActualSetsContainer);
        viewPager.setCurrentItem(0, false);
        layout.addView(viewPager);
        fragmentPagerAdapter.notifyDataSetChanged(tree.getChildren().get(binding.getIndex()));
        viewPager.setOffscreenPageLimit(fragmentPagerAdapter.getCount() - 1); // FIXME this is a bad idea, but otherwise ViewPager stuck

        binding.setActive(true);

        activeBindingItem = binding;
    }

    private void setToolbarTitle(String title) {
        Toolbar toolbar = findViewById(R.id.actualTrainingToolbar);
        toolbar.setTitle(title);
    }
}

