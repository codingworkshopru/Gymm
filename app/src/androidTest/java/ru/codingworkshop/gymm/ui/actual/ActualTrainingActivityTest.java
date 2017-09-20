package ru.codingworkshop.gymm.ui.actual;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.PluralsRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.core.internal.deps.guava.base.Preconditions;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.google.common.collect.Lists;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.loader.builder.ActualTrainingTreeBuilder;
import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.service.TrainingTimeService;
import ru.codingworkshop.gymm.testing.ActualTrainingActivityIsolated;
import ru.codingworkshop.gymm.ui.actual.viewmodel.ActualTrainingViewModel;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.RecyclerViewItemMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.codingworkshop.gymm.ui.actual.Matchers.currentPageItem;
import static ru.codingworkshop.gymm.ui.actual.Matchers.hasBackground;

/**
 * Created by Радик on 19.09.2017 as part of the Gymm project.
 */

public class ActualTrainingActivityTest {
    @Rule
    public ActivityTestRule<ActualTrainingActivityIsolated> activityTestRule =
            new ActivityTestRule<>(ActualTrainingActivityIsolated.class, true, false);

    private ActualTrainingViewModel vm;

    @Before
    public void setUp() throws Exception {
        RecyclerViewItemMatcher.setRecyclerViewId(R.id.actualExerciseSteps);

        vm = mock(ActualTrainingViewModel.class);
        when(vm.getActualTrainingTree()).thenReturn(buildHalfPopulatedTree(3));

        ViewModelProvider.Factory viewModelFactory = mock(ViewModelProvider.Factory.class);
        when(viewModelFactory.create(any())).thenReturn(vm);
        ActualTrainingActivityIsolated.viewModelFactoryMock = viewModelFactory;

        doAnswer(a -> {
            Integer stepIndex = a.getArgument(0);

            ActualExerciseNode node = vm.getActualTrainingTree().getChildren().get(stepIndex);
            ProgramExerciseNode programExerciseNode = node.getProgramExerciseNode();
            long programExerciseNodeId = programExerciseNode.getId();
            node.setParent(Models.createActualExercise(programExerciseNodeId + 10,
                    programExerciseNode.getExercise().getName(), 11L, programExerciseNodeId));

            return null;
        }).when(vm).createActualExercise(any(Integer.class));

        activityTestRule.launchActivity(null);
    }

    @Test
    public void startTrainingTest() throws Exception {
        doAnswer(invocation -> {
            final ActualTrainingTree tree = buildTreeWithoutActuals(1);
            when(vm.getActualTrainingTree()).thenReturn(tree);
            tree.setParent(Models.createActualTraining(11L, invocation.getArgument(0)));
            return new LiveData<Boolean>() {{postValue(true);}};
        }).when(vm).startTraining(1L);
        reconfigure(ActualTrainingActivity.EXTRA_PROGRAM_TRAINING_ID, 1L);

        onView(withId(R.id.actualTrainingToolbar)).check(matches(withChild(withText("foo"))));
        onView(withText("exercise100")).check(matches(isDisplayed()));

        assertTrue(TrainingTimeService.isRunning(InstrumentationRegistry.getTargetContext()));

        verify(vm).startTraining(1L);
        verify(vm, atLeastOnce()).getActualTrainingTree();
    }

    @Test
    public void resumeTrainingTest() throws Exception {
        doAnswer(invocation -> {
            when(vm.getActualTrainingTree()).thenReturn(buildHalfPopulatedTree(2));
            return new LiveData<Boolean>() {{postValue(true);}};
        }).when(vm).loadTraining(11L);
        reconfigure(ActualTrainingActivity.EXTRA_ACTUAL_TRAINING_ID, 11L);

        selectStepAtPosition(0);

        checkRepsCount(3);
        checkWeight(3.5);

        verify(vm).loadTraining(11L);
        verify(vm, atLeastOnce()).getActualTrainingTree();
    }

    private void reconfigure(String extraKey, long extraValue) {
        activityTestRule.finishActivity();

        when(vm.getActualTrainingTree()).thenReturn(null);

        Intent intent = new Intent(InstrumentationRegistry.getTargetContext(), ActualTrainingActivityIsolated.class);
        intent.putExtra(extraKey, extraValue);
        activityTestRule.launchActivity(intent);
    }

    @Test
    public void finishTrainingTest() throws Exception {
        onView(withId(R.id.action_finish_training)).perform(click());

        final String areYouSureMessage = InstrumentationRegistry.getTargetContext()
                .getString(R.string.actual_training_activity_are_you_sure_message);
        onView(withText(areYouSureMessage)).check(matches(isDisplayed()));
        onView(withText(android.R.string.ok)).perform(click());

        verify(vm).finishTraining();
    }

    @Test
    public void verifyItemCircle() throws Exception {
        onView(stepItem(R.id.stepperItemIndex, 0)).check(matches(withText("1")));
        onView(stepItem(R.id.stepperItemCircle, 1)).check(matches(isDisplayed()));
        onView(stepItem(R.id.stepperItemCircle, 1)).check(matches(hasBackground(R.drawable.ic_circle_grey_24dp)));
    }

    @Test
    public void verifyVerticalVisibility() throws Exception {
        onView(stepItem(R.id.stepperItemTopLine, 0)).check(matches(not(isDisplayed())));
        onView(stepItem(R.id.stepperItemBottomLine, 0)).check(matches(isDisplayed()));
        onView(stepItem(R.id.stepperItemTopLine, 2)).check(matches(isDisplayed()));
        onView(stepItem(R.id.stepperItemBottomLine, 2)).check(matches(not(isDisplayed())));
    }

    @Test
    public void actualSetsSlideTest() throws Exception {
        selectStepAtPosition(0);
        assertCurrentPageIndex(1);
        onView(stepItem(R.id.stepperItemActualSetsContainer, 0)).perform(swipeLeft());
        assertCurrentPageIndex(2);
    }

    @Test
    public void verifyActiveStatusSet() throws Exception {
        selectStepAtPosition(1);

        assertItemAtPositionIsActive(1);
        assertItemAtPositionIsInactive(2);
    }

    @Test
    public void actualExerciseCreationOnStepClickTest() throws Exception {
        selectStepAtPosition(0);
        verify(vm).createActualExercise(0);
    }

    @Test
    public void testPageFillsFromModel() throws Exception {
        selectStepAtPosition(0);
        final ActualSet veryFirstActualSet = vm.getActualTrainingTree().getChildren().get(0).getChildren().get(0);
        checkWeight(veryFirstActualSet.getWeight());
        checkRepsCount(veryFirstActualSet.getReps());
    }

    @Test
    public void finishExerciseTest() throws Exception {
        onView(stepItem(R.id.stepperItemCircle, 0)).check(matches(hasBackground(R.drawable.ic_check_circle_primary_24dp)));
        selectStepAtPosition(1);
        onView(stepItem(R.id.stepperItemActualSetsContainer, 1)).perform(swipeLeft());

        doAnswer(invocation -> {
            int exerciseIndex = invocation.getArgument(0);
            ActualSet actualSet = invocation.getArgument(1);
            vm.getActualTrainingTree().getChildren().get(exerciseIndex).addChild(actualSet);
            return null;
        }).when(vm).createActualSet(any(Integer.class), any(ActualSet.class));

        typeWeight(1.1, false);
        clickDoneButton();

        onView(stepItem(R.id.stepperItemCircle, 1)).check(matches(hasBackground(R.drawable.ic_check_circle_primary_24dp)));
        assertItemAtPositionIsActive(2);
    }

    @Test
    public void goToNextSetAfterCurrentSetCreationTest() throws Exception {
        selectStepAtPosition(2);

        typeWeight(1.1, false);
        clickDoneButton();

        assertCurrentPageIndex(2);
    }

    @Test
    public void updateActualSet() throws Exception {
        selectStepAtPosition(0);

        typeRepsCount(1, true);
        typeWeight(0.1, true);

        clickDoneButton();
        assertCurrentPageIndex(1);

        verify(vm).updateActualSet(eq(0), argThat(s -> s.getReps() == 1 && s.getWeight() == 0.1));
    }

    private void assertItemAtPositionIsActive(int position) {
        onView(stepItem(R.id.stepperItemCircle, position)).check(matches(hasBackground(R.drawable.ic_circle_primary_24dp)));
        onView(stepItem(R.id.stepperItemActualSetsContainer, position)).check(matches(isDisplayed()));
    }

    private void assertItemAtPositionIsInactive(int position) {
        onView(stepItem(R.id.stepperItemCircle, position)).check(matches(hasBackground(R.drawable.ic_circle_grey_24dp)));
        onView(stepItem(R.id.stepperItemActualSetsContainer, position)).check(matches(not(isDisplayed())));
    }

    private Matcher<View> stepItem(@LayoutRes int layoutId, int pos) {
        return RecyclerViewItemMatcher.itemAtPosition(layoutId, pos);
    }

    private ProgramTrainingTree buildProgramTrainingTree(int exercisesCount) {
        ProgramTrainingTree tree = new ImmutableProgramTrainingTree();
        tree.setParent(Models.createProgramTraining(1L, "foo"));
        tree.setChildren(Lists.transform(Models.createProgramExercises(exercisesCount), pe -> {
            ProgramExerciseNode node = new ImmutableProgramExerciseNode(pe);

            long exerciseId = pe.getExerciseId();
            node.setExercise(Models.createExercise(exerciseId, "exercise" + exerciseId));

            node.setChildren(Models.createProgramSets(pe.getId(), 2));

            return node;
        }));

        return tree;
    }

    protected ActualTrainingTree buildTreeWithoutActuals(int exercisesCount) {
        ActualTrainingTreeBuilder builder = new ActualTrainingTreeBuilder(new ActualTrainingTree());

        builder.setProgramTrainingTree(buildProgramTrainingTree(exercisesCount));

        return (ActualTrainingTree) builder.build();
    }

    protected ActualTrainingTree buildFullPopulatedTree(int exercisesCount) {
        ActualTrainingTreeBuilder builder = new ActualTrainingTreeBuilder(new ActualTrainingTree());

        ProgramTrainingTree programTrainingTree = buildProgramTrainingTree(exercisesCount);
        builder.setProgramTrainingTree(programTrainingTree);

        ActualTraining actualTraining = Models.createActualTraining(11L, 1L);
        builder.setParent(actualTraining);
        List<ActualSet> actualSets = new ArrayList<>();
        AtomicLong i = new AtomicLong(13L);
        builder.setChildren(new ArrayList<>(Lists.transform(
                programTrainingTree.getChildren(),
                pe -> {
                    ActualExercise actualExercise = Models.createActualExercise(
                            pe.getId() + 10, pe.getExercise().getName(), actualTraining.getId(), pe.getId());
                    for (ProgramSet s : pe.getChildren()) {
                        ActualSet actualSet = Models.createActualSet(i.getAndIncrement(), actualExercise.getId(), s.getReps());
                        actualSet.setWeight(s.getReps() + 0.5);
                        actualSets.add(actualSet);
                    }
                    return actualExercise;
                })));
        builder.setGrandchildren(actualSets);

        return (ActualTrainingTree) builder.build();
    }

    protected ActualTrainingTree buildHalfPopulatedTree(int exercisesCount) {
        Preconditions.checkArgument(exercisesCount > 1, "Exercises count must be more than 1");
        // since the method is for testing purposes
        // a full tree taken and removed last actual sets
        ActualTrainingTree tree = buildFullPopulatedTree(exercisesCount);

        List<ActualExerciseNode> exercises = tree.getChildren();
        int half = exercisesCount >> 1;
        List<ActualSet> children = exercises.get(half).getChildren();
        children.removeAll(children.subList(children.size() / 2, children.size()));

        for (int i = half + 1; i < exercisesCount; i++) {
            exercises.get(i).getChildren().clear();
        }

        return tree;
    }

    protected ActualTrainingTree buildTreeForStart(int exercisesCount) {
        ActualTrainingTree tree = new ActualTrainingTree();
        tree.setProgramTraining(Models.createProgramTraining(1L, "foo"));

        tree.setChildren(Lists.transform(Models.createProgramExercises(exercisesCount), pe -> {
            ProgramExerciseNode peNode = new ImmutableProgramExerciseNode(pe);

            peNode.setChildren(Models.createProgramSets(pe.getId(), 2));

            final long exerciseId = peNode.getExerciseId();
            Exercise exercise = Models.createExercise(exerciseId, "exercise" + exerciseId);
            exercise.setWithWeight(true);
            peNode.setExercise(exercise);

            ActualExerciseNode node = new ActualExerciseNode();
            node.setProgramExerciseNode(peNode);
            return node;
        }));

        return tree;
    }

    protected void clickDoneButton() {
        onView(currentPageItem(R.id.actualSetDoneButton)).perform(click());
    }

    protected void selectStepAtPosition(int position) {
        onView(withId(R.id.actualExerciseSteps)).perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));
    }

    protected void typeRepsCount(int repsCount, boolean clear) {
        type(R.id.actualSetRepsCountEditText, Integer.toString(repsCount), clear);
    }

    protected void typeWeight(double weight, boolean clear) {
        type(R.id.actualSetWeightEditText, Double.toString(weight), clear);
    }

    protected void type(@LayoutRes int id, String text, boolean clear) {
        if (clear) {
            onView(currentPageItem(id)).perform(clearText());
        }
        onView(currentPageItem(id)).perform(typeText(text));
    }

    protected void checkWeight(double weight) {
        onView(currentPageItem(R.id.actualSetWeightEditText)).check(matches(withText(Double.toString(weight))));
    }

    protected void checkRepsCount(int repsCount) {
        onView(currentPageItem(R.id.actualSetRepsCountEditText)).check(matches(withText(Integer.toString(repsCount))));
    }

    protected void assertCurrentPageIndex(int inUiIndex) {
        String sets = getQuantityString(R.plurals.number_of_sets, inUiIndex);
        onView(currentPageItem(R.id.actualSetIndex)).check(matches(withText(sets)));
    }

    @NonNull
    protected String getQuantityString(@PluralsRes int pluralId, Integer i) {
        return InstrumentationRegistry.getTargetContext().getResources().getQuantityString(pluralId, i, i);
    }
}
