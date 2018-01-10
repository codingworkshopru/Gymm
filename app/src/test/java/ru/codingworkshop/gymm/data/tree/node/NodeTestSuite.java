package ru.codingworkshop.gymm.data.tree.node;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        BaseNodeTest.class,
        ImmutableBaseNodeTest.class,
        ExerciseNodeTest.class,
        ProgramExerciseNodeTest.class,
        ImmutableProgramExerciseNodeTest.class,
        MutableProgramExerciseNodeTest.class,
        ImmutableProgramTrainingTreeTest.class,
        MutableProgramTrainingTreeTest.class
})
public class NodeTestSuite {
}
