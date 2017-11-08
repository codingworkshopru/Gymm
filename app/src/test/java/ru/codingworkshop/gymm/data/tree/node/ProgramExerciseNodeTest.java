package ru.codingworkshop.gymm.data.tree.node;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.tree.holder.SimpleChildrenHolder;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramExerciseNodeTest {
    @Mock private ProgramExercise exercise;
    private ProgramExerciseNode node;

    @Before
    public void setUp() throws Exception {
        node = new ProgramExerciseNode(new SimpleChildrenHolder<>()) {};
        node.setParent(exercise);
    }

    @Test
    public void exerciseAccessors() throws Exception {
        Exercise foo = Models.createExercise(100L, "foo");
        node.setExercise(foo);
        assertEquals(foo, node.getExercise());
        verify(exercise).setExerciseId(100L);
    }

    @Test
    public void sortOrderAccessors() throws Exception {
        node.getSortOrder();
        verify(exercise).getSortOrder();

        node.setSortOrder(0);
        verify(exercise).setSortOrder(0);
    }

    @Test
    public void idAccessors() throws Exception {
        node.getId();
        verify(exercise).getId();

        node.setId(2L);
        verify(exercise).setId(2L);
    }

    @Test
    public void isDrafting() throws Exception {
        node.isDrafting();
        verify(exercise).isDrafting();
    }

    @Test
    public void setDrafting() throws Exception {
        node.setDrafting(true);
        verify(exercise).setDrafting(true);
    }

    @Test
    public void getProgramTrainingId() throws Exception {
        node.getProgramTrainingId();
        verify(exercise).getProgramTrainingId();
    }

    @Test
    public void setProgramTrainingId() throws Exception {
        node.setProgramTrainingId(1L);
        verify(exercise).setProgramTrainingId(1L);
    }

    @Test
    public void getExerciseId() throws Exception {
        node.setExerciseId(100L);
        verify(exercise).setExerciseId(100L);
    }

    @Test
    public void setExerciseId() throws Exception {
        node.getExerciseId();
        verify(exercise).getExerciseId();
    }
}