package ru.codingworkshop.gymm.ui.program;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ru.codingworkshop.gymm.ui.program.exercise.picker.ExerciseListDialogViewModelTest;
import ru.codingworkshop.gymm.ui.program.exercise.picker.MuscleGroupPickerViewModelTest;

/**
 * Created by Радик on 26.08.2017 as part of the Gymm project.
 */


@RunWith(Suite.class)
@Suite.SuiteClasses({
        ProgramTrainingViewModelTest.class,
        ExerciseListDialogViewModelTest.class,
        MuscleGroupPickerViewModelTest.class
})
public class ProgramUiSuite {
}
