package ru.codingworkshop.gymm.data;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ru.codingworkshop.gymm.data.tree.TreeTestSuite;
import ru.codingworkshop.gymm.data.util.LoaderAdapterTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TreeTestSuite.class,
        LoaderAdapterTest.class
})
public class DataTestSuite {
}
