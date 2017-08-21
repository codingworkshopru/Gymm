package ru.codingworkshop.gymm.data.tree.holder;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ListChildrenHolderTest.class,
        ImmutableChildrenHolderTest.class,
        SimpleChildrenHolderTest.class,
        SortableRestoreChildrenHolderTest.class
})
public class HolderTestSuite {
}
