package ru.codingworkshop.gymm.data.tree.node;

import org.junit.Test;

import ru.codingworkshop.gymm.data.tree.holder.ImmutableChildrenHolder;

import static org.junit.Assert.assertTrue;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */
public class ExerciseNodeTest {
    @Test
    public void test() {
        ExerciseNode node = new ExerciseNode();
        assertTrue(node.getChildrenDelegate() instanceof ImmutableChildrenHolder);
    }
}