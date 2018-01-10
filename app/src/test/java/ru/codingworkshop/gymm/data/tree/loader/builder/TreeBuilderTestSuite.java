package ru.codingworkshop.gymm.data.tree.loader.builder;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Радик on 25.08.2017 as part of the Gymm project.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ActualTrainingTreeBuilderTest.class,
        ProgramTrainingTreeBuilderTest.class,
        ImmutableActualTrainingTreeBuilderTest.class
})
public class TreeBuilderTestSuite {
}
