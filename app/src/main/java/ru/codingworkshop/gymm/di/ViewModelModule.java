package ru.codingworkshop.gymm.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import ru.codingworkshop.gymm.ui.actual.ActualTrainingViewModel;
import ru.codingworkshop.gymm.viewmodel.ViewModelFactory;

/**
 * Created by Радик on 18.06.2017.
 */

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ActualTrainingViewModel.class)
    abstract ViewModel bindsActualTrainingViewModel(ActualTrainingViewModel actualTrainingViewModel);

//    @Binds
//    @IntoMap
//    @ViewModelKey(MusclesViewModel.class)
//    abstract ViewModel bindsMusclesViewModel(MusclesViewModel musclesViewModel);
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(ExercisesListViewModel.class)
//    abstract ViewModel bindsExercisesListViewModel(ExercisesListViewModel exercisesListViewModel);
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(EditorViewModel.class)
//    abstract ViewModel bindsEditorViewModel(EditorViewModel editorViewModel);
//
    @Binds
    abstract ViewModelProvider.Factory bindsViewModelFactory(ViewModelFactory viewModelFactory);
}