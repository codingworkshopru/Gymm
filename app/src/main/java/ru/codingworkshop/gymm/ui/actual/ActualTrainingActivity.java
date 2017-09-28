package ru.codingworkshop.gymm.ui.actual;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import javax.inject.Inject;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.databinding.ActivityActualTrainingStepperItemBinding;
import ru.codingworkshop.gymm.db.GymmDatabase;
import ru.codingworkshop.gymm.service.TrainingForegroundService;
import ru.codingworkshop.gymm.ui.ActivityAlerts;
import ru.codingworkshop.gymm.ui.actual.viewmodel.ActualTrainingViewModel;

public class ActualTrainingActivity extends AppCompatActivity
        implements ActualSetFragment.OnActualSetSaveListener {

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

    private ActivityAlerts alerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_actual_training);
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.actualTrainingToolbar);
        setSupportActionBar(toolbar);

        initViewModel();
        loadData();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ActualTrainingViewModel.class);
    }

    private void loadData() {
        if (viewModel.getActualTrainingTree() != null) {
            dataLoaded(true);
            return;
        }

        if (getIntent().hasExtra(EXTRA_PROGRAM_TRAINING_ID)) {
            startTraining();
        } else if (getIntent().hasExtra(EXTRA_ACTUAL_TRAINING_ID)) {
            resumeTraining();
        } else {
            throw new IllegalStateException("Activity must receive data id");
        }
    }

    private void startTraining() {
        final long programTrainingId = getIntent().getLongExtra(EXTRA_PROGRAM_TRAINING_ID, 0L);
        final LiveData<Boolean> liveLoaded = viewModel.startTraining(programTrainingId);
        liveLoaded.observe(this, this::dataLoaded);
        liveLoaded.observe(this, this::startTrainingService);
    }

    private void resumeTraining() {
        final long actualTrainingId = getIntent().getLongExtra(EXTRA_ACTUAL_TRAINING_ID, 0L);
        final LiveData<Boolean> liveLoaded = viewModel.loadTraining(actualTrainingId);
        liveLoaded.observe(this, this::dataLoaded);
    }

    private void dataLoaded(boolean loaded) {
        if (!loaded) return;

        tree = viewModel.getActualTrainingTree();
        initViews();
    }

    private void startTrainingService(boolean loaded) {
        if (!loaded) return;

        // TODO if the service crashed it must be started with ActualTraining.getStartTime()
        TrainingForegroundService.startService(
                this, tree.getParent().getId(), tree.getProgramTraining().getName());
    }

    @SuppressLint("NewApi") // FIXME
    private void initViews() {
        setToolbarTitle(tree.getProgramTraining().getName());

        alerts = new ActivityAlerts(getSupportFragmentManager(), this::onAlertButtonClick);

        fragmentPagerAdapter = new ActualSetsFragmentPagerAdapter(getSupportFragmentManager());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        viewPager = new ActualSetsViewPager(this);
        viewPager.setId(View.generateViewId());
        viewPager.setLayoutParams(layoutParams);
        viewPager.setAdapter(fragmentPagerAdapter);

        recyclerView = (RecyclerView) findViewById(R.id.actualExerciseSteps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ActualTrainingStepperAdapter(tree.getChildren(), this, this::onStepperItemActivate));
    }

    public void onAlertButtonClick(int dialogId, boolean positive) {
        if (dialogId == 0) {
            if (positive) {
                viewModel.finishTraining();
            } else {
                alerts.hideAlert();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actual_training_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_finish_training) {
            alerts.showAlertWithDefaultButtons(0, R.string.actual_training_activity_are_you_sure_message);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActualSetSave(int index, ActualSet actualSet) {
        final int currentStep = activeBindingItem.getIndex();

        if (GymmDatabase.isValidId(actualSet)) {
            viewModel.updateActualSet(currentStep, actualSet);
        } else {
            viewModel.createActualSet(currentStep, actualSet);

            if (index == viewPager.getAdapter().getCount() - 1) {
                goToNextStep();
            } else {
                viewPager.setCurrentItem(index + 1);
            }
        }
    }

    public void goToNextStep() {
        final int nextStep = activeBindingItem.getIndex() + 1;
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(nextStep);

        final ActualExerciseNode currentExerciseNode = tree.getChildren().get(nextStep - 1);
        activeBindingItem.setDone(currentExerciseNode.getChildren().size() == currentExerciseNode.getProgramExerciseNode().getChildren().size());

        if (viewHolder == null) {
            recyclerView.scrollToPosition(nextStep);
            recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(View view) {
                    View nextStepView = recyclerView.findViewHolderForAdapterPosition(nextStep).itemView;
                    onStepperItemActivate(nextStepView);
                    recyclerView.removeOnChildAttachStateChangeListener(this);
                }

                public void onChildViewDetachedFromWindow(View view) {}
            });
        } else {
            onStepperItemActivate(viewHolder.itemView);
        }
    }

    public void onStepperItemActivate(View view) {
        ActivityActualTrainingStepperItemBinding binding = DataBindingUtil.getBinding(view);

        if (binding == activeBindingItem) {
            return;
        }
        viewModel.createActualExercise(recyclerView.getChildAdapterPosition(binding.getRoot()));

        if (activeBindingItem != null) {
            activeBindingItem.setActive(false);
            FrameLayout layout = (FrameLayout) activeBindingItem.getRoot().findViewById(R.id.stepperItemActualSetsContainer);
            layout.removeView(viewPager);
        }

        FrameLayout layout = (FrameLayout) binding.getRoot().findViewById(R.id.stepperItemActualSetsContainer);
        viewPager.setCurrentItem(0, false);
        layout.addView(viewPager);
        fragmentPagerAdapter.notifyDataSetChanged(tree.getChildren().get(binding.getIndex()));
        viewPager.setOffscreenPageLimit(fragmentPagerAdapter.getCount() - 1); // FIXME this is a bad idea, but otherwise ViewPager stuck on API 24+

        binding.setActive(true);

        activeBindingItem = binding;
    }

    private void setToolbarTitle(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actualTrainingToolbar);
        toolbar.setTitle(title);
    }
}

