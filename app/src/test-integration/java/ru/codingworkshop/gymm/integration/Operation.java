package ru.codingworkshop.gymm.integration;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.hamcrest.Matcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.db.GymmDatabase;
import ru.codingworkshop.gymm.ui.common.CustomisableNumberPicker;
import ru.codingworkshop.gymm.ui.program.exercise.picker.MuscleGroupPickerFragmentTest;
import ru.codingworkshop.gymm.util.RecyclerViewItemMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.not;

/**
 * Created by Radik on 27.11.2017.
 */
class Operation {
    static void addProgramTrainingClick() {
        try {
            Espresso.openActionBarOverflowOrOptionsMenu(getTargetContext());
            onView(withText(R.string.main_activity_menu_add_program_title)).perform(click());
        } catch (NoMatchingViewException e) { // there is no action bar overflow menu
            e.printStackTrace();
            onView(withId(R.id.action_add_program)).perform(click());
        }
        Espresso.closeSoftKeyboard();
    }

    static void editProgramTrainingClick(String name) {
        onView(withId(R.id.rv_test_main)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(name)), click()));
    }

    static void saveProgramTrainingClick() {
        onView(withId(R.id.actionSaveTraining)).perform(click());
    }

    static void addProgramExerciseClick() {
        onView(withId(R.id.programTrainingAddExerciseButton)).perform(click());
    }

    static void editProgramExerciseClick(String name) {
        onView(withId(R.id.programExerciseList)).perform(RecyclerViewActions.actionOnItem(withChild(withText(name)), click()));
    }

    static void deleteProgramExerciseAt(int position) {
        onView(withId(R.id.programExerciseList)).perform(RecyclerViewActions.actionOnItemAtPosition(position, swipeRight()));
    }

    static void saveProgramExerciseClick() {
        onView(withId(R.id.actionSaveExercise)).perform(click());
    }

    static void addProgramSetClick() {
        onView(withId(R.id.programExerciseAddSetButton)).perform(click());
    }

    static void editProgramSetClick(int position) {
        onView(withId(R.id.programSetList)).perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));
    }

    static void typeProgramTrainingName(String name) {
        onView(withId(R.id.programTrainingName)).perform(typeText(name));
    }

    static void checkProgramTraining(String name) {
        rvItemAt(R.id.rv_test_main, R.id.mainActivityTrainingName, 0).check(matches(withText(name)));
    }

    static void checkProgramExercise(int position, String exerciseName, int setsCount) {
        rvItemAt(R.id.programExerciseList, R.id.programExerciseName, position).check(matches(withText(exerciseName)));
        rvItemAt(R.id.programExerciseList, R.id.programExerciseSetsCount, position).check(matches(withText(getPlural(R.plurals.number_of_sets, setsCount))));
    }

    static void checkExerciseNotPresented(String name) {
        onView(withId(R.id.programExerciseList)).check(matches(not(hasDescendant(withText(name)))));
    }

    static void typeSetAndSaveIt(int reps, int minutes, int seconds) {
        typeAndCheck(R.id.programSetRepsPicker, reps);
        typeAndCheck(R.id.programSetRestMinutesPicker, minutes);
        typeAndCheck(R.id.programSetRestSecondsPicker, seconds);
        onView(withId(R.id.programSetRepsPicker)).perform(click());
        onView(withText(android.R.string.ok)).perform(click());
    }

    private static void typeAndCheck(@IdRes int id, int value) {
        Matcher<View> m = matchPickerEditor(id);
        String stringValue = Integer.toString(value);
        onView(withId(id)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(NumberPicker.class);
            }

            @Override
            public String getDescription() {
                return String.format("number picker set value %d", value);
            }

            @Override
            public void perform(UiController uiController, View view) {
                if (view instanceof CustomisableNumberPicker) {
                    CustomisableNumberPicker numberPicker = (CustomisableNumberPicker) view;
                    numberPicker.setDisplayedValue(value);
                    try {
                        Method notifyChange = NumberPicker.class.getDeclaredMethod("notifyChange", int.class, int.class);
                        notifyChange.setAccessible(true);
                        notifyChange.invoke(numberPicker, 0, value);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new RuntimeException(String.format(
                            "Failed to set value %d to %s",
                            value,
                            CustomisableNumberPicker.class.getSimpleName()));
                }
            }
        });
        onView(m).check(matches(withText(stringValue)));
    }

    private static Matcher<View> matchPickerEditor(@IdRes int id) {
        return both(withParent(withId(id))).and(isAssignableFrom(TextView.class));
    }

    static void checkSet(int position, int reps, int minutes, int seconds) {
        rvItemAt(R.id.programSetList, R.id.programSetRepsCount, position).check(matches(withText(getTargetContext().getResources().getQuantityString(R.plurals.number_of_reps, reps, reps))));
        String repsCount = getS(R.string.program_exercise_activity_dialog_rest_time_label);
        String restTime = minutes == 0 && seconds == 0
                ? getS(R.string.program_exercise_activity_rest_time_not_set)
                : getPlural(R.plurals.minutes, minutes) + " " + getPlural(R.plurals.seconds, seconds);

        rvItemAt(R.id.programSetList, R.id.programSetRestTime, position).check(matches(withText(repsCount + " " + restTime)));
    }

    static void pickExercise(GymmDatabase db, String name) {
        onView(withId(R.id.programExerciseName)).perform(click());
        MuscleGroup mg = db.getMuscleGroupDao().getMuscleGroupByExerciseNameSync(name);
        if (!mg.isAnterior()) {
            onView(withText(getTargetContext().getString(R.string.muscles_activity_posterior))).perform(click());
        }
        onView(both(withId(R.id.muscleGroupPickerHumanMuscles)).and(isDisplayed()))
                .perform(MuscleGroupPickerFragmentTest.clickOnMuscleGroupWithColor(mg.getMapColorRgb()));
        onView(withId(R.id.fragment_exercise_picker_exercises)).perform(RecyclerViewActions.actionOnItem(withChild(withText(name)), click()));
        onView(withId(R.id.programExerciseName)).check(matches(withText(name)));
    }

    static void clearProgramTrainingName() {
        onView(withId(R.id.programTrainingName)).perform(clearText());
    }

    static void enterActionMode(@IdRes int listViewId) {
        onView(withId(listViewId)).perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
    }

    static void exitActionMode() {
        Espresso.pressBack();
    }

    static ViewInteraction rvItemAt(@IdRes int rvId, @IdRes int itemId, int position) {
        return onView(RecyclerViewItemMatcher.itemAtPosition(rvId, itemId, position));
    }

    static String getS(@StringRes int stringId) {
        return getTargetContext().getString(stringId);
    }

    static String getPlural(@PluralsRes int pluralId, int quantity) {
        return getTargetContext().getResources().getQuantityString(pluralId, quantity, quantity);
    }

    private static Context getTargetContext() {
        return InstrumentationRegistry.getTargetContext();
    }
}
