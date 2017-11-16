package ru.codingworkshop.gymm.data.tree.loader;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ru.codingworkshop.gymm.data.tree.loader.builder.TreeBuilderTestSuite;
import ru.codingworkshop.gymm.data.tree.loader.common.LoaderDelegateTest;
import ru.codingworkshop.gymm.data.tree.loader.common.NodeLoaderDelegateTest;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TreeBuilderTestSuite.class,

        LoaderDelegateTest.class,
        NodeLoaderDelegateTest.class,

        ProgramExerciseLoaderTest.class,
        ProgramTrainingTreeLoaderTest.class,
        ActualTrainingTreeLoaderTest.class,
        ExerciseLoaderTest.class
})
public class LoaderTestSuite {
}
