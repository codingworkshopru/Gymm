package ru.codingworkshop.gymm.data.tree.node;

import org.junit.Before;
import org.junit.Test;

import ru.codingworkshop.gymm.data.tree.holder.ImmutableChildrenHolder;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.assertEquals;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */
public class ImmutableProgramExerciseNodeTest {

    private ImmutableProgramExerciseNode node;

    @Before
    public void setUp() throws Exception {
        node = new ImmutableProgramExerciseNode();
    }

    @Test
    public void construction() throws Exception {
        node = new ImmutableProgramExerciseNode();
        assertImmutableChildrenDelegate(node);

        node = new ImmutableProgramExerciseNode(Models.createProgramExercise(2L, 1L, 100L, false));
        assertEquals(2L, node.getParent().getId());
        assertImmutableChildrenDelegate(node);
    }

    @Test
    public void setExercise() {
        node.setExercise(Models.createExercise(1L, "foo"));
        assertEquals("foo", node.getExercise().getName());
    }

    @Test
    public void setParent() throws Exception {
        node.setParent(Models.createProgramExercise(2L, 1L, 100L, false));
        assertEquals(2L, node.getParent().getId());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setSortOrder() {
        node.setSortOrder(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setId() {
        node.setId(1L);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setDrafting() {
        node.setDrafting(true);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setProgramTrainingId() {
        node.setProgramTrainingId(1L);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setExerciseId() {
        node.setExerciseId(1L);
    }

    private void assertImmutableChildrenDelegate(ProgramExerciseNode node) {
        assertEquals(ImmutableChildrenHolder.class, node.getChildrenDelegate().getClass());
    }
}