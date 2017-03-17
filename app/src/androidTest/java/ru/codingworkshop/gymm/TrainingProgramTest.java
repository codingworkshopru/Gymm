package ru.codingworkshop.gymm;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.codingworkshop.gymm.data.GymContract.ProgramTrainingEntry;
import ru.codingworkshop.gymm.data.GymDbHelper;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.actionWithAssertions;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
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
    public void cleanup() {
        SQLiteOpenHelper helper = new GymDbHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(ProgramTrainingEntry.TABLE_NAME, ProgramTrainingEntry.COLUMN_NAME + "=?", new String[] {TRAINING_NAME_TEXT});
    }

    @Test
    @LargeTest
    public void test_setAddAndCreate() {
        //create training, exercise and set
        onView(withId(R.id.action_add_program)).perform(click());
        onView(withId(R.id.program_training_name)).perform(typeText(TRAINING_NAME_TEXT));
        onView(withId(R.id.program_training_add_exercise_button)).perform(click());
        onView(withId(R.id.program_exercise_add_set)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.action_done)).perform(click());
        onView(withId(R.id.action_done)).perform(click());

        // add new set in created training
        onView(withId(R.id.rv_test_main)).perform(RecyclerViewActions.scrollTo(withChild(withText(TRAINING_NAME_TEXT))));
        onView(withId(R.id.rv_test_main)).perform(RecyclerViewActions.actionOnItem(withChild(withText(TRAINING_NAME_TEXT)), longClick()));
        onView(withId(R.id.program_training_exercises_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.program_exercise_add_set)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.action_done)).perform(click());
        onView(withId(R.id.action_done)).perform(click());

        // check if set has been added
        onView(withId(R.id.rv_test_main)).perform(RecyclerViewActions.scrollTo(withChild(withText(TRAINING_NAME_TEXT))));
        onView(withId(R.id.rv_test_main)).perform(RecyclerViewActions.actionOnItem(withChild(withText(TRAINING_NAME_TEXT)), longClick()));
        closeSoftKeyboard();
        onView(withText(mActivityRule.getActivity().getResources().getQuantityString(R.plurals.number_of_sets, 2, 2))).check(matches(isDisplayed()));
    }

    @Test
    @LargeTest
    public void test_deleteSet() {
        //create training, exercise and set
        onView(withId(R.id.action_add_program)).perform(click());
        onView(withId(R.id.program_training_name)).perform(typeText(TRAINING_NAME_TEXT));
        onView(withId(R.id.program_training_add_exercise_button)).perform(click());

        final int SETS_COUNT = 2;
        for (int i = 0; i < SETS_COUNT; i++) {
            onView(withId(R.id.program_exercise_add_set)).perform(click());
            onView(withId(android.R.id.button1)).perform(click());
        }
        onView(withId(R.id.action_done)).perform(click());
        onView(withId(R.id.action_done)).perform(click());

        onView(withId(R.id.rv_test_main)).perform(RecyclerViewActions.scrollTo(withChild(withText(TRAINING_NAME_TEXT))));
        onView(withId(R.id.rv_test_main)).perform(RecyclerViewActions.actionOnItem(withChild(withText(TRAINING_NAME_TEXT)), longClick()));
        onView(withId(R.id.program_training_exercises_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.program_exercise_sets_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
        onView(withId(R.id.program_exercise_sets_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, actionWithAssertions(
                new GeneralSwipeAction(
                        Swipe.FAST,
                        GeneralLocation.CENTER_LEFT,
                        GeneralLocation.CENTER_RIGHT,
                        Press.FINGER
                )
        )));
        pressBack();
        onView(withId(R.id.action_done)).perform(click());
        onView(withId(R.id.action_done)).perform(click());

        onView(withId(R.id.rv_test_main)).perform(RecyclerViewActions.scrollTo(withChild(withText(TRAINING_NAME_TEXT))));
        onView(withId(R.id.rv_test_main)).perform(RecyclerViewActions.actionOnItem(withChild(withText(TRAINING_NAME_TEXT)), longClick()));

        closeSoftKeyboard();
        onView(withText(mActivityRule.getActivity().getResources().getQuantityString(R.plurals.number_of_sets, SETS_COUNT - 1, SETS_COUNT - 1))).check(matches(isDisplayed()));
    }
}
