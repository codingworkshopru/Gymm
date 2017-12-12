package ru.codingworkshop.gymm.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import ru.codingworkshop.gymm.ui.MainActivityViewModel;
import ru.codingworkshop.gymm.ui.actual.viewmodel.ActualTrainingViewModel;
import ru.codingworkshop.gymm.ui.info.exercise.ExerciseInfoFragmentViewModel;
import ru.codingworkshop.gymm.ui.info.statistics.viewmodel.StatisticsViewModel;
import ru.codingworkshop.gymm.ui.info.statistics.viewmodel.StatisticsViewModel_Factory;
import ru.codingworkshop.gymm.ui.program.exercise.picker.ExerciseListDialogViewModel;
import ru.codingworkshop.gymm.ui.program.exercise.picker.MuscleGroupPickerViewModel;
import ru.codingworkshop.gymm.ui.program.ProgramTrainingViewModel;
import ru.codingworkshop.gymm.viewmodel.ViewModelFactory;

/**
 * Created by Радик on 18.06.2017.
 */

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel.class)
    abstract ViewModel bindMainActivityViewModel(MainActivityViewModel mainActivityViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ActualTrainingViewModel.class)
    abstract ViewModel bindActualTrainingViewModel(ActualTrainingViewModel actualTrainingViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProgramTrainingViewModel.class)
    abstract ViewModel bindProgramTrainingViewModel(ProgramTrainingViewModel programTrainingViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MuscleGroupPickerViewModel.class)
    abstract ViewModel bindMuscleGroupPickerViewModel(MuscleGroupPickerViewModel muscleGroupPickerViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ExerciseListDialogViewModel.class)
    abstract ViewModel bindExerciseListDialogViewModel(ExerciseListDialogViewModel exerciseListDialogViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ExerciseInfoFragmentViewModel.class)
    abstract ViewModel bindExerciseInfoFragmentViewModel(ExerciseInfoFragmentViewModel exerciseInfoFragmentViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(StatisticsViewModel.class)
    abstract ViewModel bindStatisticsViewModel(StatisticsViewModel statisticsViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory viewModelFactory);
}