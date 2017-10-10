package ru.codingworkshop.gymm.ui;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ru.codingworkshop.gymm.ui.actual.ActualUiTestSuite;
import ru.codingworkshop.gymm.ui.program.ProgramUiTestSuite;

/**
 * Created by Радик on 14.09.2017 as part of the Gymm project.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ActualUiTestSuite.class,
        ProgramUiTestSuite.class
})
public class UiTestSuite {
}
