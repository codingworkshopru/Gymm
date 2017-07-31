package ru.codingworkshop.gymm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ru.codingworkshop.gymm.data.wrapper.WrapperTestSuite;
import ru.codingworkshop.gymm.repository.RepositoryTestSuite;

/**
 * Created by Радик on 31.07.2017 as part of the Gymm project.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        WrapperTestSuite.class,
        RepositoryTestSuite.class
})
public class GymmTest {
}
