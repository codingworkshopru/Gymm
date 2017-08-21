package ru.codingworkshop.gymm.data.tree.node;

import org.junit.Test;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.tree.holder.SortableRestoreChildrenHolder;
import ru.codingworkshop.gymm.util.ModelsFixture;

import static org.junit.Assert.assertEquals;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */
public class MutableProgramExerciseNodeTest {
    @Test
    public void construction() throws Exception {
        MutableProgramExerciseNode node = new MutableProgramExerciseNode();
        assertImmutableChildrenDelegate(node);

        final ProgramExercise programExercise = ModelsFixture.createProgramExercise(2L, 1L, 100L, false);
        node = new MutableProgramExerciseNode(programExercise);
        assertImmutableChildrenDelegate(node);
        assertEquals(programExercise, node.getParent());
    }

    private void assertImmutableChildrenDelegate(ProgramExerciseNode node) {
        assertEquals(SortableRestoreChildrenHolder.class, node.getChildrenDelegate().getClass());
    }
}