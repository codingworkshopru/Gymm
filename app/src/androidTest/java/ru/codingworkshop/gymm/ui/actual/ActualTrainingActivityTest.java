package ru.codingworkshop.gymm.ui.actual;

import android.support.test.espresso.contrib.RecyclerViewActions;

import ru.codingworkshop.gymm.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ru.codingworkshop.gymm.ui.actual.Matchers.currentPageItem;

/**
 * Created by Радик on 17.09.2017 as part of the Gymm project.
 */

class ActualTrainingActivityTest {
    protected void clickDoneButton() {
        onView(currentPageItem(R.id.actualSetDoneButton)).perform(click());
    }

    protected void selectStepAtPosition(int position) {
        onView(withId(R.id.actualExerciseSteps)).perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));
    }

    protected void checkWeight(double weight) {
        onView(currentPageItem(R.id.actualSetWeightEditText)).check(matches(withText(Double.toString(weight))));
    }

    protected void checkRepsCount(int repsCount) {
        onView(currentPageItem(R.id.actualSetRepsCountEditText)).check(matches(withText(Integer.toString(repsCount))));
    }
}
