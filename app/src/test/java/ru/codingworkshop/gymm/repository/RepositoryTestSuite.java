package ru.codingworkshop.gymm.repository;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Радик on 27.07.2017.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        BaseRepositoryTest.class,
        ExercisesRepositoryTest.class,
        MuscleGroupRepositoryTest.class,
        ProgramTrainingRepositoryTest.class,
        SecondaryMuscleGroupsHelperTest.class,
        ActualTrainingRepositoryTest.class
})
public class RepositoryTestSuite {
}
