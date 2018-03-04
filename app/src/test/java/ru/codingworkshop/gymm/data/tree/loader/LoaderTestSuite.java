package ru.codingworkshop.gymm.data.tree.loader;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ru.codingworkshop.gymm.data.tree.loader.builder.TreeBuilderTestSuite;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TreeBuilderTestSuite.class,

        ProgramTrainingTreeLoaderTest.class,
        ImmutableActualTrainingTreeLoaderTest.class,
        ActualTrainingTreeLoaderTest.class,
        ActualTrainingEmptyTreeLoaderTest.class,
        ExerciseLoaderTest.class
})
public class LoaderTestSuite {
}
