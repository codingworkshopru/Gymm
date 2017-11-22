package ru.codingworkshop.gymm.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import ru.codingworkshop.gymm.ui.MainActivityViewModel;
import ru.codingworkshop.gymm.ui.actual.viewmodel.ActualTrainingViewModel;
import ru.codingworkshop.gymm.ui.program.exercise.ProgramExerciseViewModel;
import ru.codingworkshop.gymm.ui.program.exercise.picker.ExerciseListDialogViewModel;
import ru.codingworkshop.gymm.ui.program.exercise.picker.MuscleGroupPickerViewModel;
import ru.codingworkshop.gymm.ui.program.training.ProgramTrainingViewModel;
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
    @ViewModelKey(ProgramExerciseViewModel.class)
    abstract ViewModel bindProgramExerciseViewModel(ProgramExerciseViewModel programExerciseViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MuscleGroupPickerViewModel.class)
    abstract ViewModel bindMuscleGroupPickerViewModel(MuscleGroupPickerViewModel muscleGroupPickerViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ExerciseListDialogViewModel.class)
    abstract ViewModel bindExerciseListDialogViewModel(ExerciseListDialogViewModel exerciseListDialogViewModel);

//    @Binds
//    @IntoMap
//    @ViewModelKey(MusclesViewModel.class)
//    abstract ViewModel bindMusclesViewModel(MusclesViewModel musclesViewModel);
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(ExercisesListViewModel.class)
//    abstract ViewModel bindExercisesListViewModel(ExercisesListViewModel exercisesListViewModel);
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(EditorViewModel.class)
//    abstract ViewModel bindEditorViewModel(EditorViewModel editorViewModel);
//
    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory viewModelFactory);
}