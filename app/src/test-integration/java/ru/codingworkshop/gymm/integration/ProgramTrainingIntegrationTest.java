package ru.codingworkshop.gymm.integration;

import android.arch.persistence.room.Room;
import android.content.res.Resources;
import android.support.annotation.IdRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.db.GymmDatabase;
import ru.codingworkshop.gymm.ui.MainActivity;
import ru.codingworkshop.gymm.ui.program.exercise.picker.MuscleGroupPickerFragmentTest;
import ru.codingworkshop.gymm.util.RecyclerViewItemMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static ru.codingworkshop.gymm.db.initializer.DatabaseInitializer.DATABASE_NAME;

/**
 * Created by Radik on 22.11.2017.
 */

public class ProgramTrainingIntegrationTest {
    @Rule public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @BeforeClass
    public static void setUp() throws Exception {
        GymmDatabase db = Room.databaseBuilder(InstrumentationRegistry.getTargetContext(), GymmDatabase.class, DATABASE_NAME)
                .build();
        db.compileStatement("delete from ProgramTraining").execute();
    }

    @Test
    public void addProgramTraining() throws Exception {
        onView(withId(R.id.action_add_program)).perform(click());
        onView(withId(R.id.programTrainingName)).perform(typeText("monday workout"));
        onView(withId(R.id.programTrainingAddExerciseButton)).perform(click());
        addProgramExercise();
        onView(withId(R.id.actionSaveTraining)).perform(click());
        rvItemAt(R.id.rv_test_main, R.id.mainActivityTrainingName, 0).check(matches(withText("monday workout")));
    }

    @Test
    public void editProgramTraining() throws Exception {
        onView(withId(R.id.rv_test_main)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
//        onView(withId(R.id.programTrainingName))
//                .perform(clearText())
//                .perform(typeText("tuesday workout"));
        onView(withId(R.id.programExerciseList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.programSetList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        typeSet(5, 5, 0);
        Resources resources = InstrumentationRegistry.getTargetContext().getResources();
        rvItemAt(R.id.programSetList, R.id.programSetRepsCount, 0).check(matches(withText(resources.getQuantityString(R.plurals.number_of_reps, 5, 5))));
        rvItemAt(R.id.programSetList, R.id.programSetRestTime, 0).check(matches(withText(resources.getString(R.string.program_exercise_activity_dialog_rest_time_label) + " " + resources.getQuantityString(R.plurals.minutes, 5, 5) + " " + resources.getQuantityString(R.plurals.seconds, 0, 0))));
        onView(withId(R.id.programSetList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        typeSet(1, 0, 0);
        rvItemAt(R.id.programSetList, R.id.programSetRestTime, 0).check(matches(withText(resources.getString(R.string.program_exercise_activity_dialog_rest_time_label) + " " + resources.getString(R.string.program_exercise_activity_rest_time_not_set))));
        addProgramSet();
        onView(withId(R.id.actionSaveExercise)).perform(click());
        rvItemAt(R.id.programExerciseList, R.id.programExerciseSetsCount, 0).check(matches(withText(InstrumentationRegistry.getTargetContext().getResources().getQuantityString(R.plurals.number_of_sets, 2, 2))));
        onView(withId(R.id.actionSaveTraining)).perform(click());
//        rvItemAt(R.id.rv_test_main, R.id.mainActivityTrainingName, 0).check(matches(withText("tuesday workout")));
    }

    private void typeSet(int reps, int minutes, int seconds) {
        onView(withParent(withId(R.id.programSetRepsPicker))).perform(typeText(Integer.toString(reps)));
        onView(withParent(withId(R.id.programSetRestMinutesPicker))).perform(typeText(Integer.toString(minutes)));
        onView(withParent(withId(R.id.programSetRestSecondsPicker))).perform(typeText(Integer.toString(seconds)));
        onView(withParent(withId(R.id.programSetRepsPicker))).perform(click());
        onView(withText(android.R.string.ok)).perform(click());
    }

    private void addProgramExercise() {
        pickExercise();
        addProgramSet();
        onView(withId(R.id.actionSaveExercise)).perform(click());
        rvItemAt(R.id.programExerciseList, R.id.programExerciseSetsCount, 0).check(matches(withText(InstrumentationRegistry.getTargetContext().getResources().getQuantityString(R.plurals.number_of_sets, 1, 1))));
    }

    private void addProgramSet() {
        onView(withId(R.id.programExerciseAddSetButton)).perform(click());
        onView(withText(android.R.string.ok)).perform(click());
        rvItemAt(R.id.programSetList, R.id.programSetRepsCount, 0).check(matches(withText(InstrumentationRegistry.getTargetContext().getResources().getQuantityString(R.plurals.number_of_reps, 1, 1))));
        rvItemAt(R.id.programSetList, R.id.programSetRestTime, 0).check(matches(withText(InstrumentationRegistry.getTargetContext().getString(R.string.program_exercise_activity_dialog_rest_time_label) + " " + InstrumentationRegistry.getTargetContext().getString(R.string.program_exercise_activity_rest_time_not_set))));
    }

    private void pickExercise() {
        onView(withId(R.id.programExerciseName)).perform(click());
        onView(both(withId(R.id.muscleGroupPickerHumanMuscles)).and(isDisplayed()))
                .perform(MuscleGroupPickerFragmentTest.clickOnMuscleGroupWithColor("#0066CC"));
        onView(withId(R.id.fragment_exercise_picker_exercises)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.programExerciseName)).check(matches(not(withText(R.string.program_exercise_activity_exercise_empty_text))));
    }

    private ViewInteraction rvItemAt(@IdRes int rvId, @IdRes int itemId, int position) {
        return onView(RecyclerViewItemMatcher.itemAtPosition(rvId, itemId, position));
    }
}
