package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ru.codingworkshop.gymm.data.tree.repositoryadapter2.ExerciseAdapterTest;

/**
 * Created by Radik on 14.11.2017.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ActualTrainingAdapterTest.class,
        ExerciseAdapterTest.class,
        ProgramExerciseAdapterTest.class,
        ProgramTrainingAdapterTest.class
})
public class RepositoryAdapterTestSuite {
}
