package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Radik on 14.11.2017.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ActualTrainingAdapterTest.class,
        ExerciseAdapterTest.class,
        ProgramTrainingAdapterTest.class,

        ExerciseQueryAdapterTest.class,
        PrimaryMuscleGroupQueryAdapterTest.class,
        ProgramExercisesAlterAdapterTest.class,
        ProgramExercisesQueryAdapterTest.class,
        ProgramSetsAlterAdapterTest.class,
        ProgramSetsQueryAdapterTest.class,
        ProgramTrainingAlterAdapterTest.class,
        ProgramTrainingQueryAdapterTest.class,
        SecondaryMuscleGroupsQueryAdapterTest.class
})
public class RepositoryAdapterTestSuite {
}
