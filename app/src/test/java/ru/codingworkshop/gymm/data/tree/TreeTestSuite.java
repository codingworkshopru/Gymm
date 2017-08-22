package ru.codingworkshop.gymm.data.tree;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ru.codingworkshop.gymm.data.tree.holder.HolderTestSuite;
import ru.codingworkshop.gymm.data.tree.loader.LoaderTestSuite;
import ru.codingworkshop.gymm.data.tree.node.NodeTestSuite;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        HolderTestSuite.class,
        LoaderTestSuite.class,
        NodeTestSuite.class,
        ChildRestoreAdapterTest.class,
        Preconditions2Test.class
})
public class TreeTestSuite {
}
