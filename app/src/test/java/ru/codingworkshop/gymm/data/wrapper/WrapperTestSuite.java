package ru.codingworkshop.gymm.data.wrapper;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Радик on 24.07.2017.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ChildrenDiffTest.class,
        ExerciseWrapperTest.class,
        LoaderTest.class,
        ProgramExerciseWrapperTest.class,
        ProgramTrainingWrapperTest.class,
        SortableChildrenDelegateTest.class
})
public class WrapperTestSuite {
    // container class
}
