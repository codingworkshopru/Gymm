package ru.codingworkshop.gymm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ru.codingworkshop.gymm.repository.RepositoryTestSuite;
import ru.codingworkshop.gymm.ui.UiTestSuite;

/**
 * Created by Радик on 14.09.2017 as part of the Gymm project.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        RepositoryTestSuite.class,
        UiTestSuite.class
})
public class GymmAndroidTest {
}
