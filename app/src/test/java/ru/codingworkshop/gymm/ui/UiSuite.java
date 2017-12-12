package ru.codingworkshop.gymm.ui;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ru.codingworkshop.gymm.ui.actual.ActualUiSuite;
import ru.codingworkshop.gymm.ui.info.InfoUiSuite;
import ru.codingworkshop.gymm.ui.program.ProgramUiSuite;

/**
 * Created by Радик on 26.08.2017 as part of the Gymm project.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ActualUiSuite.class,
        ProgramUiSuite.class,
        InfoUiSuite.class,
        MainActivityViewModelTest.class
})
public class UiSuite {
}
