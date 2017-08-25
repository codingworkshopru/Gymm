package ru.codingworkshop.gymm.data.tree.saver;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Радик on 25.08.2017 as part of the Gymm project.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ModelSaverTest.class,
        ChildrenSaverTest.class,
        ProgramTrainingSaverTest.class,
        ProgramExerciseSaverTest.class
})
public class SaverTestSuite {
}
