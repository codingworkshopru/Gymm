package ru.codingworkshop.gymm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ru.codingworkshop.gymm.data.tree.TreeTestSuite;
import ru.codingworkshop.gymm.repository.RepositoryTestSuite;
import ru.codingworkshop.gymm.ui.UiSuite;

/**
 * Created by Радик on 31.07.2017 as part of the Gymm project.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        RepositoryTestSuite.class,
        TreeTestSuite.class,
        UiSuite.class
})
public class GymmTest {
}
