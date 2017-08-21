package ru.codingworkshop.gymm.data.tree.node;

import org.junit.Before;
import org.junit.Test;

import ru.codingworkshop.gymm.data.tree.holder.SortableRestoreChildrenHolder;

import static org.junit.Assert.assertEquals;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */
public class MutableProgramTrainingTreeTest {
    private MutableProgramTrainingTree tree;

    @Before
    public void setUp() throws Exception {
        tree = new MutableProgramTrainingTree();
    }

    @Test
    public void construction() throws Exception {
        assertEquals(SortableRestoreChildrenHolder.class, tree.getChildrenDelegate().getClass());
    }

    @Test
    public void createChildNode() throws Exception {
        assertEquals(MutableProgramExerciseNode.class, tree.createChildNode(null).getClass());
    }

}