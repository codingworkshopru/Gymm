package ru.codingworkshop.gymm.data.tree.node;

import org.junit.Test;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.tree.holder.SortableRestoreChildrenHolder;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.TreeBuilders;

import static junit.framework.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */
public class MutableProgramExerciseNodeTest {
    @Test
    public void construction() throws Exception {
        MutableProgramExerciseNode node = new MutableProgramExerciseNode();
        assertImmutableChildrenDelegate(node);

        final ProgramExercise programExercise = Models.createProgramExercise(2L, 1L, 100L, false);
        node = new MutableProgramExerciseNode(programExercise);
        assertImmutableChildrenDelegate(node);
        assertEquals(programExercise, node.getParent());
    }

    @Test
    public void copyConstructor() throws Exception {
        ProgramExerciseNode node = TreeBuilders.buildProgramTrainingTree(1).getChildren().get(0);
        MutableProgramExerciseNode copied = new MutableProgramExerciseNode(node);

        assertNotSame(node.getParent(), copied.getParent());
        assertEquals(node.getParent(), copied.getParent());

        assertSame(node.getExercise(), copied.getExercise());

        assertNotSame(node.getChildren().get(0), copied.getChildren().get(0));
        assertEquals(node.getChildren().get(0), copied.getChildren().get(0));
    }

    private void assertImmutableChildrenDelegate(ProgramExerciseNode node) {
        assertEquals(SortableRestoreChildrenHolder.class, node.getChildrenDelegate().getClass());
    }
}