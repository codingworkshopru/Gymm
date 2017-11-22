package ru.codingworkshop.gymm.integration;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.ui.MainActivity;
import ru.codingworkshop.gymm.ui.program.exercise.picker.MuscleGroupPickerFragment;
import ru.codingworkshop.gymm.ui.program.exercise.picker.MuscleGroupPickerFragmentTest;
import ru.codingworkshop.gymm.util.RecyclerViewItemMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.not;

/**
 * Created by Radik on 22.11.2017.
 */

public class ProgramTrainingIntegrationTest {
    @Rule public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void addProgramTraining() throws Exception {
        onView(withId(R.id.action_add_program)).perform(click());
        onView(withId(R.id.programTrainingName)).perform(typeText("monday workout"));
        onView(withId(R.id.programTrainingAddExerciseButton)).perform(click());
        addProgramExercise();
        onView(withId(R.id.actionSaveTraining)).perform(click());
        onView(RecyclerViewItemMatcher.itemAtPosition(R.id.rv_test_main, R.id.mainActivityTrainingItem, 0)).check(matches(withText("monday workout")));
    }

    private void addProgramExercise() {
        pickExercise();
        addProgramSet();
        onView(withId(R.id.actionSaveExercise)).perform(click());
        onView(RecyclerViewItemMatcher.itemAtPosition(R.id.programExerciseList, R.id.programSetRestTime, 0)).check(matches(withText(InstrumentationRegistry.getTargetContext().getResources().getQuantityString(R.plurals.number_of_sets, 1, 1))));
    }

    private void addProgramSet() {
        onView(withId(R.id.programExerciseAddSetButton)).perform(click());
        onView(withText(android.R.string.ok)).perform(click());
        onView(RecyclerViewItemMatcher.itemAtPosition(R.id.programSetList, R.id.programSetRepsCount, 0)).check(matches(withText(InstrumentationRegistry.getTargetContext().getResources().getQuantityString(R.plurals.number_of_reps, 1, 1))));
    }

    private void pickExercise() {
        onView(withId(R.id.programExerciseName)).perform(click());
        onView(both(withId(R.id.muscleGroupPickerHumanMuscles)).and(isDisplayed()))
                .perform(MuscleGroupPickerFragmentTest.clickOnMuscleGroupWithColor("#0066CC"));
        onView(withId(R.id.fragment_exercise_picker_exercises)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.programExerciseName)).check(matches(not(withText(R.string.program_exercise_activity_exercise_empty_text))));
    }
}
