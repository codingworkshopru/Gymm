package ru.codingworkshop.gymm.ui.actual.exercise;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.PluralsRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Test;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.RecyclerViewItemMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static ru.codingworkshop.gymm.ui.actual.Matchers.currentPageItem;
import static ru.codingworkshop.gymm.ui.actual.Matchers.hasBackground;
import static ru.codingworkshop.gymm.ui.actual.TreeBuilders.buildHalfPopulatedTree;

/**
 * Created by Радик on 01.10.2017 as part of the Gymm project.
 */

public class ActualExercisesFragmentTest extends Base {

    @Override
    public void beforeFragmentSet() {
        setFakeTree(buildHalfPopulatedTree(3));

        doAnswer(a -> {
            Integer stepIndex = a.getArgument(0);

            ActualExerciseNode node = fakeTree.getChildren().get(stepIndex);
            ProgramExerciseNode programExerciseNode = node.getProgramExerciseNode();
            long programExerciseNodeId = programExerciseNode.getId();
            node.setParent(Models.createActualExercise(programExerciseNodeId + 10,
                    programExerciseNode.getExercise().getName(), 11L, programExerciseNodeId));

            return null;
        }).when(vm).createActualExercise(anyInt());

        doAnswer(invocation -> {
            int exerciseIndex = invocation.getArgument(0);
            ActualSet actualSet = invocation.getArgument(1);
            fakeTree.getChildren().get(exerciseIndex).addChild(actualSet);
            return null;
        }).when(vm).createActualSet(anyInt(), any(ActualSet.class));
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
    public void verifyVerticalLinesVisibility() throws Exception {
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

        selectStepAtPosition(1);
        assertCurrentPageIndex(1);
        onView(stepItem(R.id.stepperItemActualSetsContainer, 1)).perform(swipeLeft());
        assertCurrentPageIndex(2);

        selectStepAtPosition(2);
        assertCurrentPageIndex(1);
        onView(stepItem(R.id.stepperItemActualSetsContainer, 2)).perform(swipeLeft());
        assertCurrentPageIndex(1);
    }

    @Test
    public void verifyActiveStatusSet() throws Exception {
        selectStepAtPosition(1);

        assertItemAtPositionIsActive(1);
        assertItemAtPositionIsInactive(2);

        selectStepAtPosition(2);
        assertItemAtPositionIsInactive(1);
        assertItemAtPositionIsActive(2);
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
    public void finishSetWithoutRestTest() throws Exception {
        selectStepAtPosition(2);

        onView(currentPageItem(R.id.actualSetWeightEditText)).perform(click());
        typeWeight(1.1);
        clickDoneButton();

        verify(vm).createActualSet(eq(2), argThat(set -> set.getWeight() == 1.1));
        verify(callback, never()).onStartRest(anyInt());
    }

    @Test
    public void startRestTest() throws Exception {
        vm.getActualTrainingTree()
                .getChildren()
                .get(2)
                .getProgramExerciseNode()
                .getChildren()
                .get(0)
                .setSecondsForRest(1);

        selectStepAtPosition(2);
        typeWeight(1.1);
        Espresso.closeSoftKeyboard();
        clickDoneButton();

        verify(vm).createActualSet(eq(2), argThat(set -> set.getWeight() == 1.1));
        verify(callback).onStartRest(1);
    }

    @Test
    public void finishExerciseTest() throws Exception {
        onView(stepItem(R.id.stepperItemCircle, 0)).check(matches(hasBackground(R.drawable.ic_check_circle_primary_24dp)));
        selectStepAtPosition(1);
        onView(stepItem(R.id.stepperItemActualSetsContainer, 1)).perform(swipeLeft());

        typeWeight(1.1);
        clickDoneButton();

        onView(stepItem(R.id.stepperItemCircle, 1)).check(matches(hasBackground(R.drawable.ic_check_circle_primary_24dp)));
        assertItemAtPositionIsActive(2);
    }

    @Test
    public void goToNextSetAfterSetCreationTest() throws Exception {
        selectStepAtPosition(2);

        typeWeight(1.1);
        clickDoneButton();

        assertCurrentPageIndex(2);
    }

    @Test
    public void updateActualSet() throws Exception {
        selectStepAtPosition(0);

        replaceRepsCount(1);
        replaceWeight(0.1);

        Espresso.closeSoftKeyboard();

        clickDoneButton();
        assertCurrentPageIndex(1);

        verify(vm).updateActualSet(eq(0), argThat(s -> s.getReps() == 1 && s.getWeight() == 0.1));
    }

    @Test
    public void saveStateBeforeDetach() throws Exception {
        selectStepAtPosition(1);
        onView(stepItem(R.id.stepperItemActualSetsContainer, 1)).perform(swipeLeft());

        activityTestRule
                .getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .remove(fragment)
                .commit();

        Thread.sleep(100);

        activityTestRule
                .getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment)
                .commit();

        Thread.sleep(100);

        assertItemAtPositionIsActive(1);
        assertCurrentPageIndex(2);
    }

    private Matcher<View> stepItem(@LayoutRes int layoutId, int pos) {
        return RecyclerViewItemMatcher.itemAtPosition(layoutId, pos);
    }

    private void typeRepsCount(int repsCount) {
        type(R.id.actualSetRepsCountEditText, Integer.toString(repsCount));
    }

    private void typeWeight(double weight) {
        type(R.id.actualSetWeightEditText, Double.toString(weight));
    }

    private void type(@LayoutRes int id, String text) {
        onCurrentPageItem(id, typeText(text));
    }

    private void replaceRepsCount(int repsCount) {
        replace(R.id.actualSetRepsCountEditText, Integer.toString(repsCount));
    }

    private void replaceWeight(double weight) {
        replace(R.id.actualSetWeightEditText, Double.toString(weight));
    }

    private void replace(@LayoutRes int id, String text) {
        onCurrentPageItem(id, replaceText(text));
    }

    private void onCurrentPageItem(@LayoutRes int id, ViewAction... viewActions) {
        onView(currentPageItem(id)).perform(viewActions);
    }

    private void clickDoneButton() {
        onCurrentPageItem(R.id.actualSetDoneButton, click());
    }
    
    private void assertCurrentPageIndex(int inUiIndex) {
        String sets = getQuantityString(R.plurals.number_of_sets, inUiIndex);
        onView(currentPageItem(R.id.actualSetIndex)).check(matches(withText(sets)));
    }

    private void assertItemAtPositionIsActive(int position) {
        onView(stepItem(R.id.stepperItemCircle, position)).check(matches(hasBackground(R.drawable.ic_circle_primary_24dp)));
        onView(stepItem(R.id.stepperItemActualSetsContainer, position)).check(matches(isDisplayed()));
    }

    private void assertItemAtPositionIsInactive(int position) {
        onView(stepItem(R.id.stepperItemCircle, position)).check(matches(hasBackground(R.drawable.ic_circle_grey_24dp)));
        onView(stepItem(R.id.stepperItemActualSetsContainer, position)).check(matches(not(isDisplayed())));
    }

    @NonNull
    private String getQuantityString(@PluralsRes int pluralId, Integer i) {
        return InstrumentationRegistry.getTargetContext().getResources().getQuantityString(pluralId, i, i);
    }
}