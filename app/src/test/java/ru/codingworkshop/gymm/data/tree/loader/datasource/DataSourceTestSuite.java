package ru.codingworkshop.gymm.data.tree.loader.datasource;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Радик on 24.08.2017 as part of the Gymm project.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ExerciseDataSourceTest.class,
        NodeDataSourceTest.class,
        ProgramExerciseDataSourceTest.class,
        TreeDataSourceTest.class,
        ProgramTrainingDataSourceTest.class,
        ActualTrainingDataSourceTest.class
})
public class DataSourceTestSuite {
}
