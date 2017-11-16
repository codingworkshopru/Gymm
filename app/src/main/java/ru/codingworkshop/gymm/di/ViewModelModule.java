package ru.codingworkshop.gymm.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import ru.codingworkshop.gymm.ui.actual.viewmodel.ActualTrainingViewModel;
import ru.codingworkshop.gymm.viewmodel.ViewModelFactory;

/**
 * Created by Радик on 18.06.2017.
 */

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ActualTrainingViewModel.class)
    abstract ViewModel bindActualTrainingViewModel(ActualTrainingViewModel actualTrainingViewModel);

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