package ru.codingworkshop.gymm.data.tree.loader.builder;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 25.08.2017 as part of the Gymm project.
 */

public class ActualTrainingTreeBuilderTest {

    private ProgramTrainingTree programTrainingTree;

    @Before
    public void setUp() throws Exception {
        programTrainingTree = mock(ProgramTrainingTree.class);
        when(programTrainingTree.getParent()).thenReturn(Models.createProgramTraining(1L, "foo"));
        when(programTrainingTree.getChildren()).thenReturn(Models.createProgramExercises(1).stream().map(e -> {
            ImmutableProgramExerciseNode node = new ImmutableProgramExerciseNode(e);
            node.setChildren(Models.createProgramSets(2L, 1));
            node.setExercise(Models.createExercise(100L, "bar"));
            return node;
        }).collect(Collectors.toList()));
    }

    @Test
    public void build() throws Exception {
        ActualTrainingTree tree = (ActualTrainingTree) new ActualTrainingTreeBuilder(new ActualTrainingTree())
                .setProgramTrainingTree(programTrainingTree)
                .setParent(Models.createActualTraining(11L, 1L))
                .setChildren(Models.createActualExercises(12L))
                .setGrandchildren(Models.createActualSets(12L, 13L))
                .build();

        assertProgramTrainingTree(tree);

        assertEquals(11L, tree.getParent().getId());
        assertEquals(12L, tree.getChildren().get(0).getParent().getId());
        assertEquals(13L, tree.getChildren().get(0).getChildren().get(0).getId());
    }

    @Test
    public void buildEmpty() throws Exception {
        ActualTrainingTree tree = (ActualTrainingTree) new ActualTrainingTreeBuilder(new ActualTrainingTree())
                .setProgramTrainingTree(programTrainingTree)
                .build();

        assertProgramTrainingTree(tree);
        assertNull(tree.getParent());
        List<ActualExercise> collect = tree.getChildren().stream().map(BaseNode::getParent).collect(Collectors.toList());
        assertNull(collect.get(0));
        List<ActualSet> actualSets = tree.getChildren().get(0).getChildren();
        assertTrue(actualSets.isEmpty());
    }

    private void assertProgramTrainingTree(ActualTrainingTree tree) {
        assertEquals(1L, tree.getProgramTraining().getId());
        assertEquals(2L, tree.getChildren().get(0).getProgramExerciseNode().getParent().getId());
        assertEquals(3L, tree.getChildren().get(0).getProgramExerciseNode().getChildren().get(0).getId());
        assertEquals(100L, tree.getChildren().get(0).getProgramExerciseNode().getExercise().getId());
    }
}