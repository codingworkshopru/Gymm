package ru.codingworkshop.gymm.ui.program.training;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
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
import android.widget.ImageButton;

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
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;
import ru.codingworkshop.gymm.util.RecyclerViewItemMatcher;
import ru.codingworkshop.gymm.util.TreeBuilders;
import ru.codingworkshop.gymm.ui.program.ProgramTrainingViewModel;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.GeneralLocation.CENTER;
import static android.support.test.espresso.action.ViewActions.actionWithAssertions;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.codingworkshop.gymm.ui.Matchers.hasErrorText;

/**
 * Created by Радик on 07.10.2017 as part of the Gymm project.
 */

public class ProgramTrainingFragmentTest {
    @Mock private ViewModelProvider.Factory viewModelFactory;
    @Mock private ProgramTrainingViewModel vm;
    @InjectMocks private ProgramTrainingFragment fragment;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

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
    public void initializationTest() throws Exception {
        onView(withId(R.id.programTrainingName)).check(matches(withText("foo")));
        onView(withId(R.id.actionSaveTraining)).check(matches(isDisplayed()));
    }

    @Test
    public void exercisesListTest() throws Exception {
        onView(exerciseAt(R.id.programExerciseName, 0)).check(matches(withText("exercise100")));
        String setsCount = InstrumentationRegistry.getTargetContext().getResources().getQuantityString(R.plurals.number_of_sets, 2, 2);
        onView(exerciseAt(R.id.programExerciseSetsCount, 0)).check(matches(withText(setsCount)));
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
        onView(withText(R.string.program_training_activity_exercise_deleted_message)).check(matches(isDisplayed()));
        assertEquals(2, tree.getChildren().size());
        onView(withText(android.R.string.cancel)).perform(click());
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
        removeAll();
        onView(withId(R.id.programTrainingBackground)).check(matches(isDisplayed()));
        onView(withText(android.R.string.cancel)).perform(click());
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

    @Test
    public void saveWithEmptyNameTest() throws Exception {
        onView(withId(R.id.programTrainingName)).perform(clearText());
        onView(withId(R.id.actionSaveTraining)).perform(click());
        onView(withId(R.id.programTrainingNameLayout)).check(matches(hasErrorText(R.string.program_training_activity_name_empty_error)));
        verify(vm, never()).saveTree();
    }

    @Test
    public void typeTextWithErrorShownTest() throws Exception {
        saveWithEmptyNameTest();

        onView(withId(R.id.programTrainingName)).perform(typeText("foo"));
        onView(withId(R.id.programTrainingNameLayout)).check(matches(not(hasErrorText())));
    }

    @Test
    public void saveDuplicateTest() throws Exception {
        when(vm.validateProgramTraining()).thenReturn(LiveDataUtil.getLive(false));
        onView(withId(R.id.actionSaveTraining)).perform(click());
        onView(withId(R.id.programTrainingNameLayout)).check(matches(hasErrorText(R.string.program_training_activity_name_duplicate_error)));
        verify(vm, never()).saveTree();
    }

    @Test
    public void saveWithoutExercisesTest() throws Exception {
        when(vm.validateProgramTraining()).thenReturn(LiveDataUtil.getLive(false));
        enterActionMode();
        removeAll();
        Thread.sleep(1000);
        onView(withId(R.id.actionSaveTraining)).perform(click());
        onView(withText(R.string.program_training_activity_empty_list_dialog_message)).check(matches(isDisplayed()));
        verify(vm, never()).saveTree();
    }

    @Test
    public void saveTest() throws Exception {
        when(vm.validateProgramTraining()).thenReturn(LiveDataUtil.getLive(true));
        onView(withId(R.id.actionSaveTraining)).perform(click());
        verify(vm).saveTree();
    }

    @Test
    public void programTrainingChangedAssuranceMessageTest() throws Exception {
        when(vm.isProgramTrainingChanged()).thenReturn(LiveDataUtil.getLive(true));

        onView(both(isAssignableFrom(ImageButton.class)).and(withParent(withId(R.id.programTrainingToolbar)))).perform(click());
        onView(withText(R.string.cancel_changes_question)).check(matches(isDisplayed()));
        onView(withText(android.R.string.ok)).perform(click());

        verify(vm).isProgramTrainingChanged();
        verify(vm, never()).saveTree();
    }

    private void checkActionModeOn() {
        for (int i = 0; i < 3; i++) {
            onView(exerciseAt(R.id.programExerciseReorderImage, i)).check(matches(isDisplayed()));
        }
        onView(withId(R.id.programTrainingNameLayout)).check(matches(not(isDisplayed())));
    }

    private void checkActionModeOff() {
        for (int i = 0; i < 3; i++) {
            onView(exerciseAt(R.id.programExerciseReorderImage, i)).check(matches(not(isDisplayed())));
        }
        onView(withId(R.id.programTrainingNameLayout)).check(matches(isDisplayed()));
    }

    private void enterActionMode() {
        onView(exerciseAt(R.id.programExerciseName, 0)).perform(longClick());
    }

    private void removeAt(int position, int direction) {
        onView(exerciseAt(R.id.programExerciseName, position)).perform(direction == LEFT ? swipeLeft() : swipeRight());
    }

    private void removeAll() {
        enterActionMode();
        for (int i = 2; i >= 0; i--) {
            removeAt(i, LEFT);
        }
        Espresso.pressBack();
    }

    private Matcher<View> exerciseAt(@LayoutRes int id, int index) {
        return RecyclerViewItemMatcher.itemAtPosition(id, index);
    }
}
