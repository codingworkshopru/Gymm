package ru.codingworkshop.gymm.data.wrapper;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Радик on 24.07.2017.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoaderTest.class,
        BaseLoaderTest.class,
        ChildrenSaverTest.class,
        SortableChildrenDelegateTest.class,
        ExerciseWrapperTest.class,
        ProgramExerciseWrapperTest.class,
        ProgramTrainingWrapperTest.class,
        ActualTrainingWrapperTest.class
})
public class WrapperTestSuite {
    // useless class
}
