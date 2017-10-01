package ru.codingworkshop.gymm.ui.actual;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ru.codingworkshop.gymm.ui.actual.exercise.ActualExercisesTestSuite;
import ru.codingworkshop.gymm.ui.actual.rest.ActualTrainingRestFragmentTest;

/**
 * Created by Радик on 14.09.2017 as part of the Gymm project.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ActualExercisesTestSuite.class,
        ActualSetFragmentTest.class,
        ActualTrainingActivityTest.class,
        ActualTrainingRestFragmentTest.class
})
public class ActualUiTestSuite {
}
