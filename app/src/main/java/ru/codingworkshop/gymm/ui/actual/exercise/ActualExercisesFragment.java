package ru.codingworkshop.gymm.ui.actual.exercise;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import javax.inject.Inject;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.databinding.ActivityActualTrainingStepperItemBinding;
import ru.codingworkshop.gymm.databinding.FragmentActualSetBinding;
import ru.codingworkshop.gymm.db.GymmDatabase;
import ru.codingworkshop.gymm.service.TrainingForegroundService;
import ru.codingworkshop.gymm.ui.TwoButtonAlert;
import ru.codingworkshop.gymm.ui.actual.ActualSetFragment;
import ru.codingworkshop.gymm.ui.actual.ActualSetsFragmentPagerAdapter;
import ru.codingworkshop.gymm.ui.actual.ActualSetsViewPager;
import ru.codingworkshop.gymm.ui.actual.ActualTrainingStepperAdapter;
import ru.codingworkshop.gymm.ui.actual.viewmodel.ActualTrainingViewModel;

public class ActualExercisesFragment extends Fragment implements
        ActualSetFragment.OnActualSetSaveListener {

    public static final String EXTRA_ACTUAL_TRAINING_ID = "extraActualTrainingId";
    public static final String EXTRA_PROGRAM_TRAINING_ID = "extraProgramTrainingId";

    public interface ActualExercisesCallback {
        void onStartRest(int restSeconds);
        void onLoadingFinished();
    }

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    public ActualExercisesCallback callback;

    private ActualTrainingViewModel viewModel;
    private ActualTrainingTree tree;

    private RecyclerView exerciseList;
    private ActualSetsViewPager setsViewPager;
    private TwoButtonAlert alert;

    private ActualSetsFragmentPagerAdapter setsPagerAdapter;
    private ActualTrainingStepperAdapter exercisesAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ActualTrainingViewModel.class);

        tree = viewModel.getActualTrainingTree();
        if (tree != null) {
            return;
        }

        Bundle arguments = getArguments();
        long programTrainingId = arguments.getLong(EXTRA_PROGRAM_TRAINING_ID);
        long actualTrainingId = arguments.getLong(EXTRA_ACTUAL_TRAINING_ID);

        LiveData<Boolean> liveLoaded;
        if (actualTrainingId != 0L) {
            liveLoaded = viewModel.loadTraining(actualTrainingId);
        } else if (programTrainingId != 0L) {
            liveLoaded = viewModel.startTraining(programTrainingId);
        } else {
            throw new IllegalStateException("Activity must receive data id");
        }

        liveLoaded.observe(this, this::onTrainingTreeLoaded);
    }

    private void onTrainingTreeLoaded(boolean loaded) {
        if (!loaded) return;

        tree = viewModel.getActualTrainingTree();

        callback.onLoadingFinished();

        TrainingForegroundService.startService(getContext(), tree.getParent().getId(),
                tree.getProgramTraining().getName());

        initUi(getView());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actual_exercises, container, false);

        initUi(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (callback == null) {
            if (context instanceof ActualExercisesCallback) {
                callback = (ActualExercisesCallback) context;
            } else {
                throw new IllegalStateException("Activity must implement "
                        + ActualExercisesCallback.class);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_finish_training) {
            alert.showAlertWithDefaultButtons(0, R.string.actual_training_activity_are_you_sure_message);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActualSetSave(int setIndex, ActualSet actualSet) {
        final int currentExerciseIndex = exercisesAdapter.getActiveBinding().getIndex();
        if (GymmDatabase.isValidId(actualSet)) {
            viewModel.updateActualSet(currentExerciseIndex, actualSet);
        } else {
            viewModel.createActualSet(currentExerciseIndex, actualSet);
            startRestIfNeeded(setIndex, currentExerciseIndex);
            goNext();
        }
    }

    private void startRestIfNeeded(int setIndex, int currentExerciseIndex) {
        Integer secondsForRest = tree.getChildren().get(currentExerciseIndex)
                .getProgramExerciseNode().getChildren().get(setIndex).getSecondsForRest();

        if (secondsForRest != null && secondsForRest > 0) {
            callback.onStartRest(secondsForRest);
        }
    }

    // go to next set or exercise
    private void goNext() {
        final int currentSetIndex = setsViewPager.getCurrentItem();
        FragmentActualSetBinding setCurrentBinding = setsPagerAdapter.getBinding(currentSetIndex);

        if (setCurrentBinding == null) return;

        if (setCurrentBinding.getIndex() < setCurrentBinding.getSetsCount() - 1) {
            setsViewPager.setCurrentItem(currentSetIndex + 1);
        } else {
            getToNextExercise();
        }
    }

    private void getToNextExercise() {// TODO: 01.10.2017 shouldn't go to finished exercise

        final ActivityActualTrainingStepperItemBinding exerciseCurrentBinding =
                exercisesAdapter.getActiveBinding();

        final int currentExerciseIndex = exerciseCurrentBinding.getIndex();
        exerciseList.getAdapter().notifyItemChanged(currentExerciseIndex);

        if (exerciseCurrentBinding.getLast()) {
            // TODO: 01.10.2017 finish training
        } else {
            final int nextExerciseIndex = currentExerciseIndex + 1;
            RecyclerView.ViewHolder nextExerciseItem = exerciseList.findViewHolderForAdapterPosition(nextExerciseIndex);
            if (nextExerciseItem == null) {
                exerciseList.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                    @Override
                    public void onChildViewAttachedToWindow(View view) {
                        exerciseList.findViewHolderForAdapterPosition(nextExerciseIndex).itemView.callOnClick();
                        exerciseList.removeOnChildAttachStateChangeListener(this);
                    }

                    @Override
                    public void onChildViewDetachedFromWindow(View view) {

                    }
                });
                exerciseList.scrollToPosition(nextExerciseIndex);
            } else {
                nextExerciseItem.itemView.callOnClick();
            }
        }
    }

    private void initUi(View view) {
        if (tree == null || view == null) return;

        alert = new TwoButtonAlert(getChildFragmentManager(), this::onAlertButtonClick);

        initToolbar(view);
        initExerciseList(view);
        initSetsViewPager();
    }

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.actualExercisesToolbar);
        toolbar.inflateMenu(R.menu.actual_training_menu);
        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);
        toolbar.setTitle(tree.getProgramTraining().getName());
    }

    private void initExerciseList(View view) {
        exerciseList = view.findViewById(R.id.actualExerciseList);
        exerciseList.setLayoutManager(new LinearLayoutManager(getContext()));
        exercisesAdapter = new ActualTrainingStepperAdapter(
                tree.getChildren(), getContext(), this::onStepperItemActivate);
        exerciseList.setAdapter(exercisesAdapter);
    }

    private void initSetsViewPager() {
        setsViewPager = new ActualSetsViewPager(getContext());
        setsViewPager.setId(View.generateViewId());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        setsViewPager.setLayoutParams(layoutParams);

        setsPagerAdapter = new ActualSetsFragmentPagerAdapter(getChildFragmentManager());
        setsViewPager.setAdapter(setsPagerAdapter);
    }

    public void onAlertButtonClick(int dialogId, boolean positive) {
        if (dialogId == 0) {
            if (positive) {
                viewModel.finishTraining();
            } else {
                alert.hideAlert();
            }
        }
    }

    public void onStepperItemActivate(ActivityActualTrainingStepperItemBinding oldItemBinding,
                                      ActivityActualTrainingStepperItemBinding newItemBinding) {

        final int newItemIndex = newItemBinding.getIndex();

        viewModel.createActualExercise(newItemIndex);

        final View fromItem = oldItemBinding != null ? oldItemBinding.getRoot() : null;
        moveSetsViewPager(fromItem, newItemBinding.getRoot());

        setsPagerAdapter.notifyDataSetChanged(tree.getChildren().get(newItemIndex));
    }

    private void moveSetsViewPager(View fromItem, View toItem) {
        if (fromItem != null) {
            FrameLayout layout = fromItem.findViewById(R.id.stepperItemActualSetsContainer);
            layout.removeView(setsViewPager);
        }

        FrameLayout layout = toItem.findViewById(R.id.stepperItemActualSetsContainer);
        setsViewPager.setCurrentItem(0, false);
        layout.addView(setsViewPager);
    }
}
