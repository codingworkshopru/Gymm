package ru.codingworkshop.gymm.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.codingworkshop.gymm.ui.program.exercise.ProgramExerciseFragment;
import ru.codingworkshop.gymm.ui.program.exercise.picker.ExerciseListDialogFragment;
import ru.codingworkshop.gymm.ui.program.exercise.picker.MuscleGroupPickerFragment;
import ru.codingworkshop.gymm.ui.program.training.ProgramTrainingFragment;

/**
 * Created by Радик on 18.06.2017.
 */

@Module
abstract class FragmentModule {
    @ContributesAndroidInjector
    abstract ProgramTrainingFragment contributeProgramTrainingFragment();

    @ContributesAndroidInjector
    abstract ProgramExerciseFragment contributeProgramExerciseFragment();

    @ContributesAndroidInjector
    abstract MuscleGroupPickerFragment contributeMuscleGroupPickerFragment();

    @ContributesAndroidInjector
    abstract ExerciseListDialogFragment contributeExerciseLIstDialogFragment();
}
