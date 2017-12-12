package ru.codingworkshop.gymm.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.codingworkshop.gymm.ui.MainActivity;
import ru.codingworkshop.gymm.ui.actual.ActualTrainingActivity;
import ru.codingworkshop.gymm.ui.info.exercise.ExerciseInfoActivity;
import ru.codingworkshop.gymm.ui.info.statistics.StatisticsActivity;
import ru.codingworkshop.gymm.ui.program.ProgramTrainingActivity;
import ru.codingworkshop.gymm.ui.program.exercise.picker.ExercisePickerActivity;

/**
 * Created by Радик on 31.05.2017.
 */

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivityInjector();

    @ContributesAndroidInjector
    abstract ProgramTrainingActivity contributeProgramTrainingActivityInjector();

    @ContributesAndroidInjector
    abstract ExercisePickerActivity contributeExercisePickerActivityInjector();

    @ContributesAndroidInjector
    abstract ExerciseInfoActivity contributeExerciseInfoActivity();

    @ContributesAndroidInjector
    abstract ActualTrainingActivity contributeActualTrainingActivity();

    @ContributesAndroidInjector
    abstract StatisticsActivity contributeStatisticsActivity();
}
