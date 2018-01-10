package ru.codingworkshop.gymm.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.codingworkshop.gymm.ui.actual.exercise.ActualExercisesFragment;
import ru.codingworkshop.gymm.ui.info.exercise.ExerciseInfoFragment;
import ru.codingworkshop.gymm.ui.info.statistics.journal.StatisticsJournalExerciseDetailsFragment;
import ru.codingworkshop.gymm.ui.info.statistics.journal.StatisticsJournalTrainingDetailsFragment;
import ru.codingworkshop.gymm.ui.info.statistics.journal.StatisticsJournalTrainingsFragment;
import ru.codingworkshop.gymm.ui.program.exercise.ProgramExerciseFragment;
import ru.codingworkshop.gymm.ui.program.exercise.ProgramSetEditorFragment;
import ru.codingworkshop.gymm.ui.program.exercise.picker.ExerciseListDialogFragment;
import ru.codingworkshop.gymm.ui.program.exercise.picker.MuscleGroupPickerFragment;
import ru.codingworkshop.gymm.ui.program.training.ProgramTrainingFragment;

/**
 * Created by Радик on 18.06.2017.
 */

@SuppressWarnings("unused")
@Module
abstract class FragmentModule {
    @ContributesAndroidInjector
    abstract ProgramTrainingFragment contributeProgramTrainingFragment();

    @ContributesAndroidInjector
    abstract ProgramExerciseFragment contributeProgramExerciseFragment();

    @ContributesAndroidInjector
    abstract ProgramSetEditorFragment contributeProgramSetEditorFragment();

    @ContributesAndroidInjector
    abstract MuscleGroupPickerFragment contributeMuscleGroupPickerFragment();

    @ContributesAndroidInjector
    abstract ExerciseListDialogFragment contributeExerciseLIstDialogFragment();

    @ContributesAndroidInjector
    abstract ExerciseInfoFragment contributeExerciseInfoFragment();

    @ContributesAndroidInjector
    abstract ActualExercisesFragment contributeActualExercisesFragment();

    @ContributesAndroidInjector
    abstract StatisticsJournalTrainingsFragment contributeStatisticsJournalTrainingsFragment();

    @ContributesAndroidInjector
    abstract StatisticsJournalTrainingDetailsFragment contributeStatisticsJournalTrainingFragment();

    @ContributesAndroidInjector
    abstract StatisticsJournalExerciseDetailsFragment contributeStatisticsJournalExerciseDetailsFragment();
}
