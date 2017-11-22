package ru.codingworkshop.gymm.ui.program;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ru.codingworkshop.gymm.ui.program.exercise.ProgramExerciseCreateFragmentTest;
import ru.codingworkshop.gymm.ui.program.exercise.ProgramExerciseFragmentTest;
import ru.codingworkshop.gymm.ui.program.exercise.ProgramSetEditorFragmentTest;
import ru.codingworkshop.gymm.ui.program.exercise.picker.ExerciseListDialogFragmentTest;
import ru.codingworkshop.gymm.ui.program.exercise.picker.MuscleGroupPickerFragmentTest;
import ru.codingworkshop.gymm.ui.program.training.ProgramTrainingCreateFragmentTest;
import ru.codingworkshop.gymm.ui.program.training.ProgramTrainingFragmentTest;

/**
 * Created by Радик on 10.10.2017 as part of the Gymm project.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ProgramSetEditorFragmentTest.class,
        ProgramExerciseFragmentTest.class,
        ProgramExerciseCreateFragmentTest.class,
        ProgramTrainingFragmentTest.class,
        ProgramTrainingCreateFragmentTest.class,

        ExerciseListDialogFragmentTest.class,
        MuscleGroupPickerFragmentTest.class
})
public class ProgramUiTestSuite {
}
