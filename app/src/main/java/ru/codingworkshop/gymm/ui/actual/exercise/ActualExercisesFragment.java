package ru.codingworkshop.gymm.ui.actual.exercise;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.common.base.Preconditions;

import javax.inject.Inject;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.databinding.FragmentActualSetBinding;
import ru.codingworkshop.gymm.db.GymmDatabase;
import ru.codingworkshop.gymm.service.TrainingForegroundService;
import ru.codingworkshop.gymm.ui.program.common.FragmentAlert;
import ru.codingworkshop.gymm.ui.actual.ServiceBindController;
import ru.codingworkshop.gymm.ui.actual.set.ActualSetDataBindingWrapper;
import ru.codingworkshop.gymm.ui.actual.set.ActualSetFragment;
import ru.codingworkshop.gymm.ui.actual.set.ActualSetsFragmentPagerAdapter;
import ru.codingworkshop.gymm.ui.actual.set.ActualSetsViewPager;
import ru.codingworkshop.gymm.ui.actual.viewmodel.ActualTrainingViewModel;
import ru.codingworkshop.gymm.ui.common.ListItemListeners;

public class ActualExercisesFragment extends Fragment implements
        ActualSetFragment.OnActualSetSaveListener {

    public static final String EXTRA_ACTUAL_TRAINING_ID = "extraActualTrainingId";
    public static final String EXTRA_PROGRAM_TRAINING_ID = "extraProgramTrainingId";
    static final String PREFS_NAME = "exerciseAndSetIndices";
    private static final String PREFS_ACTUAL_TRAINING_ID_KEY = "prefsActualTrainingId";
    private static final String PREFS_EXERCISE_INDEX_KEY = "prefsExerciseIndex";
    private static final String PREFS_SET_INDEX_KEY = "prefsSetIndex";

    public interface ActualExercisesCallback {
        void onStartRest(int restSeconds);
        void onLoadingFinished();
    }

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    public ActualExercisesCallback callback;
    private Context context;

    private ActualTrainingViewModel viewModel;
    private ActualTrainingTree tree;

    private ActualExercisesStepperView exerciseList;
    private ActualSetsViewPager setsViewPager;
    private FragmentAlert alert;

    private ActualSetsFragmentPagerAdapter setsPagerAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ActualTrainingViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actual_exercises, container, false);
    }

    @NonNull
    @Override
    public View getView() {
        return Preconditions.checkNotNull(super.getView(), "getView called before onCreateView");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (viewModel.getActualTrainingTree() != null) {
            onTrainingTreeLoaded(viewModel.getActualTrainingTree());
            return;
        }

        Bundle arguments = getArguments();
        long programTrainingId = arguments.getLong(EXTRA_PROGRAM_TRAINING_ID);
        long actualTrainingId = arguments.getLong(EXTRA_ACTUAL_TRAINING_ID);

        LiveData<ActualTrainingTree> liveTree;
        if (actualTrainingId != 0L) {
            liveTree = viewModel.loadTraining(actualTrainingId);
        } else if (programTrainingId != 0L) {
            liveTree = viewModel.startTraining(programTrainingId);
        } else {
            throw new IllegalStateException("Activity must receive data id");
        }

        liveTree.observe(this, this::onTrainingTreeLoaded);
    }

    private void onTrainingTreeLoaded(ActualTrainingTree loadedTree) {
        if (loadedTree == null) return;

        tree = loadedTree;

        callback.onLoadingFinished();

        if (!TrainingForegroundService.isRunning(context)) {
            TrainingForegroundService.startService(context, tree.getParent().getId(),
                    tree.getParent().getName());
        } else {
            ServiceBindController controller = new ServiceBindController(context);
            controller.bindService().observe(this, service -> {
                if (service != null && (service.isRestInProgress() || service.isRestInPause())) {
                    startRest((int)(service.getMillisecondsLeft()/1000));
                }
                controller.unbindService();
            });
        }

        initUi();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (exerciseList.getCurrentItemPosition() == RecyclerView.NO_POSITION) return;

        saveExerciseAndSetPosition();
    }

    private void saveExerciseAndSetPosition() {
        getSharedPreferences()
                .edit()
                .putLong(PREFS_ACTUAL_TRAINING_ID_KEY, tree.getParent().getId())
                .putInt(PREFS_EXERCISE_INDEX_KEY, exerciseList.getCurrentItemPosition())
                .putInt(PREFS_SET_INDEX_KEY, setsViewPager.getCurrentItem())
                .apply();
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_finish_training) {
            finishTraining();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void finishTraining() {
        alert.showTwoButtonsAlert(0, R.string.actual_training_activity_are_you_sure_message);
    }

    @Override
    public void onActualSetSave(int setIndex, ActualSetDataBindingWrapper actualSetWrapped) {
        final int currentExerciseIndex = exerciseList.getCurrentItemPosition();
        final ActualSet actualSet = actualSetWrapped.unwrap();
        if (GymmDatabase.isValidId(actualSet)) {
            viewModel.updateActualSet(currentExerciseIndex, actualSet);
        } else {
            viewModel.createActualSet(currentExerciseIndex, actualSet)
                    .observe(this, actualSetWrapped::setId);
            setsPagerAdapter.notifyDataSetChanged();
            startRestIfNeeded(currentExerciseIndex, setIndex);
            goNext();
        }
    }

    private void startRestIfNeeded(int currentExerciseIndex, int setIndex) {
        Integer secondsForRest = tree.getChildren().get(currentExerciseIndex)
                .getProgramExerciseNode().getChildren().get(setIndex).getSecondsForRest();
        startRest(secondsForRest);
    }

    private void startRest(Integer secondsForRest) {
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
        final boolean wentToNext = exerciseList.goToNext();
        if (!wentToNext) {
            finishTraining();
        }
    }

    private void initUi() {
        alert = new FragmentAlert(getChildFragmentManager(), this::onAlertButtonClick);

        initToolbar();
        initSetsViewPager();
        initExerciseList();
    }

    private int getSavedExercisePosition() {
        return getSharedPreferenceForCurrentTraining(PREFS_EXERCISE_INDEX_KEY);
    }

    private int getSavedSetPosition() {
        int position = getSharedPreferenceForCurrentTraining(PREFS_SET_INDEX_KEY);
        getSharedPreferences().edit().clear().apply();
        return position;
    }

    private int getSharedPreferenceForCurrentTraining(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        long actualTrainingId = sharedPreferences.getLong(PREFS_ACTUAL_TRAINING_ID_KEY, 0L);
        return actualTrainingId != 0L && actualTrainingId == tree.getParent().getId()
                ? sharedPreferences.getInt(key, -1) : -1;
    }

    private void initToolbar() {
        Toolbar toolbar = getView().findViewById(R.id.actualExercisesToolbar);
        toolbar.inflateMenu(R.menu.actual_training_menu);
        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);
        toolbar.setTitle(tree.getParent().getName());
    }

    private void initSetsViewPager() {
        setsViewPager = new ActualSetsViewPager(context);
        setsViewPager.setId(android.R.id.tabcontent);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        setsViewPager.setLayoutParams(layoutParams);

        setsPagerAdapter = new ActualSetsFragmentPagerAdapter(getChildFragmentManager());
        setsViewPager.setAdapter(setsPagerAdapter);
    }

    private void initExerciseList() {
        exerciseList = getView().findViewById(R.id.actualExerciseList);
        exerciseList.setOnExerciseActivatedListener(this::onExerciseActivated);
        exerciseList.setItemFloatingContainer(setsViewPager);
        ListItemListeners listeners = new ListItemListeners(R.layout.activity_actual_training_stepper_item)
                .setOnClickListener(exerciseList::activateItem);
        exerciseList.setAdapter(new ActualExercisesStepperAdapter(tree.getChildren(), listeners));
        exerciseList.setCurrentItemPosition(getSavedExercisePosition());
    }

    public void onAlertButtonClick(int dialogId, boolean positive) {
        if (dialogId == 0) {
            if (positive) {
                viewModel.finishTraining();
                context.stopService(new Intent(context, TrainingForegroundService.class));
            } else {
                alert.hideAlert();
            }
        }
    }

    public void onExerciseActivated(int position) {
        viewModel.createActualExercise(position);
        setsPagerAdapter.setActualExerciseNode(tree.getChildren().get(position));
        setsViewPager.post(() -> {
            int setPosition = getSavedSetPosition();
            setsViewPager.setCurrentItem(setPosition != -1 ? setPosition : 0, false);
        });
    }
}
