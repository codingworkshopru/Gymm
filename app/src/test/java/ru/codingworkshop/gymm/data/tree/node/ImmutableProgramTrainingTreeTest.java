package ru.codingworkshop.gymm.data.tree.node;

import org.junit.Before;
import org.junit.Test;

import ru.codingworkshop.gymm.data.tree.holder.ImmutableChildrenHolder;

import static org.junit.Assert.assertEquals;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */
public class ImmutableProgramTrainingTreeTest {

    private ImmutableProgramTrainingTree tree;

    @Before
    public void setUp() throws Exception {
        tree = new ImmutableProgramTrainingTree();
    }

    @Test
    public void construction() throws Exception {
        assertEquals(ImmutableChildrenHolder.class, tree.getChildrenDelegate().getClass());
    }

    @Test
    public void createChildNode() throws Exception {
        assertEquals(ImmutableProgramExerciseNode.class, tree.createChildNode(null).getClass());
    }

}