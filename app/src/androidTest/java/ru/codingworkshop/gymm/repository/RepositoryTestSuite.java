package ru.codingworkshop.gymm.repository;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Радик on 20.09.2017 as part of the Gymm project.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        BaseRepositoryAndroidTest.class,
        ActualTrainingRepositoryTest.class
})
public class RepositoryTestSuite {
}
