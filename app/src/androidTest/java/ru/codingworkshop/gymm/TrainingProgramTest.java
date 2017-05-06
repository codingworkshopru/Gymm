package ru.codingworkshop.gymm;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.requery.Persistable;
import io.requery.sql.EntityDataStore;
import ru.codingworkshop.gymm.data.model.ProgramTraining;
import ru.codingworkshop.gymm.data.model.ProgramTrainingEntity;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.actionWithAssertions;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Радик on 24.02.2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TrainingProgramTest {
    private static final String TRAINING_NAME_TEXT = "my test training name";

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void cleanup() throws Exception {
        EntityDataStore<Persistable> data = ((App) mActivityRule.getActivity().getApplication()).getData();
        data.transaction().begin();
        data.delete(ProgramTraining.class).where(ProgramTrainingEntity.NAME.eq(TRAINING_NAME_TEXT)).get().call();
        data.transaction().commit();
        data.transaction().close();
    }

    private void selectExercise() {
        // choose exercise
        onView(withId(R.id.program_exercise_name_layout)).perform(click());
        CoordinatesProvider cp = new CoordinatesProvider() {
            @Override
            public float[] calculateCoordinates(View view) {
                int xy[] = new int[2];
                view.getLocationOnScreen(xy);
                return new float[] {xy[0] + 30, xy[1] + 220};
            }
        };
        onView(withId(R.id.imageView5)).perform(new GeneralClickAction(Tap.SINGLE, cp, Press.FINGER));
        onView(withId(R.id.fragment_exercise_picker_exercises)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    private void addProgramTraining(String name) {
        onView(withId(R.id.action_add_program)).perform(click());
        onView(withId(R.id.program_training_name)).perform(typeText(name));
    }

    private void selectProgramTraining(String name) {
        onView(withId(R.id.rv_test_main)).perform(RecyclerViewActions.scrollTo(withChild(withText(name))));
        onView(withId(R.id.rv_test_main)).perform(RecyclerViewActions.actionOnItem(withChild(withText(name)), longClick()));
    }

    private void addProgramExercise() {
        onView(withId(R.id.program_training_add_exercise_button)).perform(click());
        selectExercise();
        onView(withText(R.string.program_exercise_activity_exercise_not_selected_message)).check(doesNotExist());
    }

    private void selectProgramExercise(int index) {
        onView(withId(R.id.program_training_exercises_list)).perform(RecyclerViewActions.actionOnItemAtPosition(index, click()));
    }

    private void addProgramSet() {addProgramSet(1);}
    private void addProgramSet(int times) {
        for (int i = 0; i < times; i++) {
            onView(withId(R.id.program_exercise_add_set)).perform(click());
            onView(withId(android.R.id.button1)).perform(click());
        }
    }

    private void deleteProgramSet() {deleteProgramSets(1);}
    private void deleteProgramSets(int count) {
        onView(withId(R.id.program_exercise_sets_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
        for (int i = 0; i < count; i++) {
            onView(withId(R.id.program_exercise_sets_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, actionWithAssertions(
                    new GeneralSwipeAction(
                            Swipe.FAST,
                            GeneralLocation.CENTER_LEFT,
                            GeneralLocation.CENTER_RIGHT,
                            Press.FINGER
                    )
            )));
        }
        pressBack();
    }

    private void close(boolean save) {
        Matcher<View> matcher = save ? withId(R.id.action_done) : withContentDescription(R.string.abc_action_bar_up_description);
        onView(matcher).perform(click());
        try {
            onView(withId(android.R.id.button1)).perform(click());
        } catch (NoMatchingViewException e) {
            // we're ok if dialog is not shown
        }
    }

    @Test
    @LargeTest
    public void test_createCreate() {
        final int SETS = 5;
        final int TO_DELETE = 2;

        // create
        addProgramTraining(TRAINING_NAME_TEXT);
        addProgramExercise();
        addProgramSet(SETS);
        close(true);
        close(true);

        // delete two sets and press back
        selectProgramTraining(TRAINING_NAME_TEXT);
        selectProgramExercise(0);
        deleteProgramSets(TO_DELETE);
        close(false);
        onView(withText(mActivityRule.getActivity().getResources().getQuantityString(R.plurals.number_of_sets, SETS, SETS))).check(matches(isDisplayed()));
        close(false);

        // delete two sets and save
        selectProgramTraining(TRAINING_NAME_TEXT);
        selectProgramExercise(0);
        deleteProgramSets(TO_DELETE);
        close(true);
        onView(withText(mActivityRule.getActivity().getResources().getQuantityString(R.plurals.number_of_sets, SETS - TO_DELETE, SETS - TO_DELETE))).check(matches(isDisplayed()));
        close(true);
    }

    @Test
    @LargeTest
    public void test_setAddAndCreate() {
        //create training, exercise and set
        addProgramTraining(TRAINING_NAME_TEXT);
        addProgramExercise();
        addProgramSet();

        // save all
        close(true);
        close(true);

        selectProgramTraining(TRAINING_NAME_TEXT);
        addProgramExercise();
        addProgramSet();
        close(true);
        close(true);

        // add new set in created exercise
        selectProgramTraining(TRAINING_NAME_TEXT);
        selectProgramExercise(0);
        addProgramSet();
        close(true);
        close(true);

        // check if set has been added
        selectProgramTraining(TRAINING_NAME_TEXT);
        closeSoftKeyboard();
        onView(withText(mActivityRule.getActivity().getResources().getQuantityString(R.plurals.number_of_sets, 2, 2))).check(matches(isDisplayed()));
    }

    @Test
    @LargeTest
    public void test_deleteSet() {
        //create training, exercise and set
        addProgramTraining(TRAINING_NAME_TEXT);
        addProgramExercise();

        final int SETS_COUNT = 2;
        addProgramSet(SETS_COUNT);

        close(true);
        close(true);

        selectProgramTraining(TRAINING_NAME_TEXT);
        selectProgramExercise(0);
        deleteProgramSet();
        close(true);
        close(true);

        selectProgramTraining(TRAINING_NAME_TEXT);

        closeSoftKeyboard();
        onView(withText(mActivityRule.getActivity().getResources().getQuantityString(R.plurals.number_of_sets, SETS_COUNT - 1, SETS_COUNT - 1))).check(matches(isDisplayed()));
    }
}
