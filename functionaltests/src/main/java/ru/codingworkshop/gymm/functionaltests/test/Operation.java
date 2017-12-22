package ru.codingworkshop.gymm.functionaltests.test;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.db.GymmDatabase;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.not;

/**
 * Created by Radik on 22.12.2017.
 */

public class Operation {
    static void addProgramTrainingClick() {
        onView(withId(ru.codingworkshop.gymm.R.id.action_add_program)).perform(click());
    }

    static void editProgramTrainingClick(String name) {
        onView(withId(ru.codingworkshop.gymm.R.id.rv_test_main)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(name)), click()));
    }

    static void saveProgramTrainingClick() {
        onView(withId(ru.codingworkshop.gymm.R.id.actionSaveTraining)).perform(click());
    }

    static void addProgramExerciseClick() {
        onView(withId(ru.codingworkshop.gymm.R.id.programTrainingAddExerciseButton)).perform(click());
    }

    static void editProgramExerciseClick(String name) {
        onView(withId(ru.codingworkshop.gymm.R.id.programExerciseList)).perform(RecyclerViewActions.actionOnItem(withChild(withText(name)), click()));
    }

    static void deleteProgramExerciseAt(int position) {
        onView(withId(ru.codingworkshop.gymm.R.id.programExerciseList)).perform(RecyclerViewActions.actionOnItemAtPosition(position, swipeRight()));
    }

    static void saveProgramExerciseClick() {
        onView(withId(ru.codingworkshop.gymm.R.id.actionSaveExercise)).perform(click());
    }

    static void addProgramSetClick() {
        onView(withId(ru.codingworkshop.gymm.R.id.programExerciseAddSetButton)).perform(click());
    }

    static void editProgramSetClick(int position) {
        onView(withId(ru.codingworkshop.gymm.R.id.programSetList)).perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));
    }

    static void typeProgramTrainingName(String name) {
        onView(withId(ru.codingworkshop.gymm.R.id.programTrainingName)).perform(typeText(name));
    }

    static void checkProgramTraining(String name) {
        rvItemAt(ru.codingworkshop.gymm.R.id.rv_test_main, ru.codingworkshop.gymm.R.id.mainActivityTrainingName, 0).check(matches(withText(name)));
    }

    static void checkProgramExercise(int position, String exerciseName, int setsCount) {
        rvItemAt(ru.codingworkshop.gymm.R.id.programExerciseList, ru.codingworkshop.gymm.R.id.programExerciseName, position).check(matches(withText(exerciseName)));
        rvItemAt(ru.codingworkshop.gymm.R.id.programExerciseList, ru.codingworkshop.gymm.R.id.programExerciseSetsCount, position).check(matches(withText(getPlural(ru.codingworkshop.gymm.R.plurals.number_of_sets, setsCount))));
    }

    static void checkExerciseNotPresented(String name) {
        onView(withId(ru.codingworkshop.gymm.R.id.programExerciseList)).check(matches(not(hasDescendant(withText(name)))));
    }

    static void typeSetAndSaveIt(int reps, int minutes, int seconds) {
        onView(withParent(withId(ru.codingworkshop.gymm.R.id.programSetRepsPicker))).perform(typeText(Integer.toString(reps)));
        onView(withParent(withId(ru.codingworkshop.gymm.R.id.programSetRestMinutesPicker))).perform(typeText(Integer.toString(minutes)));
        onView(withParent(withId(ru.codingworkshop.gymm.R.id.programSetRestSecondsPicker))).perform(typeText(Integer.toString(seconds)));
        onView(withParent(withId(ru.codingworkshop.gymm.R.id.programSetRepsPicker))).perform(click());
        onView(withText(android.R.string.ok)).perform(click());
    }

    static void checkSet(int position, int reps, int minutes, int seconds) {
        rvItemAt(ru.codingworkshop.gymm.R.id.programSetList, ru.codingworkshop.gymm.R.id.programSetRepsCount, position).check(matches(withText(InstrumentationRegistry.getTargetContext().getResources().getQuantityString(ru.codingworkshop.gymm.R.plurals.number_of_reps, reps, reps))));
        String repsCount = getS(ru.codingworkshop.gymm.R.string.program_exercise_activity_dialog_rest_time_label);
        String restTime = minutes == 0 && seconds == 0
                ? getS(ru.codingworkshop.gymm.R.string.program_exercise_activity_rest_time_not_set)
                : getPlural(ru.codingworkshop.gymm.R.plurals.minutes, minutes) + " " + getPlural(ru.codingworkshop.gymm.R.plurals.seconds, seconds);

        rvItemAt(ru.codingworkshop.gymm.R.id.programSetList, ru.codingworkshop.gymm.R.id.programSetRestTime, position).check(matches(withText(repsCount + " " + restTime)));
    }

    static void pickExercise(GymmDatabase db, String name) {
        onView(withId(ru.codingworkshop.gymm.R.id.programExerciseName)).perform(click());
        MuscleGroup mg = db.getMuscleGroupDao().getMuscleGroupByExerciseNameSync(name);
        if (!mg.isAnterior()) {
            onView(withText(InstrumentationRegistry.getTargetContext().getString(ru.codingworkshop.gymm.R.string.muscles_activity_posterior))).perform(click());
        }
        onView(both(withId(ru.codingworkshop.gymm.R.id.muscleGroupPickerHumanMuscles)).and(isDisplayed()))
                .perform(clickOnMuscleGroupWithColor(mg.getMapColorRgb()));
        onView(withId(ru.codingworkshop.gymm.R.id.fragment_exercise_picker_exercises)).perform(RecyclerViewActions.actionOnItem(withChild(withText(name)), click()));
        onView(withId(ru.codingworkshop.gymm.R.id.programExerciseName)).check(matches(withText(name)));
}

    static void clearProgramTrainingName() {
        onView(withId(ru.codingworkshop.gymm.R.id.programTrainingName)).perform(clearText());
    }

    static void enterActionMode(@IdRes int listViewId) {
        onView(withId(listViewId)).perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
    }

    static void exitActionMode() {
        Espresso.pressBack();
    }

    static ViewInteraction rvItemAt(@IdRes int rvId, @IdRes int itemId, int position) {
        return onView(itemAtPosition(rvId, itemId, position));
    }

    public static Matcher<View> itemAtPosition(@IdRes final int recyclerViewId, @IdRes final int targetViewId, final int position) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                View targetView = findItemView(item, recyclerViewId, targetViewId, position);
                return targetView != null && item == targetView;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is matches to specified item of specified recycler view");
            }
        };
    }

    public static View findItemView(View item, @IdRes int recyclerViewId, @IdRes int targetViewId, int position) {
        RecyclerView rv = (RecyclerView) item.getRootView().findViewById(recyclerViewId);
        if (rv == null) {
            return null;
        }

        RecyclerView.ViewHolder viewHolder = rv.findViewHolderForAdapterPosition(position);
        if (viewHolder == null) {
            return null;
        }

        View child = viewHolder.itemView;
        if (child == null) {
            return null;
        }

        return child.findViewById(targetViewId);
    }

    static String getS(@StringRes int stringId) {
        return InstrumentationRegistry.getTargetContext().getString(stringId);
    }

    static String getPlural(@PluralsRes int pluralId, int quantity) {
        return InstrumentationRegistry.getTargetContext().getResources().getQuantityString(pluralId, quantity, quantity);
    }

    public static GeneralClickAction clickOnMuscleGroupWithColor(String color) {
        return new GeneralClickAction(
                Tap.SINGLE,
                getCoordinatesForColor(color, ru.codingworkshop.gymm.R.id.muscleGroupPickerMap),
                Press.FINGER,
                InputDevice.SOURCE_UNKNOWN,
                MotionEvent.BUTTON_PRIMARY
        );
    }

    private static CoordinatesProvider getCoordinatesForColor(final String htmlRgbColor, final @IdRes int searchImageView) {
        return new CoordinatesProvider() {
            @Override
            public float[] calculateCoordinates(View view) {
                View v = view.getRootView().findViewById(searchImageView);
                if (v instanceof ImageView) {
                    int parsedColor = Color.parseColor(htmlRgbColor);
                    ImageView iw = (ImageView) v;

                    iw.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(iw.getDrawingCache());
                    iw.setDrawingCacheEnabled(false);
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();

                    Point topLeft = null;

                    for (int x = 0; x < width; x++) {
                        if (topLeft != null) {
                            break;
                        }
                        for (int y = 0; y < height; y++) {
                            if (bitmap.getPixel(x, y) == parsedColor) {
                                topLeft = new Point(x, y);
                                break;
                            }
                        }
                    }

                    if (topLeft == null) {
                        throw new IllegalArgumentException("color " + htmlRgbColor + " not found");
                    }

                    Point bottomRight = new Point();
                    for (int x = topLeft.x; bitmap.getPixel(x, topLeft.y) == parsedColor && x < width; x++) {
                        bottomRight.set(x, 0);
                    }

                    for (int y = topLeft.y; bitmap.getPixel(topLeft.x, y) == parsedColor && y < height; y++) {
                        bottomRight.set(bottomRight.x, y);
                    }

                    Rect r = new Rect(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y);

                    int[] screenLocation = new int[2];
                    view.getLocationOnScreen(screenLocation);
                    float[] result = {
                            screenLocation[0] + r.centerX(),
                            screenLocation[1] + r.centerY()
                    };
                    Log.d("FROM TEST", "tap coordinates are " + result[0] + " " + result[1] + " expected color is " + parsedColor);
                    return result;
                } else {
                    throw new IllegalArgumentException("the view in matcher must be an ImageView instance");
                }
            }
        };
    }
}
