package ru.codingworkshop.gymm.data.tree.node;

import org.junit.Test;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.tree.holder.ImmutableChildrenHolder;
import ru.codingworkshop.gymm.util.ModelsFixture;

import static org.junit.Assert.assertEquals;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */
public class ImmutableProgramExerciseNodeTest {

    @Test
    public void construction() throws Exception {
        ImmutableProgramExerciseNode node = new ImmutableProgramExerciseNode();
        assertImmutableChildrenDelegate(node);

        final ProgramExercise programExercise = ModelsFixture.createProgramExercise(2L, 1L, 100L, false);
        node = new ImmutableProgramExerciseNode(programExercise);
        assertImmutableChildrenDelegate(node);
        assertEquals(programExercise, node.getParent());
    }

    private void assertImmutableChildrenDelegate(ProgramExerciseNode node) {
        assertEquals(ImmutableChildrenHolder.class, node.getChildrenDelegate().getClass());
    }
}