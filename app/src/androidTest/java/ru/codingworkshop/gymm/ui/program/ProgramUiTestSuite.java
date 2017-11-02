package ru.codingworkshop.gymm.ui.program;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ru.codingworkshop.gymm.ui.program.exercise.ProgramExerciseFragmentTest;
import ru.codingworkshop.gymm.ui.program.training.ProgramTrainingCreateFragmentTest;
import ru.codingworkshop.gymm.ui.program.training.ProgramTrainingFragmentTest;

/**
 * Created by Радик on 10.10.2017 as part of the Gymm project.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ProgramExerciseFragmentTest.class,
        ProgramTrainingFragmentTest.class,
        ProgramTrainingCreateFragmentTest.class
})
public class ProgramUiTestSuite {
}
