package ru.codingworkshop.gymm.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import ru.codingworkshop.gymm.ui.MainActivityViewModel;
import ru.codingworkshop.gymm.ui.actual.viewmodel.ActualTrainingViewModel;
import ru.codingworkshop.gymm.ui.info.exercise.ExerciseInfoFragmentViewModel;
import ru.codingworkshop.gymm.ui.info.statistics.journal.StatisticsJournalViewModel;
import ru.codingworkshop.gymm.ui.info.statistics.plot.StatisticsPlotViewModel;
import ru.codingworkshop.gymm.ui.program.ProgramTrainingViewModel;
import ru.codingworkshop.gymm.ui.program.exercise.picker.ExercisePickerViewModel;
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
    @ViewModelKey(ExercisePickerViewModel.class)
    abstract ViewModel bindExercisePickerViewModel(ExercisePickerViewModel exercisePickerViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ExerciseInfoFragmentViewModel.class)
    abstract ViewModel bindExerciseInfoFragmentViewModel(ExerciseInfoFragmentViewModel exerciseInfoFragmentViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(StatisticsPlotViewModel.class)
    abstract ViewModel bindStatisticsPlotViewModel(StatisticsPlotViewModel statisticsPlotViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(StatisticsJournalViewModel.class)
    abstract ViewModel bindStatisticsJournalViewModel(StatisticsJournalViewModel statisticsJournalViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory viewModelFactory);
}