package ru.codingworkshop.gymm.ui.actual;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.PluralsRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.google.common.collect.Lists;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.service.TrainingTimeService;
import ru.codingworkshop.gymm.testing.ActualTrainingActivityIsolated;
import ru.codingworkshop.gymm.ui.actual.viewmodel.ActualTrainingViewModel;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.RecyclerViewItemMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.codingworkshop.gymm.ui.actual.Matchers.currentPageItem;
import static ru.codingworkshop.gymm.ui.actual.Matchers.hasBackground;

/**
 * Created by Радик on 28.08.2017 as part of the Gymm project.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ActualTrainingActivityStartTrainingTest extends ActualTrainingActivityTest {

    @Rule
    public ActivityTestRule<ActualTrainingActivityIsolated> activityRule = new ActivityTestRule<ActualTrainingActivityIsolated>(ActualTrainingActivityIsolated.class) {
        @Override
        protected void beforeActivityLaunched() {
            vm = mock(ActualTrainingViewModel.class);
            ViewModelProvider.Factory viewModelFactory = mock(ViewModelProvider.Factory.class);
            ActualTrainingActivityIsolated.viewModelFactoryMock = viewModelFactory;
            when(viewModelFactory.create(any())).thenReturn(vm);
            when(vm.startTraining(1L)).thenReturn(liveTrue);
        }

        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent(InstrumentationRegistry.getTargetContext(), ActualTrainingActivityIsolated.class);
            intent.putExtra(ActualTrainingActivity.EXTRA_PROGRAM_TRAINING_ID, 1L);
            return intent;
        }
    };

    private ActualTrainingViewModel vm;

    private MutableLiveData<Boolean> liveTrue = new MutableLiveData<>();

    private ActualTrainingTree tree;

    private void buildTree(int size) {
        tree.setProgramTraining(Models.createProgramTraining(1L, "foo"));

        tree.setChildren(Lists.transform(Models.createProgramExercises(size), pe -> {
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

        liveTrue.postValue(true);
    }

    @Before
    public void setUp() throws Exception {
        tree = new ActualTrainingTree();
        when(vm.getActualTrainingTree()).thenReturn(tree);
        RecyclerViewItemMatcher.setRecyclerViewId(R.id.actualExerciseSteps);

        doAnswer(a -> {
            Integer i = a.getArgument(0);
            ActualExerciseNode node = tree.getChildren().get(i);
            final ProgramExerciseNode programExerciseNode = node.getProgramExerciseNode();
            ActualExercise actualExercise = new ActualExercise(programExerciseNode.getExercise().getName(), 11L, programExerciseNode.getId());
            node.setParent(actualExercise);
            return null;
        }).when(vm).createActualExercise(any(Integer.class));
    }

    @Test
    public void startTrainingTest() throws Exception {
        buildTree(1);

        verify(vm).startTraining(1L);

        onView(withId(R.id.actualTrainingToolbar)).check(matches(hasDescendant(withText("foo"))));
        onView(withText("exercise100")).check(matches(isDisplayed()));

        assertTrue(TrainingTimeService.isRunning(InstrumentationRegistry.getTargetContext()));

        verify(vm).getActualTrainingTree();
    }

    @Test
    public void verifyItemCircle() throws Exception {
        buildTree(1);
        onView(withText("1")).check(matches(isDisplayed()));
        onView(withId(R.id.stepperItemCircle)).check(matches(isDisplayed()));
        onView(withId(R.id.stepperItemCircle)).check(matches(hasBackground(R.drawable.ic_circle_grey_24dp)));
    }

    @Test
    public void verifyVerticalLinesOnMultipleItems() throws Exception {
        buildTree(2);
        onView(iap(R.id.stepperItemTopLine, 0)).check(matches(not(isDisplayed())));
        onView(iap(R.id.stepperItemBottomLine, 0)).check(matches(isDisplayed()));
        onView(iap(R.id.stepperItemTopLine, 1)).check(matches(isDisplayed()));
        onView(iap(R.id.stepperItemBottomLine, 1)).check(matches(not(isDisplayed())));
    }

    @Test
    public void actualSetsSlideTest() throws Exception {
        final int exercisesCount = 2;
        buildTree(exercisesCount);

        for (int exerciseIndex = 0; exerciseIndex < exercisesCount; exerciseIndex++) {
            selectStepAtPosition(exerciseIndex);
            assertCurrentPageIndex(1);
            for (int setIndex = 2; setIndex < 3; setIndex++) {
                onView(RecyclerViewItemMatcher.itemAtPosition(R.id.stepperItemActualSetsContainer, exerciseIndex)).perform(swipeLeft());
                assertCurrentPageIndex(setIndex);
            }
        }
    }

    @NonNull
    private String getQuantityString(@PluralsRes int pluralId, Integer i) {
        return InstrumentationRegistry.getTargetContext().getResources().getQuantityString(pluralId, i, i);
    }

    @Test
    public void testKeepingSavedData() throws Exception {
        buildTree(2);

        doAnswer(invocation -> {
            Integer index = invocation.getArgument(0);
            ActualSet set = invocation.getArgument(1);
            tree.getChildren().get(index).addChild(set);
            return null;
        }).when(vm).createActualSet(any(Integer.class), any(ActualSet.class));

        selectStepAtPosition(0);

        typeRepsCount(1, true);
        typeWeight(1.1, true);

        clickDoneButton();

        selectStepAtPosition(1);
        selectStepAtPosition(0);

        checkRepsCount(1);
        checkWeight(1.1);
    }

    @Test
    public void finishActualSetGoToNextExerciseTest() throws Exception {
        final int exercises = 2;
        buildTree(exercises);

        selectStepAtPosition(0);

        typeWeight(5.125, false);
        clickDoneButton();
        verify(vm).createActualSet(eq(0), argThat(s -> s.getReps() == 3 && s.getWeight() == 5.125));

        assertCurrentPageIndex(2);

        typeWeight(6.5, false);
        clickDoneButton();
        verify(vm).createActualSet(eq(0), argThat(s -> s.getReps() == 4 && s.getWeight() == 6.5));

        typeWeight(1.1, false);
        Espresso.closeSoftKeyboard();
        clickDoneButton();
        verify(vm).createActualSet(eq(1), argThat(s -> s.getReps() == 3 && s.getWeight() == 1.1));
    }

    @Test
    public void updateActualSets() throws Exception {
        buildTree(2);
        final List<ActualSet> actualSets = Models.createActualSets(12L, 13L, 14L);
        actualSets.forEach(s -> s.setWeight(0.5));
        tree.getChildren().get(0).setChildren(actualSets);

        selectStepAtPosition(0);
        typeRepsCount(15, true);
        typeWeight(12.5, true);
        clickDoneButton();

        assertCurrentPageIndex(1);

        verify(vm).updateActualSet(eq(0), argThat(
                s -> s.getId() == 13L
                        && s.getReps() == 15
                        && s.getWeight() == 12.5));
    }

    @Test
    public void verifyActiveStatusSet() throws Exception {
        buildTree(2);

        selectStepAtPosition(0);

        assertItemAtPositionIsActive(0);
        assertItemAtPositionIsInactive(1);

        selectStepAtPosition(1);

        assertItemAtPositionIsActive(1);
        assertItemAtPositionIsInactive(0);

        verify(vm).createActualExercise(0);
        verify(vm).createActualExercise(1);
    }

    private void assertCurrentPageIndex(int index) {
        String sets = getQuantityString(R.plurals.number_of_sets, index);
        onView(currentPageItem(R.id.actualSetIndex)).check(matches(withText(sets)));
    }

    private void typeRepsCount(int repsCount, boolean clear) {
        type(R.id.actualSetRepsCountEditText, Integer.toString(repsCount), clear);
    }

    private void typeWeight(double weight, boolean clear) {
        type(R.id.actualSetWeightEditText, Double.toString(weight), clear);
    }

    private void type(@LayoutRes int id, String text, boolean clear) {
        if (clear) {
            onView(currentPageItem(id)).perform(clearText());
        }
        onView(currentPageItem(id)).perform(typeText(text));
    }

    private void assertItemAtPositionIsActive(int position) {
        onView(iap(R.id.stepperItemCircle, position)).check(matches(hasBackground(R.drawable.ic_circle_primary_24dp)));
        onView(iap(R.id.stepperItemActualSetsContainer, position)).check(matches(isDisplayed()));
    }

    private void assertItemAtPositionIsInactive(int position) {
        onView(iap(R.id.stepperItemCircle, position)).check(matches(hasBackground(R.drawable.ic_circle_grey_24dp)));
        onView(iap(R.id.stepperItemActualSetsContainer, position)).check(matches(not(isDisplayed())));
    }

    private Matcher<View> iap(int id, int pos) {
        return RecyclerViewItemMatcher.itemAtPosition(id, pos);
    }
}
