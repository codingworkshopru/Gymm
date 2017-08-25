package ru.codingworkshop.gymm.data.tree.node;

import org.junit.Before;
import org.junit.Test;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.tree.holder.SimpleChildrenHolder;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Радик on 22.08.2017 as part of the Gymm project.
 */

public class ActualExerciseNodeTest {
    private ActualExerciseNode node;

    @Before
    public void setUp() throws Exception {
        node = new ActualExerciseNode();
    }

    @Test
    public void constructing() throws Exception {
        assertEquals(SimpleChildrenHolder.class, node.getChildrenDelegate().getClass());

        final ActualExercise actualExercise = Models.createActualExercise(12L, "foo", 11L, 2L);
        node = new ActualExerciseNode(actualExercise);
        assertEquals(actualExercise, node.getParent());
    }

    @Test
    public void programExerciseNodeAccessors() throws Exception {
        final ImmutableProgramExerciseNode programExerciseNode = new ImmutableProgramExerciseNode();
        node.setProgramExerciseNode(programExerciseNode);
        assertEquals(programExerciseNode, node.getProgramExerciseNode());
    }
}
