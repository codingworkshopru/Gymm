package ru.codingworkshop.gymm.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.codingworkshop.gymm.ui.MainActivity;

/**
 * Created by Радик on 31.05.2017.
 */

@Module
public abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract MainActivity contributesMainActivityInjector();

    // will add in further work
//    @ContributesAndroidInjector
//    abstract ProgramTrainingActivity contributeProgramTrainingActivityInjector();
//
//    @ContributesAndroidInjector
//    abstract ProgramExerciseActivity contributeProgramExerciseActivityInjector();
//
//    @ContributesAndroidInjector
//    abstract MuscleGroupsActivity contributeMuscleGroupsActivityInjector();

//    @ContributesAndroidInjector
//    abstract MuscleGroupsActivity contributeMuscleGroupsListActivityInjector();
//
//    @ContributesAndroidInjector
//    abstract ExercisesListActivity contributeExercisesListActivityInjector();
//
//    @ContributesAndroidInjector
//    abstract EditorActivity contributeEditorActivityInjector();
}
