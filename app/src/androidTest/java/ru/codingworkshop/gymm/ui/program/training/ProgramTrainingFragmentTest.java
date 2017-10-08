package ru.codingworkshop.gymm.ui.program.training;

import android.arch.lifecycle.ViewModelProvider;
import android.graphics.Rect;
import android.support.annotation.LayoutRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;
import ru.codingworkshop.gymm.util.RecyclerViewItemMatcher;
import ru.codingworkshop.gymm.util.TreeBuilders;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.GeneralLocation.CENTER;
import static android.support.test.espresso.action.ViewActions.actionWithAssertions;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 07.10.2017 as part of the Gymm project.
 */

public class ProgramTrainingFragmentTest {
    @Mock private ViewModelProvider.Factory viewModelFactory;
    @Mock private ProgramTrainingViewModel vm;
    @InjectMocks private ProgramTrainingFragment fragment;

    @Rule
    public ActivityTestRule<SimpleFragmentActivity> activityTestRule =
            new ActivityTestRule<>(SimpleFragmentActivity.class);
    private ProgramTrainingTree tree;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        RecyclerViewItemMatcher.setRecyclerViewId(R.id.programExerciseList);
        when(viewModelFactory.create(any())).thenReturn(vm);
        tree = TreeBuilders.buildProgramTrainingTree(3);
        when(vm.getProgramTrainingTree()).thenReturn(tree);
        activityTestRule.getActivity().setFragment(fragment);
    }

    @Test
    public void trainingListTest() throws Exception {
        onView(exerciseAt(R.id.programExerciseReorderImage, 0)).check(matches(not(isDisplayed())));
        onView(exerciseAt(R.id.programExerciseName, 0)).check(matches(withText("exercise100")));
        String setsCount = InstrumentationRegistry.getTargetContext().getResources().getQuantityString(R.plurals.number_of_sets, 2, 2);
        onView(exerciseAt(R.id.programSetsCount, 0)).check(matches(withText(setsCount)));
        removeAt(2, LEFT);
        assertEquals(3, tree.getChildren().size());
    }

    @Test
    public void removeExercise() throws Exception {
        enterActionMode();
        removeAt(2, RIGHT);
        assertEquals(2, tree.getChildren().size());

        removeAt(1, LEFT);
        assertEquals(1, tree.getChildren().size());
    }

    @Test
    public void restoreRemovedExercise() throws Exception {
        enterActionMode();
        removeAt(2, LEFT);
        onView(withText(InstrumentationRegistry.getTargetContext().getString(R.string.program_training_activity_exercise_deleted_message))).check(matches(isDisplayed()));
        assertEquals(2, tree.getChildren().size());
        onView(withText(InstrumentationRegistry.getTargetContext().getString(android.R.string.cancel))).perform(click());
        assertEquals(3, tree.getChildren().size());
        onView(exerciseAt(R.id.programExerciseName, 2)).check(matches(withText("exercise102")));
    }

    @Test
    public void moveExercise() throws Exception {
        ProgramExerciseNode first = tree.getChildren().get(0);
        ProgramExerciseNode second = tree.getChildren().get(1);
        final CoordinatesProvider cp = view -> {
            float[] coordinates = new float[2];
            Rect globalRect = new Rect();
            view.getGlobalVisibleRect(globalRect);
            coordinates[0] = globalRect.centerX();
            coordinates[1] = globalRect.bottom + 2*globalRect.height();
            return coordinates;
        };
        enterActionMode();
        final ViewAction viewAction = actionWithAssertions(new GeneralSwipeAction(Swipe.FAST,
                CENTER, cp, Press.THUMB));
        onView(exerciseAt(R.id.programExerciseReorderImage, 0)).perform(viewAction);
        onView(exerciseAt(R.id.programExerciseName, 0)).check(matches(withText("exercise101")));
        assertEquals(first, tree.getChildren().get(1));
        assertEquals(second, tree.getChildren().get(0));
    }

    @Test
    public void backgroundImageTest() throws Exception {
        onView(withId(R.id.programTrainingBackground)).check(matches(not(isDisplayed())));
        enterActionMode();
        removeExercise();
        removeAt(0, RIGHT);
        onView(withId(R.id.programTrainingBackground)).check(matches(isDisplayed()));
        onView(withText(InstrumentationRegistry.getTargetContext().getString(android.R.string.cancel))).perform(click());
        onView(withId(R.id.programTrainingBackground)).check(matches(not(isDisplayed())));
    }

    @Test
    public void actionModeTest() throws Exception {
        checkActionModeOff();
        enterActionMode();
        checkActionModeOn();
        Espresso.pressBack();
        checkActionModeOff();
    }

    private void checkActionModeOn() {
        for (int i = 0; i < 3; i++) {
            onView(exerciseAt(R.id.programExerciseReorderImage, i)).check(matches(isDisplayed()));
        }
    }

    private void checkActionModeOff() {
        for (int i = 0; i < 3; i++) {
            onView(exerciseAt(R.id.programExerciseReorderImage, i)).check(matches(not(isDisplayed())));
        }
        removeAt(0, LEFT);
        assertEquals(3, tree.getChildren().size());
    }

    private void enterActionMode() {
        onView(exerciseAt(R.id.programExerciseName, 0)).perform(longClick());
    }

    private void removeAt(int position, int direction) {
        onView(exerciseAt(R.id.programExerciseName, position)).perform(direction == LEFT ? swipeLeft() : swipeRight());
    }

    private Matcher<View> exerciseAt(@LayoutRes int id, int index) {
        return RecyclerViewItemMatcher.itemAtPosition(id, index);
    }
}
