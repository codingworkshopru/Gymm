package ru.codingworkshop.gymm.data.tree.loader;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        NodeLoaderTest.class,
        SetAndRemoveTest.class,
        ProgramExerciseLoaderTest.class,
        ProgramTrainingTreeLoaderTest.class,
        ProgramTrainingTreeAdapterTest.class,
        ActualTrainingTreeLoaderTest.class,
        ActualTrainingTreeAdapterTest.class,
        ExerciseLoaderTest.class
})
public class LoaderTestSuite {
}
