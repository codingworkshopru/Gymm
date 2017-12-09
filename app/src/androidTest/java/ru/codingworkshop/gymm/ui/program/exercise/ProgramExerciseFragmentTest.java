package ru.codingworkshop.gymm.ui.program.exercise;

import android.app.Instrumentation;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.LayoutRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.ResettingStubber;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.view.View;
import android.widget.ImageButton;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;
import ru.codingworkshop.gymm.ui.program.ProgramTrainingViewModel;
import ru.codingworkshop.gymm.ui.program.exercise.picker.ExercisePickerActivity;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.RecyclerViewItemMatcher;
import ru.codingworkshop.gymm.util.TreeBuilders;

import static android.app.Activity.RESULT_OK;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.GeneralLocation.CENTER;
import static android.support.test.espresso.action.ViewActions.actionWithAssertions;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.init;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.codingworkshop.gymm.ui.program.exercise.ProgramExerciseFragment.EXERCISE_ID_KEY;
import static ru.codingworkshop.gymm.ui.program.exercise.ProgramExerciseFragment.EXERCISE_NAME_KEY;

/**
 * Created by Радик on 07.10.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramExerciseFragmentTest {
    @Mock private ViewModelProvider.Factory viewModelFactory;
    @Mock private ProgramTrainingViewModel vm;
    @InjectMocks private ProgramExerciseFragment fragment;

    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Rule public IntentsTestRule<SimpleFragmentActivity> activityTestRule =
            new IntentsTestRule<>(SimpleFragmentActivity.class);

    private ProgramExerciseNode node;
    private Instrumentation.ActivityResult activityResult;

    @Before
    public void setUp() throws Exception {
        Exercise e = Models.createExercise(101L, "exercise101");
        Intent intent = new Intent();
        intent.putExtra(EXERCISE_ID_KEY, e.getId());
        intent.putExtra(EXERCISE_NAME_KEY, e.getName());
        activityResult = new Instrumentation.ActivityResult(RESULT_OK, intent);

        RecyclerViewItemMatcher.setRecyclerViewId(R.id.programSetList);

        when(viewModelFactory.create(any())).thenReturn(vm);
        node = TreeBuilders.buildProgramTrainingTree(3, 3).getChildren().get(0);
        node.getChildren().get(0).setSecondsForRest(145);
        when(vm.getProgramExercise()).thenReturn(LiveDataUtil.getLive(node));

        activityTestRule.getActivity().setFragment(fragment);
    }

    @Test
    public void initializationTest() throws Exception {
        onView(withId(R.id.programExerciseName)).check(matches(withText("exercise100")));
        onView(withId(R.id.actionSaveExercise)).check(matches(isDisplayed()));
    }

    @Test
    public void setsListTest() throws Exception {
        checkListItem(0, 3, 145);
        checkListItem(1, 4, 0);
    }

    private void checkListItem(int position, int repsCount, int seconds) {
        Resources resources = InstrumentationRegistry.getTargetContext().getResources();
        String prefix = resources.getString(R.string.program_exercise_activity_dialog_rest_time_label);
        String repsCountStr = resources.getQuantityString(R.plurals.number_of_reps, repsCount, repsCount);
        String timeString;
        if (seconds == 0) {
            timeString = resources.getString(R.string.program_exercise_activity_rest_time_not_set);
        } else {
            String minutesStr = resources.getQuantityString(R.plurals.minutes, 2, 2);
            String secondsStr = resources.getQuantityString(R.plurals.seconds, 25, 25);
            timeString = minutesStr + ' ' + secondsStr;
        }
        onView(setAt(R.id.programSetRepsCount, position)).check(matches(withText(repsCountStr)));
        onView(setAt(R.id.programSetRestTime, position)).check(matches(withText(prefix + ' ' + timeString)));
    }

    @Test
    public void removeSet() throws Exception {
        enterActionMode();
        removeAt(2, RIGHT);
        assertEquals(2, node.getChildren().size());

        removeAt(1, LEFT);
        assertEquals(1, node.getChildren().size());
    }

    @Test
    public void restoreRemovedExercise() throws Exception {
        enterActionMode();
        removeAt(2, LEFT);
        onView(withText(R.string.program_exercise_activity_set_deleted_message)).check(matches(isDisplayed()));
        assertEquals(2, node.getChildren().size());
        onView(withText(android.R.string.cancel)).perform(click());
        assertEquals(3, node.getChildren().size());
        checkListItem(2, 5, 0);
    }

    @Test
    public void moveSet() throws Exception {
        ProgramSet first = node.getChildren().get(0);
        ProgramSet second = node.getChildren().get(1);
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
        onView(setAt(R.id.programSetReorderImage, 0)).perform(viewAction);
        checkListItem(0, 4, 0);
        assertEquals(first, node.getChildren().get(1));
        assertEquals(second, node.getChildren().get(0));
    }

    @Test
    public void backgroundImageTest() throws Exception {
        onView(withId(R.id.programExerciseBackground)).check(matches(not(isDisplayed())));
        enterActionMode();
        removeAll();
        onView(withId(R.id.programExerciseBackground)).check(matches(isDisplayed()));
        onView(withText(android.R.string.cancel)).perform(click());
        onView(withId(R.id.programExerciseBackground)).check(matches(not(isDisplayed())));
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
    public void saveTest() throws Exception {
        onView(withId(R.id.actionSaveExercise)).perform(click());
        verify(vm).saveProgramExercise();
    }

    @Test
    public void pickExerciseTest() throws Exception {
        intending(hasComponent(ExercisePickerActivity.class.getName())).respondWith(activityResult);
        onView(withId(R.id.programExerciseName)).perform(click());
        onView(withId(R.id.programExerciseName)).check(matches(withText("exercise101")));
        assertEquals(101L, node.getExercise().getId());
    }

    @Test
    public void pickExerciseWithoutResultTest() throws Exception {
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(0, null);
        intending(hasComponent(ExercisePickerActivity.class.getName())).respondWith(result);

        onView(withId(R.id.programExerciseName)).perform(click());
    }

    @Test
    public void exerciseChangedAssuranceMessageTest() throws Exception {
        when(vm.isProgramExerciseChanged()).thenReturn(true);
        onView(both(isAssignableFrom(ImageButton.class)).and(withParent(withId(R.id.programExerciseToolbar)))).perform(click());
        onView(withText(R.string.cancel_changes_question)).check(matches(isDisplayed()));
        onView(withText(android.R.string.ok)).perform(click());
        verify(vm).isProgramExerciseChanged();
    }

    private void checkActionModeOn() {
        for (int i = 0; i < 3; i++) {
            onView(setAt(R.id.programSetReorderImage, i)).check(matches(isDisplayed()));
        }
        onView(withId(R.id.programExerciseName)).check(matches(not(isDisplayed())));
    }

    private void checkActionModeOff() {
        for (int i = 0; i < 3; i++) {
            onView(setAt(R.id.programSetReorderImage, i)).check(matches(not(isDisplayed())));
        }
        onView(withId(R.id.programExerciseName)).check(matches(isDisplayed()));
    }

    private void enterActionMode() {
        onView(setAt(R.id.programSetRepsCount, 0)).perform(longClick());
    }

    private void removeAt(int position, int direction) {
        onView(setAt(R.id.programSetRepsCount, position)).perform(direction == LEFT ? swipeLeft() : swipeRight());
    }

    private void removeAll() {
        enterActionMode();
        for (int i = 2; i >= 0; i--) {
            removeAt(i, LEFT);
        }
        Espresso.pressBack();
    }

    private Matcher<View> setAt(@LayoutRes int id, int index) {
        return RecyclerViewItemMatcher.itemAtPosition(id, index);
    }
}
